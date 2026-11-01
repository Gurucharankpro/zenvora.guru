"""Retire the village knight, compact resource indices and install generated egg art.

The generated PNGs are copied unchanged. Egg models select UV regions in the
shared atlas; no raster editing/cropping is performed by this importer.
"""
import argparse,copy,json,struct
from pathlib import Path
from zipfile import ZipFile

REMOVED=('villager_knight','village_knight_lodge','knighthaven_villages')

def removed_path(path):
    return any(word in path for word in REMOVED) or path=='KNIGHT-CREDITS.txt' or '/sounds/knighthaven/knight/' in path

def transform_json(data):
    if isinstance(data,list):
        return [transform_json(x) for x in data if not (isinstance(x,dict) and x.get('kind')==34)]
    if isinstance(data,dict):
        out={}
        for key,value in data.items():
            if any(s in key for s in REMOVED) or key.startswith(('knighthaven.shadowsovereign.','knighthaven_knight_')):continue
            if key=='kind' and isinstance(value,int) and value>34:value-=1
            out[key]=transform_json(value)
        return out
    return data

def egg_model(uv):
    u,v,U,V=uv
    return {'gui_light':'front','ambientocclusion':False,
            'textures':{'egg':'shadowsovereign:item/legendary_egg_atlas','particle':'shadowsovereign:item/legendary_egg_atlas'},
            'elements':[{'from':[0,0,7.5],'to':[16,16,8.5],
                         'faces':{'north':{'uv':[u,v,U,V],'texture':'#egg'},
                                  'south':{'uv':[U,v,u,V],'texture':'#egg'}}}],
            'display':{'gui':{'rotation':[0,0,0],'translation':[0,0,0],'scale':[1,1,1]},
                       'ground':{'rotation':[0,0,0],'translation':[0,2,0],'scale':[.5,.5,.5]},
                       'fixed':{'rotation':[0,180,0],'translation':[0,0,0],'scale':[1,1,1]},
                       'thirdperson_righthand':{'rotation':[0,0,0],'translation':[0,3,1],'scale':[.55,.55,.55]},
                       'thirdperson_lefthand':{'rotation':[0,0,0],'translation':[0,3,1],'scale':[.55,.55,.55]},
                       'firstperson_righthand':{'rotation':[0,-90,25],'translation':[1.13,3.2,1.13],'scale':[.68,.68,.68]},
                       'firstperson_lefthand':{'rotation':[0,90,-25],'translation':[1.13,3.2,1.13],'scale':[.68,.68,.68]}}}

def main(base_path,out):
    spec=json.loads((out/'integration-spec.json').read_text())
    def write(name,value):
        target=out/name;target.parent.mkdir(parents=True,exist_ok=True)
        target.write_bytes(value if isinstance(value,bytes) else (json.dumps(value,ensure_ascii=False,indent=2)+'\n').encode())
    deleted=[];processed=set()
    with ZipFile(base_path) as base:
        for name in base.namelist():
            if name.endswith('/'):continue
            if removed_path(name):deleted.append(name);continue
            if name.endswith('.json'):
                processed.add(name)
                raw=(out/name).read_bytes() if (out/name).exists() else base.read(name)
                data=json.loads(raw);changed=transform_json(data)
                if data!=changed:write(name,changed)
        for p in list(out.rglob('*.json')):
            if p.name=='integration-spec.json':continue
            name=str(p.relative_to(out))
            if removed_path(name):p.unlink();continue
            if name in processed:continue
            data=json.loads(p.read_text());changed=transform_json(data)
            if data!=changed:write(name,changed)
        # Resource transforms run only once; profile indices are explicitly
        # compacted here to match removing slot 34 from the original initializer.
        for profile in spec['profiles']:profile['index']-=1
        eggs={Path(n).stem for n in base.namelist() if n.startswith('assets/shadowsovereign/models/item/') and n.endswith('_spawn_egg.json') and not removed_path(n)}
        eggs.update(p['id']+'_spawn_egg' for p in spec['profiles'])
        standalone={'amin_el_nemr_spawn_egg':'amin-egg.png','king_of_corny_memes_spawn_egg':'king-egg.png','the_69_pose_spawn_egg':'acrobat-egg.png'}
        paired={'hanan_spawn_egg':[0,0,8,16],'glass_ronin_spawn_egg':[8,0,16,16]}
        atlas_eggs=sorted(eggs-set(standalone)-set(paired))
        # The existing atlas order must never shift when another egg is added.
        assert len(atlas_eggs)==52 and len(eggs)==57
        art=Path(__file__).parent/'art'
        write('assets/shadowsovereign/textures/item/legendary_egg_atlas.png',(art/'egg-atlas.png').read_bytes())
        mapping={}
        write('assets/shadowsovereign/textures/item/hanan_ronin_eggs.png',(art/'hanan-ronin-eggs.png').read_bytes())
        for id,uv in paired.items():
            model=egg_model(uv)
            model['textures']={k:'shadowsovereign:item/hanan_ronin_eggs' for k in model['textures']}
            # Square atlas has two narrow cells; geometry restores the egg aspect.
            model['elements'][0]['from']=[4,0,7.5];model['elements'][0]['to']=[12,16,8.5]
            write(f'assets/shadowsovereign/models/item/{id}.json',model)
            mapping[id]={'texture':'hanan_ronin_eggs','paired_uv':uv}
        for index,id in enumerate(atlas_eggs):
            row,col=divmod(index,8);uv=[col*2,row*2,(col+1)*2,(row+1)*2]
            write(f'assets/shadowsovereign/models/item/{id}.json',egg_model(uv))
            mapping[id]={'cell':index,'uv':uv,'texture':'legendary_egg_atlas'}
        for id,filename in standalone.items():
            write(f'assets/shadowsovereign/textures/item/{id}.png',(art/filename).read_bytes())
            write(f'assets/shadowsovereign/models/item/{id}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'shadowsovereign:item/'+id}})
            mapping[id]={'texture':id}
    # Match ocean-floor density changes to a climate band where the new ocean
    # is actually the closest biome, avoiding lowering old humid inland biomes.
    path='data/shadowsovereign/worldgen/noise_settings/echo_expanses.json'
    noise=json.loads((out/path).read_text())
    for key in ['final_density','initial_density_without_jaggedness']:
        choice=noise['noise_router'][key]
        baseline=copy.deepcopy(choice['when_out_of_range'])
        choice['min_inclusive']=1.15
        noise['noise_router'][key]={'type':'minecraft:range_choice','input':copy.deepcopy(noise['noise_router']['temperature']),
                                  'min_inclusive':-.4,'max_exclusive':.4,'when_in_range':choice,'when_out_of_range':baseline}
    write(path,noise)
    spec.update(removed_resources=deleted,removed_profile_index=34,original_profile_count=37,
                eggs=mapping,profile_count=51,format_version='1.8.1')
    write('integration-spec.json',spec)
    print('Prepared knight removal, compacted profile IDs and 57 unique egg designs.')

if __name__=='__main__':
    parser=argparse.ArgumentParser();parser.add_argument('--base',type=Path,required=True);parser.add_argument('--out',type=Path,required=True)
    args=parser.parse_args();main(args.base,args.out)
