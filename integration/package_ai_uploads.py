"""Make three independently readable ZIPs below the AI's 20/50 MB limits.

The binary chunks reproduce the complete JAR; current decompiled Java is supplied
separately for review, not represented as an original buildable Forge project.
"""
import argparse
import hashlib
import json
from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED

CHUNK_BYTES = 14_000_000
FILE_LIMIT = 20_000_000
TOTAL_LIMIT = 50_000_000


def package(jar, output, reference):
    jar = jar.resolve()
    output.mkdir(parents=True, exist_ok=True)
    if list(output.glob('AI-Mod-*.zip')):
        raise FileExistsError('Use a fresh directory; existing archives are preserved')
    raw = jar.read_bytes()
    chunks = [raw[i:i + CHUNK_BYTES] for i in range(0, len(raw), CHUNK_BYTES)]
    if len(chunks) != 3:
        raise ValueError('This delivery requires exactly three chunks')
    names = [f'AI-Mod-1.8.1-part-{i:02}.zip' for i in range(1, 4)]
    manifest = {
        'format': 'independent-zips-with-ordered-raw-jar-chunks-v1',
        'jar_name': jar.name,
        'jar_bytes': len(raw),
        'jar_sha256': hashlib.sha256(raw).hexdigest(),
        'chunks': [dict(archive=name, entry=f'chunks/mod-{i:03}.bin',
                        bytes=len(chunk), sha256=hashlib.sha256(chunk).hexdigest())
                   for i, (name, chunk) in enumerate(zip(names, chunks), 1)],
    }
    source = Path(__file__).resolve().parent
    references = sorted(reference.rglob('*.java'))
    if not references:
        raise ValueError('Current JAR decompilation is required for the review bundle')
    guide = '''Soulbound Army 1.8.1 - AI upload bundle

Upload ONLY the three AI-Mod-1.8.1-part-*.zip files together. Each is an ordinary
independent ZIP, not a spanned archive. All three are needed for the full mod.
Part 01 includes the integration patch/test sources. Part 03 includes Java
decompiled from THIS fixed JAR by CFR 0.152 for reading. Decompiled references
are not an original, complete, compilable Forge source project.

To reconstruct, extract rebuild_ai_mod.py from any part next to all three ZIPs:
python rebuild_ai_mod.py
Or pass the three ZIP paths explicitly, optionally with --output /path/mod.jar.
Python 3 standard library only. Chunk hashes, full JAR SHA-256 and ZIP CRC are
verified. The binary .bin chunks are not individual installable mods.

No local Forge startup pass is claimed. Read TEST-REPORT.md before assessing
compatibility. A reviewer needs ZIP/Python tools for binary reconstruction;
an AI that only reads plain text can inspect the included Java source instead.
'''
    report = (output.parent / 'TEST-REPORT.md').read_bytes()
    review = (output.parent / 'AI-REVIEW.md').read_bytes()
    for index, (name, chunk, spec) in enumerate(zip(names, chunks, manifest['chunks'])):
        with ZipFile(output / name, 'w', ZIP_DEFLATED, compresslevel=6) as z:
            z.writestr('AI-MANIFEST.json', json.dumps(manifest, indent=2) + '\n')
            z.write(source / 'rebuild_ai_mod.py', 'rebuild_ai_mod.py')
            z.writestr('READ-ME.txt', guide)
            z.writestr('TEST-REPORT.md', report)
            z.writestr('AI-REVIEW.md', review)
            z.writestr(spec['entry'], chunk)
            if index == 0:
                for path in sorted(source.rglob('*')):
                    if path.is_file() and '__pycache__' not in path.parts and path.suffix in {'.java', '.py', '.sh', '.json', '.md'}:
                        z.write(path, 'integration/' + path.relative_to(source).as_posix())
            if index == 2:
                z.writestr('decompiled-reference/README.txt',
                           'CFR 0.152 output from the exact fixed 1.8.1 JAR.\n'
                           'Reference only; not original buildable Forge source.\n'
                           'JAR SHA-256: ' + manifest['jar_sha256'] + '\n')
                for path in references:
                    z.write(path, 'decompiled-reference/' + path.relative_to(reference).as_posix())
    measurements = []
    for name in names:
        path = output / name
        with ZipFile(path) as z:
            if z.testzip() is not None:
                raise ValueError('Archive CRC failure: ' + name)
            expanded = sum(entry.file_size for entry in z.infolist())
        size = path.stat().st_size
        if size >= FILE_LIMIT:
            raise ValueError('Individual upload exceeds limit: ' + name)
        measurements.append(dict(file=name, bytes=size, uncompressed_bytes=expanded,
                                 sha256=hashlib.sha256(path.read_bytes()).hexdigest()))
    total = sum(m['bytes'] for m in measurements)
    expanded_total = sum(m['uncompressed_bytes'] for m in measurements)
    if total >= TOTAL_LIMIT or expanded_total >= TOTAL_LIMIT:
        raise ValueError('Total upload or expanded size exceeds 50 MB')
    result = dict(files=measurements, total_bytes=total,
                  total_uncompressed_bytes=expanded_total,
                  per_file_limit=FILE_LIMIT, total_limit=TOTAL_LIMIT,
                  decompiled_java_files=len(references))
    (output / 'AI-MANIFEST.json').write_text(json.dumps(manifest, indent=2) + '\n')
    (output / 'UPLOAD-SIZES.json').write_text(json.dumps(result, indent=2) + '\n')
    (output / 'SHA256SUMS.txt').write_text(''.join(m['sha256'] + '  ' + m['file'] + '\n' for m in measurements))
    (output / 'READ-ME.txt').write_text(guide)
    print(json.dumps(result, indent=2))


if __name__ == '__main__':
    p = argparse.ArgumentParser()
    p.add_argument('jar', type=Path)
    p.add_argument('output', type=Path)
    p.add_argument('reference', type=Path)
    a = p.parse_args()
    package(a.jar, a.output, a.reference)
