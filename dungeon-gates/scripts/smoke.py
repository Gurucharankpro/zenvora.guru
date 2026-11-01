"""Actual final obfuscated server JAR tests on two fresh normal Overworld seeds; graphical source client separately."""
import os,sys,json,hashlib,shutil,subprocess,time,signal
from pathlib import Path
R=Path(__file__).resolve().parents[1];reports=R/'reports';reports.mkdir(exist_ok=True)
def main():
 dist=R/'dist';dist.mkdir(exist_ok=True)
 jar=R/'build/libs/dungeon-gates-1.20.1-0.1.0.jar';qa=R/'build/libs/dungeon-gates-1.20.1-0.1.0-qa.jar'
 abi=subprocess.check_output(['javap','-p','-c','-classpath',str(qa),'dev.dungeongates.qa.NativeQA'],text=True)
 for name in ['getMaxHealth','getAttributeValue','getType','save','blockPosition']:
  if 'DungeonMob.'+name+':' in abi:raise RuntimeError('QA inherited game method was not reobfuscated: '+name)
 gecko=next((Path.home()/'.gradle/caches/modules-2/files-2.1/software.bernie.geckolib/geckolib-forge-1.20.1/4.4.9').rglob('geckolib-forge-1.20.1-4.4.9.jar'))
 server=R/'build/isolated-server';server.mkdir(parents=True,exist_ok=True)
 installer=server/'installer.jar'
 subprocess.run(['curl','-fLsS','https://maven.minecraftforge.net/net/minecraftforge/forge/1.20.1-47.3.22/forge-1.20.1-47.3.22-installer.jar','-o',str(installer)],check=True)
 subprocess.run(['java','-jar',str(installer),'--installServer',str(server)],cwd=server,check=True,timeout=300)
 (server/'mods').mkdir(exist_ok=True)
 for f in [jar,qa,gecko]:shutil.copyfile(f,server/'mods'/f.name)
 (server/'eula.txt').write_text('eula=true\n');(server/'user_jvm_args.txt').write_text('-Xmx4G\n-Xms1G\n-Ddungeongates.qa=true\n')
 results=[]
 for seed in [104729,20261101]:
  (server/'server.properties').write_text(f'online-mode=false\nserver-ip=127.0.0.1\nserver-port=25579\nlevel-name=qa-{seed}\nlevel-seed={seed}\nlevel-type=minecraft:normal\nview-distance=2\nsimulation-distance=2\nspawn-protection=0\nmax-tick-time=240000\n')
  resultfile=server/'DGATES-NATIVE-RESULT.json';resultfile.unlink(missing_ok=True)
  log=reports/f'PRODUCTION-{seed}.log'
  with log.open('w') as out:
   p=subprocess.Popen(['bash','run.sh','nogui'],cwd=server,stdout=out,stderr=subprocess.STDOUT,start_new_session=True)
   try:p.wait(timeout=720)
   except subprocess.TimeoutExpired:os.killpg(p.pid,signal.SIGTERM);raise RuntimeError(f'Production seed {seed} timed out; see {log}')
  result=json.loads(resultfile.read_text()) if resultfile.exists() else {'passed':False,'failure':'No test completion marker'}
  result.update(source_commit=os.environ.get('GITHUB_SHA'),final_jar_sha256=hashlib.sha256(jar.read_bytes()).hexdigest());(reports/f'PRODUCTION-{seed}.json').write_text(json.dumps(result,indent=2)+'\n')
  if p.returncode or not result['passed'] or 'DGATES_NATIVE_PASS' not in log.read_text(errors='replace'):raise RuntimeError('Real production test failed: '+str(result))
  results.append(result)
 env=dict(os.environ,LIBGL_ALWAYS_SOFTWARE='true',ALSOFT_DRIVERS='null');run=R/'run-client';run.mkdir(exist_ok=True);(run/'options.txt').write_text('onboardAccessibility:false\ntutorialStep:none\npauseOnLostFocus:false\nnarrator:0\n')
 log=reports/'CLIENT.log'
 try:
  with log.open('w') as out:
   p=subprocess.Popen(['xvfb-run','-a','-s','-screen 0 1280x800x24','gradle','--no-daemon','--console=plain','-Dorg.gradle.jvmargs=-Xmx1500m','-PclientQA','runClient'],cwd=R,env=env,stdout=out,stderr=subprocess.STDOUT,start_new_session=True)
   try:p.wait(timeout=480)
   except subprocess.TimeoutExpired:os.killpg(p.pid,signal.SIGTERM);raise RuntimeError('Actual graphical test timed out')
  if p.returncode or 'DGATES_CLIENT_PASS:' not in log.read_text(errors='replace'):raise RuntimeError('Actual graphical client failed; see CLIENT.log')
  bad=[line for line in log.read_text(errors='replace').splitlines() if ('dungeongates' in line and any(s in line for s in ['Failed to load model','Unable to load model','Unable to load texture','Missing textures','Exception loading model'])) or 'Invalid rotation' in line]
  if bad:raise RuntimeError('Gameplay passed but resource loading failed: '+repr(bad))
 finally:
  if (run/'screenshots').exists():shutil.copytree(run/'screenshots',reports/'screenshots',dirs_exist_ok=True)
 from PIL import Image
 gallery=Image.open(reports/'screenshots/six-authored-creatures-and-trophies.png').convert('RGB');w,h=gallery.size;colored=[]
 for k in range(3):
  roi=gallery.crop((k*w//3,h//2+6,(k+1)*w//3,h-50))
  count=sum(1 for r,g,b in roi.getdata() if r+g+b>130)
  if count<=64:raise RuntimeError('Frozen model is still black/untextured, gallery column '+str(k))
  colored.append(count)
 client={'passed':True,'runtime':'native ForgeGradle source client at same commit, Xvfb/Mesa','both_gates_touch_entry_boss_death_return_trophies_save_reopen':True,'source_commit':os.environ.get('GITHUB_SHA'),'final_jar_sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'mobile_tested':False,'multiplayer_tested':False,'frozen_models_colored_pixel_counts':colored,'dungeongates_model_texture_errors':0}
 (reports/'CLIENT.json').write_text(json.dumps(client,indent=2)+'\n')
 (dist/'mods').mkdir(exist_ok=True);shutil.copyfile(jar,dist/'mods'/jar.name);shutil.copyfile(gecko,dist/'mods'/gecko.name)
 print('PASS: real final production JAR on two normal seeds and actual graphical client')
if __name__=='__main__':main()
