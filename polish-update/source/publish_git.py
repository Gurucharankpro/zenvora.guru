"""Layer CI delivery files onto the latest branch without rebasing generated logs.

Concurrent documentation/publication commits are preserved. A stale runtime source
build may never overwrite a newer source tree. No checkout reset or force-push.
"""
import json
import os
from pathlib import Path
import subprocess
import tempfile

repo=Path(__file__).resolve().parents[2]
branch=os.environ['GITHUB_REF_NAME'];built=os.environ['GITHUB_SHA']
result=json.loads((repo/'polish-update/reports/CI-RESULT.json').read_text())
paths=['README.md','review','polish-update/reports','polish-update/resources','polish-update/content-spec.json','polish-update/visual-report.json','polish-update/RESOURCE-CHECKS.json']
if result['production_built']:paths.append('downloads/current')
files=[]
for name in paths:
 p=repo/name
 if p.is_dir():files.extend(f for f in p.rglob('*') if f.is_file() and '__pycache__' not in f.parts)
 elif p.is_file():files.append(p)
files=sorted(set(files))
if any(p.is_symlink() or '\n' in str(p) for p in files):raise ValueError('Invalid generated publication path')
guard=['native-review/qa','polish-update/source','polish-update/inputs','polish-update/art','polish-update/base']
for attempt in range(3):
 subprocess.run(['git','fetch','--no-tags','origin',branch],cwd=repo,check=True)
 parent=subprocess.check_output(['git','rev-parse','FETCH_HEAD'],cwd=repo,text=True).strip()
 changed=subprocess.run(['git','diff','--quiet',built,parent,'--',*guard],cwd=repo)
 if changed.returncode==1:
  print('Newer runtime/build source exists; this older run will not replace its delivery.')
  break
 if changed.returncode!=0:raise RuntimeError('Could not verify current source')
 with tempfile.TemporaryDirectory() as tmp:
  env=dict(os.environ,GIT_INDEX_FILE=str(Path(tmp)/'index'))
  def git(*args,**kwargs):return subprocess.run(['git',*args],cwd=repo,env=env,check=True,capture_output=True,**kwargs).stdout
  git('read-tree',parent)
  old=git('ls-tree','-r','--name-only',parent,'downloads/current').decode().splitlines()
  for path in old:git('update-index','--force-remove',path)
  hashes=git('hash-object','-w','--stdin-paths',input=('\n'.join(str(p) for p in files)+'\n').encode()).decode().splitlines()
  if len(hashes)!=len(files):raise RuntimeError('Generated publication manifest mismatch')
  git('update-index','-z','--index-info',input=b''.join(('100644 '+h+'\t'+p.relative_to(repo).as_posix()+'\0').encode() for p,h in zip(files,hashes)))
  tree=git('write-tree').decode().strip()
  message=f'Record verified content build {result["status"]} for {built}\n'
  commit=git('commit-tree',tree,'-p',parent,input=message.encode()).decode().strip()
  pushed=subprocess.run(['git','push','origin',commit+':refs/heads/'+branch],cwd=repo,env=env,text=True,capture_output=True)
  if pushed.returncode==0:print('Published complete CI delivery commit '+commit);break
  if 'non-fast-forward' not in pushed.stderr and 'fetch first' not in pushed.stderr:raise RuntimeError(pushed.stderr)
else:raise RuntimeError('Concurrent publication changed repeatedly; no force-push was attempted')
