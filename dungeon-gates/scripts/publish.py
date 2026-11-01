from pathlib import Path
import subprocess,tempfile,os,json
root=Path(__file__).resolve().parents[2];project=root/'dungeon-gates';reports=project/'reports'
status=os.environ['BUILD_OUTCOME'];(reports/'CI-RESULT.json').write_text(json.dumps({'status':status,'source_commit':os.environ['GITHUB_SHA'],'run_id':os.environ['GITHUB_RUN_ID']},indent=2)+'\n')
files=[p for p in reports.rglob('*') if p.is_file()]
if status=='success':files += [p for p in (root/'dungeon-gates-downloads').rglob('*') if p.is_file()]
branch='dungeon-gates-forge-1.20.1'
subprocess.run(['git','config','user.name','Dungeon Gates Build'],cwd=root,check=True);subprocess.run(['git','config','user.email','dungeon-gates-build@users.noreply.github.com'],cwd=root,check=True)
subprocess.run(['git','fetch','origin',branch],cwd=root,check=True)
parent=subprocess.check_output(['git','rev-parse','FETCH_HEAD'],cwd=root,text=True).strip()
if subprocess.run(['git','diff','--quiet',os.environ['GITHUB_SHA'],parent,'--','dungeon-gates/src','dungeon-gates/scripts','dungeon-gates/build.gradle','.github/workflows/dungeon-gates.yml'],cwd=root).returncode:raise RuntimeError('Newer remote source exists; refusing stale publication')
with tempfile.TemporaryDirectory() as tmp:
 env=dict(os.environ,GIT_INDEX_FILE=tmp+'/index')
 def git(*args,**kw):return subprocess.check_output(['git',*args],cwd=root,env=env,**kw)
 git('read-tree',parent)
 old=git('ls-files','-z','--','dungeon-gates/reports','dungeon-gates-downloads')
 if old:git('update-index','--force-remove','-z','--stdin',input=old)
 for f in files:
  h=git('hash-object','-w',str(f)).decode().strip();git('update-index','--add','--cacheinfo','100644,'+h+','+f.relative_to(root).as_posix())
 tree=git('write-tree').decode().strip();commit=git('commit-tree',tree,'-p',parent,input=('Publish Dungeon Gates '+status+' evidence\n').encode()).decode().strip()
 subprocess.run(['git','push','origin',commit+':refs/heads/'+branch],cwd=root,check=True)
 print('Published',commit,status)
