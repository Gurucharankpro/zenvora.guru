"""Actual graphical client against a ForgeGradle-remapped copy of the final JAR.
Separate from the production-obfuscated dedicated-server check.
"""
import hashlib,json,os,shutil,signal,subprocess,time
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];repo=ROOT.parent
def main():
 reports=ROOT/'reports';reports.mkdir(exist_ok=True)
 if not shutil.which('xvfb-run'):
  (reports/'CLIENT-SMOKE.json').write_text(json.dumps({'status':'unavailable','reason':'xvfb-run unavailable','graphical_client_tested':False})+'\n');return
 native=repo/'native-review/qa';local=native/'localrepo/local/soulbound/army/1.8.1-polished-check';local.mkdir(parents=True,exist_ok=True)
 jar=ROOT/'dist/soulbound-army-1.20.1-1.8.1-polished.jar';shutil.copyfile(jar,local/'army-1.8.1-polished-check.jar')
 (local/'army-1.8.1-polished-check.pom').write_text('<project><modelVersion>4.0.0</modelVersion><groupId>local.soulbound</groupId><artifactId>army</artifactId><version>1.8.1-polished-check</version></project>\n')
 env=dict(os.environ,LIBGL_ALWAYS_SOFTWARE='true',ALSOFT_DRIVERS='null');log=reports/'CLIENT-SMOKE.log'
 clientdir=native/'run-client';clientdir.mkdir(exist_ok=True)
 (clientdir/'options.txt').write_text('onboardAccessibility:false\ntutorialStep:none\npauseOnLostFocus:false\nnarrator:0\n')
 command=['xvfb-run','-a','-s','-screen 0 1280x800x24','gradle','--no-daemon','--console=plain','--max-workers=2','-Dorg.gradle.jvmargs=-Xmx1500m','-PqaArtifact=1.8.1-polished-check','-Psmoke','-PpolishClient','runClient']
 with log.open('w') as out:
  p=subprocess.Popen(command,cwd=native,env=env,stdout=out,stderr=subprocess.STDOUT,start_new_session=True)
  try:p.wait(timeout=420)
  except subprocess.TimeoutExpired:
   screenshots=native/'run-client/screenshots'
   if screenshots.exists():shutil.copytree(screenshots,reports/'screenshots',dirs_exist_ok=True)
   os.killpg(p.pid,signal.SIGTERM);p.wait(timeout=20);raise TimeoutError('Graphical test timed out; see CLIENT-SMOKE.log and actual stalled-screen screenshots')
 text=log.read_text(errors='replace')
 if p.returncode!=0 or '181 NATIVE CLIENT PASS:' not in text:raise RuntimeError('Native graphical client check did not pass; see CLIENT-SMOKE.log')
 screenshots=native/'run-client/screenshots'
 if not screenshots.is_dir() or len(list(screenshots.glob('*.png')))<4:raise RuntimeError('Missing actual rendered screenshots')
 shutil.copytree(screenshots,reports/'screenshots',dirs_exist_ok=True)
 result={'status':'passed','graphical_client_tested':True,'final_input_sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'runtime':'ForgeGradle remaps final production JAR to official dev mappings; Xvfb/Mesa software rendering','actual_menu_book_summon_save_reopen':True,'latest_three_legends_and_jerboa_rendered':True,'screenshots':[p.name for p in screenshots.glob('*.png')],'mobile':False,'multiplayer':False}
 (reports/'CLIENT-SMOKE.json').write_text(json.dumps(result,indent=2)+'\n');print('PASS: actual native graphical client',result)
if __name__=='__main__':main()
