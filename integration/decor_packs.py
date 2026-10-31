"""Import the two supplied decorative packs as native static display props.

Use their complete legal Java models with one facing anchor block per item.
Do not import 1.21 item definitions or half-state files into a one-anchor block.
The themed generated ruins provide clearance and floor/wall/ceiling supports.
"""
import argparse
import copy
import gzip
import io
import json
import math
import re
from pathlib import Path
from zipfile import ZipFile

import nbtlib

NS='shadowsovereign'
REAPER_AR=['تمثال الحاصد ذي الأذرع الأربع','عرش الحاصد','مذبح حصاد الأرواح','نصب المنجل العملاق',
           'فانوس الروح الجوفاء','شمعدان الأيدي الأربع','تابوت الحاصد','مسلة الروح المقيدة',
           'الساعة الرملية الملعونة','صليب القبر الطيفي','جرة جامع الأرواح','ثريا المناجل المعلقة',
           'راية الحاصد الجدارية','قوس العظام القديم','جرس الجنازة المنسي','مرآة الروح',
           'منصة كتاب الحاصد','نافورة الحداد الأبدي','وعاء الأرواح العائم','ساعة الحصاد الأخير']
DEVOURER_AR=['نصب ملتهم الأرواح','قفص سجن الأرواح العظيم','عرش الملتهم','طوطم الحارس سداسي الأرجل',
             'مذبح أرواح الهاوية','فانوس الروح المحبوسة','قوس فك الملتهم','مسلة استخراج الأرواح',
             'جمجمة الوحش القديمة','ثريا المخالب الستة','بلورة النجمة المسجونة','راية حرب الملتهم',
             'تابوت قفص الأرواح','عمود العمود الفقري السحيق','حوض التغذية المنسي','حامل سلاسل الأرواح',
             'موقد طقوس الملتهم','أثر قدم الوحش الملعون','بوابة السجن الأبدي','قلب الملتهم']
DEVOURER_LIGHT=[6,9,5,5,10,13,5,8,4,14,12,0,5,6,4,2,14,0,3,12]


def geometry_bounds(model):
    points=[]
    for e in model['elements']:
        rotation=e.get('rotation',{});angle=rotation.get('angle',0)
        assert angle in (-45,-22.5,0,22.5,45)
        assert all(-16<=v<=32 for v in e['from']+e['to'])
        assert all(a<b for a,b in zip(e['from'],e['to']))
        for face in e['faces'].values():
            assert all(math.isfinite(v) and 0<=v<=16 for v in face['uv'])
        # Include both handedness conventions conservatively for reserved space.
        for sign in [-1,1]:
            for x in [e['from'][0],e['to'][0]]:
                for y in [e['from'][1],e['to'][1]]:
                    for z in [e['from'][2],e['to'][2]]:
                        p=[x,y,z]
                        if angle:
                            axis='xyz'.index(rotation['axis']);a=(axis+1)%3;b=(axis+2)%3
                            origin=rotation['origin'];u=p[a]-origin[a];v=p[b]-origin[b]
                            theta=math.radians(angle*sign);factor=1/math.cos(theta) if rotation.get('rescale') else 1
                            p[a]=origin[a]+(u*math.cos(theta)-v*math.sin(theta))*factor
                            p[b]=origin[b]+(u*math.sin(theta)+v*math.cos(theta))*factor
                        points.append(p)
    return [[min(p[a] for p in points) for a in range(3)],
            [max(p[a] for p in points) for a in range(3)]]


def build(inputs,out,base_path):
    spec=json.loads((out/'integration-spec.json').read_text())
    def write(path,value):
        p=out/path;p.parent.mkdir(parents=True,exist_ok=True)
        p.write_bytes(value if isinstance(value,bytes) else (json.dumps(value,ensure_ascii=False,indent=2)+'\n').encode())
    langs={loc:json.loads((out/f'assets/{NS}/lang/{loc}.json').read_text()) for loc in ['en_us','ar_sa']}
    packs={'mob1':list(spec['decor_ids']),'hollow_reaper':[],'soul_devourer':[]}
    new=[]
    def register(id,model,texture,glow,title,ar,light,layer,mount,pack):
        assert not any(b['id']==id for b in spec['blocks']),id
        assert texture.read_bytes()[:8]==b'\x89PNG\r\n\x1a\n'
        model['render_type']='minecraft:'+layer
        bounds=geometry_bounds(model)
        write(f'assets/{NS}/models/block/{id}.json',model)
        write(f'assets/{NS}/models/item/{id}.json',{'parent':f'{NS}:block/{id}'})
        write(f'assets/{NS}/blockstates/{id}.json',{'variants':{
            f'facing={direction}':{'model':f'{NS}:block/{id}','y':angle}
            for direction,angle in [('north',0),('east',90),('south',180),('west',270)]}})
        write(f'assets/{NS}/textures/block/{id}.png',texture.read_bytes())
        write(f'assets/{NS}/textures/block/{id}_e.png',glow.read_bytes())
        write(f'data/{NS}/loot_tables/blocks/{id}.json',{'type':'minecraft:block','pools':[{
            'rolls':1,'entries':[{'type':'minecraft:item','name':f'{NS}:{id}'}],
            'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
        b=dict(id=id,category='decor',light=light,hardness=1.5,render_layer=layer,
               visual_bounds=bounds,mount=mount,pack=pack)
        spec['blocks'].append(b);spec['decor_ids'].append(id);packs[pack].append(id);new.append(b)
        langs['en_us']['block.shadowsovereign.'+id]=title
        langs['ar_sa']['block.shadowsovereign.'+id]=ar
    manifest=next(inputs.glob('Decor_Pack_02_Hollow_Reaper/**/Documentation/registry_manifest.json'))
    root=manifest.parent.parent
    for e in sorted(json.loads(manifest.read_text()),key=lambda e:e['number']):
        original=e['id'];id='reaper_'+original
        model=json.loads((root/f'Models/Block_JSON/{original}.json').read_text().replace(f'soulbound_decor:block/{original}',f'{NS}:block/{id}'))
        mount='ceiling' if 'ceiling' in e['placement'] else 'wall' if e['placement']=='wall' else 'floor'
        register(id,model,root/f'Textures/{original}.png',root/f'Textures/Emissive/{original}_emissive.png',
                 e['title'],REAPER_AR[e['number']-1],e['suggested_light_level'],e['render_type'],mount,'hollow_reaper')
    root=inputs/'Decor_Pack_03_Soul_Devourer/assets/soul_bound'
    for p in sorted((root/'models/block/decor').glob('*.json')):
        original=p.stem;number=int(original.split('_')[1]);short=re.sub(r'^decor_\d+_','',original);id='devourer_'+short
        model=json.loads(p.read_text().replace(f'soul_bound:block/decor/{original}',f'{NS}:block/{id}'))
        mount='ceiling' if number in (6,10,11) else 'wall' if number in (12,16) else 'floor'
        layer='translucent' if number in (2,5,6,20) else 'cutout'
        register(id,model,root/f'textures/block/decor/{original}.png',root/f'textures/block/decor/{original}_emissive.png',
                 short.replace('_',' ').title(),DEVOURER_AR[number-1],DEVOURER_LIGHT[number-1],layer,mount,'soul_devourer')
    assert all(len(v)==20 for v in packs.values())

    salts=json.loads(Path(__file__).with_name('structure_salts.json').read_text())
    def gallery(pack,name,biome):
        size=[25,9,27];cells={}
        stone='minecraft:polished_deepslate' if pack=='hollow_reaper' else 'minecraft:polished_blackstone'
        def put(pos,id,properties=None):
            state={'Name':id}
            if properties:state['Properties']=properties
            cells[tuple(pos)]=state
        # Air is explicit, so the generated galleries do not inherit terrain
        # inside their reserved visual volumes. Entrances remain open.
        for x in range(size[0]):
            for z in range(size[2]):
                for y in range(size[1]):put((x,y,z),'minecraft:air')
                put((x,0,z),stone);put((x,7,z),stone)
                if x in (0,24) or z in (0,26):
                    for y in range(1,7):
                        if z==0 and 10<=x<=14 and y<=4:continue
                        put((x,y,z),stone if y in (1,6) or x%6==0 or z%5==0 else 'minecraft:purple_stained_glass')
        for index,b in enumerate(v for v in new if v['pack']==pack):
            row,col=divmod(index,4);x,z=3+col*6,3+row*5;y=6 if b['mount']=='ceiling' else 1
            if b['mount']=='wall':
                for height in range(1,5):put((x,height,z+1),stone)
            assert cells[(x,y,z)]['Name']=='minecraft:air'
            put((x,y,z),f"{NS}:{b['id']}",{'facing':'north'})
            spec['decor_placements'].append(dict(id=b['id'],structure=name,position=[x,y,z],mount=b['mount']))
        palette=[];indices={};blocks=[]
        for pos,state in sorted(cells.items()):
            key=json.dumps(state,sort_keys=True)
            if key not in indices:indices[key]=len(palette);palette.append(nbtlib.Compound({
                'Name':nbtlib.String(state['Name']),**({'Properties':nbtlib.Compound({k:nbtlib.String(v) for k,v in state['Properties'].items()})} if 'Properties' in state else {})}))
            blocks.append(nbtlib.Compound({'pos':nbtlib.List[nbtlib.Int](pos),'state':nbtlib.Int(indices[key])}))
        n=nbtlib.File({'DataVersion':nbtlib.Int(3465),'size':nbtlib.List[nbtlib.Int](size),
                       'palette':nbtlib.List[nbtlib.Compound](palette),'blocks':nbtlib.List[nbtlib.Compound](blocks),
                       'entities':nbtlib.List[nbtlib.Compound]()})
        buf=io.BytesIO();n.write(buf);write(f'data/{NS}/structures/expansion/{name}.nbt',gzip.compress(buf.getvalue(),mtime=0))
        sid='expansion_'+name
        with ZipFile(base_path) as base:
            def old(kind):return json.loads(base.read(f'data/{NS}/worldgen/{kind}/shattered_gentlemans_manor.json'))
            st=old('structure');st.update(biomes=f'{NS}:{biome}',start_pool=f'{NS}:{sid}',start_height={'absolute':-1})
            pool=old('template_pool');pool['name']=f'{NS}:{sid}';pool['elements'][0]['element']['location']=f'{NS}:expansion/{name}'
            ss=old('structure_set');ss['structures'][0]['structure']=f'{NS}:{sid}';ss['placement'].update(salt=salts[name],spacing=40,separation=16)
            for kind,value in [('structure',st),('template_pool',pool),('structure_set',ss)]:write(f'data/{NS}/worldgen/{kind}/{sid}.json',value)
        spec['structures'].append(sid)
    gallery('hollow_reaper','hollow_reaper_reliquary','eternal_abyss')
    gallery('soul_devourer','devourer_relic_sanctum','gloomwood')
    spec['decor_packs']=packs
    for loc,d in langs.items():write(f'assets/{NS}/lang/{loc}.json',d)
    write('integration-spec.json',spec)
    print('Imported 40 native decorative props with two supported, spaced themed ruins.')


if __name__=='__main__':
    p=argparse.ArgumentParser();p.add_argument('--inputs',type=Path,required=True);p.add_argument('--out',type=Path,required=True);p.add_argument('--base',type=Path,required=True)
    a=p.parse_args();build(a.inputs,a.out,a.base)
