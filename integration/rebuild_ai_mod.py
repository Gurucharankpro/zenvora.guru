"""Rebuild the byte-identical mod from the three independent AI upload ZIPs.
Usage: python rebuild_ai_mod.py [--output mod.jar] [part1.zip part2.zip part3.zip]
Only manifest-selected chunk entries are read; no archive paths are extracted.
"""
import argparse,hashlib,json
from pathlib import Path
from zipfile import ZipFile

def rebuild(archives,output=None):
    docs=[];pieces={}
    for archive in archives:
        with ZipFile(archive) as z:
            manifest=json.loads(z.read('AI-MANIFEST.json'));docs.append(manifest)
            for chunk in manifest['chunks']:
                if chunk['entry'] not in z.namelist():continue
                raw=z.read(chunk['entry'])
                if len(raw)!=chunk['bytes'] or hashlib.sha256(raw).hexdigest()!=chunk['sha256']:
                    raise ValueError('Chunk integrity failure: '+chunk['entry'])
                if chunk['entry'] in pieces:raise ValueError('Duplicate input chunk: '+chunk['entry'])
                pieces[chunk['entry']]=raw
    if not docs or any(d!=docs[0] for d in docs):raise ValueError('Missing or inconsistent manifests')
    manifest=docs[0]
    if set(pieces)!={c['entry'] for c in manifest['chunks']}:raise ValueError('All three parts are required')
    data=b''.join(pieces[c['entry']] for c in manifest['chunks'])
    if len(data)!=manifest['jar_bytes'] or hashlib.sha256(data).hexdigest()!=manifest['jar_sha256']:
        raise ValueError('Complete JAR integrity failure')
    output=Path(output or Path(__file__).resolve().parent/Path(manifest['jar_name']).name)
    if output.exists() and output.read_bytes()!=data:raise FileExistsError('Refusing to replace '+str(output))
    output.parent.mkdir(parents=True,exist_ok=True);output.write_bytes(data)
    with ZipFile(output) as z:
        bad=z.testzip()
        if bad:raise ValueError('JAR CRC failure: '+bad)
    print('PASS: exact JAR reconstructed; SHA256 '+manifest['jar_sha256'])
    print(output)
    return output

if __name__=='__main__':
    p=argparse.ArgumentParser();p.add_argument('--output',type=Path);p.add_argument('archives',type=Path,nargs='*')
    args=p.parse_args();archives=args.archives or sorted(Path(__file__).resolve().parent.glob('AI-Mod-1.8.1-part-*.zip'))
    rebuild(archives,args.output)
