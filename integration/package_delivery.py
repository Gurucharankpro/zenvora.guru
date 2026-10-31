"""Create standard Info-ZIP split volumes and validate their complete payload.

For verification only, normalize disk-relative directory offsets in memory.
The delivered volumes remain the original standard split-ZIP output.
"""
import argparse
import hashlib
import io
from pathlib import Path
import struct
import subprocess
from zipfile import ZipFile


def package(jar, output):
    jar=jar.resolve();output=output.resolve();output.mkdir(parents=True,exist_ok=True)
    archive=output/'Soulbound-Army-1.3.0.zip'
    if archive.exists() or list(output.glob('Soulbound-Army-1.3.0.z[0-9]*')):
        raise FileExistsError('Use an empty delivery directory; existing volumes are preserved.')
    subprocess.run(['zip','-6','-s','10m',str(archive),jar.name],cwd=jar.parent,check=True)
    volumes=sorted(output.glob('Soulbound-Army-1.3.0.z[0-9]*'))+[archive]
    sizes=[v.stat().st_size for v in volumes]
    merged=bytearray(b''.join(v.read_bytes() for v in volumes))
    eocd=merged.rfind(b'PK\x05\x06')
    if eocd<0:raise ValueError('Missing end-of-central-directory record')
    sig,disk,cdisk,count,total,csize,coffset,comment=struct.unpack_from('<4s4H2IH',merged,eocd)
    if disk!=len(volumes)-1 or cdisk!=disk or count!=1 or total!=1 or comment!=0:
        raise ValueError('Unexpected split archive layout')
    absolute=sum(sizes[:cdisk])+coffset
    if merged[absolute:absolute+4]!=b'PK\x01\x02' or absolute+csize!=eocd:
        raise ValueError('Invalid central directory bounds')
    startdisk=struct.unpack_from('<H',merged,absolute+34)[0]
    localoffset=struct.unpack_from('<I',merged,absolute+42)[0]
    if startdisk!=0 or localoffset!=4 or merged[:8]!=b'PK\x07\x08PK\x03\x04':
        raise ValueError('Unexpected split marker/local-header location')
    struct.pack_into('<4s4H2IH',merged,eocd,sig,0,0,1,1,csize,absolute,0)
    digest=hashlib.sha256(jar.read_bytes()).hexdigest()
    with ZipFile(io.BytesIO(merged)) as z:
        if z.namelist()!=[jar.name] or z.testzip() is not None:
            raise ValueError('Archive member or CRC mismatch')
        if hashlib.sha256(z.read(jar.name)).hexdigest()!=digest:
            raise ValueError('Reassembled JAR hash mismatch')
    lines=[hashlib.sha256(v.read_bytes()).hexdigest()+'  '+v.name for v in volumes]
    (output/'SHA256SUMS.txt').write_text('\n'.join(lines)+'\n')
    print(f'PASS: {len(volumes)} standard split ZIP volumes, CRC and reassembled JAR SHA-256 verified.')
    print('JAR SHA-256: '+digest)
    for v,size in zip(volumes,sizes):print(f'{v.name}: {size} bytes')


if __name__=='__main__':
    p=argparse.ArgumentParser();p.add_argument('jar',type=Path);p.add_argument('output',type=Path)
    args=p.parse_args();package(args.jar,args.output)
