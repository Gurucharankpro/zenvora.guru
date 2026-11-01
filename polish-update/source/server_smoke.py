"""Launch the final obfuscated distribution in an isolated official Forge server.
Also include a separate QA mod for real registry/template/entity checks.
"""
import gzip,io,json,os,re,shutil,subprocess,sys,time,zlib
import nbtlib
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];repo=ROOT.parent
def main():
 server=ROOT/'build/production-server';server.mkdir(parents=True,exist_ok=True)
 installer=ROOT/'build/tools/forge-installer.jar'
 subprocess.run(['curl','--fail','--location','--silent','--show-error','https://maven.minecraftforge.net/net/minecraftforge/forge/1.20.1-47.3.22/forge-1.20.1-47.3.22-installer.jar','-o',str(installer)],check=True)
 subprocess.run(['java','-jar',str(installer),'--installServer',str(server)],cwd=server,check=True,timeout=300)
 (server/'mods').mkdir(exist_ok=True)
 shutil.copyfile(ROOT/'dist/soulbound-army-1.20.1-1.8.1-polished.jar',server/'mods/soulbound.jar')
 shutil.copyfile(repo/'native-review/qa/build/libs/soulbound-181-qa-1.0.jar',server/'mods/soulboundqa.jar')
 (server/'eula.txt').write_text('eula=true\n')
 (server/'server.properties').write_text('online-mode=false\nserver-ip=127.0.0.1\nserver-port=25579\nlevel-name=polish-isolated-test\nlevel-type=minecraft:flat\ngenerator-settings={"layers":[{"block":"minecraft:bedrock","height":1},{"block":"minecraft:dirt","height":2},{"block":"minecraft:grass_block","height":1}],"biome":"minecraft:plains","features":false,"lakes":false,"structure_overrides":[]}\nview-distance=2\nsimulation-distance=2\nspawn-protection=0\nmax-tick-time=180000\n')
 (server/'user_jvm_args.txt').write_text('-Xms1G\n-Xmx3G\n-Dsoulboundqa.polish=true\n')
 logfile=server/'console.log'
 with logfile.open('w') as out:
  p=subprocess.Popen(['bash','run.sh','nogui'],cwd=server,stdin=subprocess.PIPE,stdout=out,stderr=subprocess.STDOUT,text=True)
  def command(s):p.stdin.write(s+'\n');p.stdin.flush()
  def until(predicate,seconds):
   deadline=time.monotonic()+seconds
   while time.monotonic()<deadline:
    text=logfile.read_text(errors='replace')
    if predicate(text):return text
    if p.poll() is not None:raise RuntimeError('Production server exited before required check: '+text[-5000:])
    time.sleep(1)
   raise TimeoutError(logfile.read_text(errors='replace')[-5000:])
  try:
   until(lambda t:'Done (' in t and 'SOULBOUND_POLISH_NATIVE_PASS' in t,240)
   command('execute in shadowsovereign:echo_realms run locate biome shadowsovereign:singing_glass_desert')
   text=until(lambda t:re.search(r'nearest shadowsovereign:singing_glass_desert is at',t)!=None,180)
   match=re.search(r'nearest shadowsovereign:singing_glass_desert is at \[(-?\d+),[^,]+,\s*(-?\d+)\]',text)
   if not match:raise RuntimeError('Could not parse actual biome location')
   x,z=map(int,match.groups());command(f'execute in shadowsovereign:echo_realms run forceload add {x} {z} {x+16} {z+16}')
   until(lambda t:'Marked' in t and 'force load' in t,120)
   # Let generated chunk tasks finish, then flush every test dimension to disk.
   time.sleep(12);command('save-all flush');until(lambda t:'Saved the game' in t,120)
   command('stop');p.wait(timeout=120)
   if p.returncode!=0:raise RuntimeError('Production server exit '+str(p.returncode))
   text=logfile.read_text(errors='replace')
   forbidden=['NoSuchMethodError','NoSuchFieldError','BootstrapMethodError','VerifyError','Exception in server tick loop','Failed to load datapacks','Parsing error loading custom advancement','Error loading registry data','Unknown registry key']
   failures=[s for s in forbidden if s in text]
   if failures:raise RuntimeError('Final production smoke found '+repr(failures))
   generated=[];surface=False;biome=False
   region=server/'polish-isolated-test/dimensions/shadowsovereign/echo_realms/region'
   for cx in [x//16,x//16+1]:
    for cz in [z//16,z//16+1]:
     raw=(region/f'r.{cx//32}.{cz//32}.mca').read_bytes();slot=4*((cx%32)+(cz%32)*32);offset=int.from_bytes(raw[slot:slot+3],'big')*4096
     if offset==0:raise AssertionError('Requested chunk was never saved')
     length=int.from_bytes(raw[offset:offset+4],'big');compression=raw[offset+4];payload=raw[offset+5:offset+4+length]
     decoded=zlib.decompress(payload) if compression==2 else gzip.decompress(payload) if compression==1 else payload
     chunk=nbtlib.File.parse(io.BytesIO(decoded));status=str(chunk['Status'])
     if status not in ['full','minecraft:full']:raise AssertionError('Chunk not fully generated: '+status)
     for section in chunk.get('sections',[]):
      biome|=any(str(b)=='shadowsovereign:singing_glass_desert' for b in section.get('biomes',{}).get('palette',[]))
      surface|=any(str(b['Name']) in ['shadowsovereign:violet_dune_sand','shadowsovereign:golden_glass_sand'] for b in section.get('block_states',{}).get('palette',[]))
     generated.append([cx,cz])
   if not biome or not surface:raise AssertionError('Saved chunks lack actual desert biome/surface blocks')
   result=json.loads((server/'POLISH-RUNTIME.json').read_text());result.update(production_server_started=True,isolated_save_flushed=True,biome_located=[x,z],biome_chunks_requested=4,graphical_client=False,multiplayer=False)
   result.update(saved_full_chunks=generated,actual_desert_biome_and_surface_blocks=True)
   (ROOT/'reports').mkdir(exist_ok=True);(ROOT/'reports/PRODUCTION-SMOKE.json').write_text(json.dumps(result,indent=2)+'\n')
   shutil.copyfile(logfile,ROOT/'reports/PRODUCTION-SERVER.log');print('PASS: real Forge final-JAR startup, native checks, biome locate/generation and save/stop',result)
  finally:
   if p.poll() is None:p.terminate();p.wait(timeout=20)
   (ROOT/'reports').mkdir(exist_ok=True);shutil.copyfile(logfile,ROOT/'reports/PRODUCTION-SERVER.log')
if __name__=='__main__':main()
