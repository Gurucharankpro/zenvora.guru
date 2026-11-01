from pathlib import Path
from zipfile import ZipFile,ZIP_DEFLATED
import hashlib,json,os
R=Path(__file__).resolve().parents[1];out=R.parent/'dungeon-gates-downloads';out.mkdir(exist_ok=True)
jar=R/'dist/mods/dungeon-gates-1.20.1-0.1.0.jar';assert jar.exists()
with ZipFile(jar) as built:
 assert built.testzip() is None
 assert not any('/qa/' in n or n.startswith('net/minecraft/') for n in built.namelist())
 for n in built.namelist():
  if n.endswith('.class'):assert int.from_bytes(built.read(n)[6:8],'big')==61
jarsha=hashlib.sha256(jar.read_bytes()).hexdigest()
for name in ['PRODUCTION-104729.json','PRODUCTION-20261101.json','CLIENT.json']:
 proof=json.loads((R/'reports'/name).read_text())
 assert proof['passed'] and proof['source_commit']==os.environ.get('GITHUB_SHA') and proof['final_jar_sha256']==jarsha, name
(R/'reports/CI-RESULT.json').write_text(json.dumps({'status':'success','source_commit':os.environ.get('GITHUB_SHA'),'run_id':os.environ.get('GITHUB_RUN_ID'),'jar_sha256':jarsha,'production_normal_seeds_tested':2,'graphical_client_tested':True,'multiplayer_tested':False},indent=2)+'\n')
name='Dungeon_Gates_Forge_1.20.1_Complete_Mod.zip';p=out/name
with ZipFile(p,'w',ZIP_DEFLATED,compresslevel=9) as z:
 for folder in ['src','scripts','gradle','input','reports']:
  for f in sorted((R/folder).rglob('*')):
   if not f.is_file() or '__pycache__' in f.parts:continue
   z.write(f,'DungeonGates/'+f.relative_to(R).as_posix())
 for name in ['build.gradle','settings.gradle','gradle.properties','gradlew','gradlew.bat','README.md','INTEGRATION.md','.gitignore','DEVELOPMENT-PROMPT.txt','README-AR.md']:
  z.write(R/name,'DungeonGates/'+name)
 for f in (R/'dist/mods').glob('*.jar'):z.write(f,'mods/'+f.name)
with ZipFile(p) as z:
 assert z.testzip() is None
 assert z.read('mods/'+jar.name)==jar.read_bytes()
result={'status':'success','source_commit':os.environ.get('GITHUB_SHA'),'jar_sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'jar_bytes':jar.stat().st_size,'zip_sha256':hashlib.sha256(p.read_bytes()).hexdigest(),'zip_bytes':p.stat().st_size,'zip_crc_and_jar_equality':True,'minecraft':'1.20.1','forge':'47.3.22','java':17,'geckolib':'4.4.9','active_gates':2,'future_gates_pending_assets':4,'multiplayer_tested':False}
(out/'RESULT.json').write_text(json.dumps(result,indent=2)+'\n')
print(result)
