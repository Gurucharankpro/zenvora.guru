"""Static integration checks. These do not substitute for Forge world/client tests."""
import argparse
import gzip
import hashlib
import io
import json
import math
import re
import struct
import unittest
from pathlib import Path
from zipfile import ZipFile

import nbtlib


class IntegrationTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.resources=ARGS.resources
        cls.spec=json.loads((cls.resources/'integration-spec.json').read_text())
        with ZipFile(ARGS.jar) as z:cls.jar={n:z.read(n) for n in z.namelist()}
        with ZipFile(ARGS.base) as z:cls.base={n:z.read(n) for n in z.namelist() if not n.endswith('/')}
        cls.new_json={str(p.relative_to(cls.resources)):json.loads(p.read_text()) for p in cls.resources.rglob('*.json') if p.name!='integration-spec.json'}

    def test_catalogue_alignment_and_surviving_ids(self):
        profiles=self.spec['profiles']
        self.assertEqual([p['index'] for p in profiles],list(range(37,48)))
        self.assertEqual(len(set(p['id'] for p in profiles)),11)
        path='data/shadowsovereign/mythic_sites.json'
        old=json.loads(self.base[path]);new=json.loads(self.jar[path])
        old=[dict(p,kind=p['kind']-1 if p['kind']>34 else p['kind']) for p in old if p['kind']!=34]
        self.assertEqual(new[:len(old)],old)
        self.assertEqual(sorted(p['kind'] for p in new),list(range(-1,48)))

    def test_resources_were_packaged_without_loss(self):
        for path in self.resources.rglob('*'):
            if path.is_file() and path.name!='integration-spec.json':
                name=str(path.relative_to(self.resources))
                with self.subTest(resource=name):self.assertEqual(self.jar[name],path.read_bytes())

    def test_model_hierarchies_and_animation_tracks(self):
        for profile in self.spec['profiles']:
            d=json.loads(self.jar[f"assets/shadowsovereign/mythic/{profile['id']}.json"])
            bones=d['bones'];names=[b['name'] for b in bones]
            self.assertEqual(len(set(names)),len(names))
            for i,bone in enumerate(bones):
                seen={i};parent=bone['parent']
                while parent>=0:
                    self.assertLess(parent,len(bones));self.assertNotIn(parent,seen)
                    seen.add(parent);parent=bones[parent]['parent']
                for c in bone['cubes']:
                    self.assertTrue(all(a<b for a,b in zip(c['from'],c['to'])))
                    for uv in c['faces'].values():
                        self.assertTrue(all(0<=v<=d['width'] if i%2==0 else 0<=v<=d['height'] for i,v in enumerate(uv)))
            for clip,animation in d['animations'].items():
                self.assertGreater(animation['length'],0)
                for t in animation['tracks']:
                    self.assertTrue(0<=t['bone']<len(bones));self.assertIn(t['channel'],['rotation','position','scale'])
                    keys=t['keys'];self.assertEqual(keys,sorted(keys,key=lambda k:k[0]))
                    self.assertTrue(all(len(k)==4 and all(math.isfinite(v) for v in k) for k in keys))
                    self.assertTrue(all(0<=k[0]<=animation['length']+.001 for k in keys))
            for move in profile['moves']:
                self.assertIn(move[0],d['animations'])
                self.assertLess(move[1],d['animations'][move[0]]['length'])
            for key in ['idle','death','summoned_arrival',profile['walk'],profile['run']]:self.assertIn(key,d['animations'])

    def test_textures_and_glow_masks(self):
        for p in self.spec['profiles']:
            for suffix in ['','_glow']:
                data=self.jar[f"assets/shadowsovereign/textures/entity/{p['id']}{suffix}.png"]
                self.assertEqual(data[:8],b'\x89PNG\r\n\x1a\n')
                self.assertEqual(struct.unpack('>II',data[16:24]),(128,128))

    def test_explicit_transparent_block_render_layers(self):
        for b in self.spec['blocks']:
            if b['category'] in ('plant','aquatic','sapling','leaves','glass','decor'):
                model=json.loads(self.jar[f"assets/shadowsovereign/models/block/{b['id']}.json"])
                expected='minecraft:translucent' if b['category']=='glass' else 'minecraft:cutout_mipped' if b['category']=='leaves' else 'minecraft:'+b.get('render_layer','cutout')
                self.assertEqual(model.get('render_type'),expected,b['id'])

    def test_block_model_and_texture_references(self):
        def resource(value,kind):
            if ':' not in value:return
            namespace,path=value.split(':',1)
            if namespace=='minecraft':return
            suffix='.png' if kind=='textures' else '.json'
            self.assertIn(f'assets/{namespace}/{kind}/{path}{suffix}',self.jar,value)
        for path,d in self.new_json.items():
            if '/models/' in path:
                if 'parent' in d:resource(d['parent'],'models')
                for v in d.get('textures',{}).values():
                    if not v.startswith('#'):resource(v,'textures')
            if '/blockstates/' in path:
                for variants in d.get('variants',{}).values():
                    for v in variants if isinstance(variants,list) else [variants]:resource(v['model'],'models')

    def test_dimension_biomes_and_features_resolve(self):
        d=json.loads(self.jar['data/shadowsovereign/dimension/echo_realms.json'])
        for entry in d['generator']['biome_source']['biomes']:
            namespace,id=entry['biome'].split(':')
            b=json.loads(self.jar[f'data/{namespace}/worldgen/biome/{id}.json'])
            for stage in b['features']:
                for feature in stage:
                    ns,f=feature.split(':')
                    if ns=='minecraft':continue
                    self.assertIn(f'data/{ns}/worldgen/placed_feature/{f}.json',self.jar)
        for path,d in self.new_json.items():
            if '/placed_feature/' in path:
                namespace,id=d['feature'].split(':');self.assertIn(f'data/{namespace}/worldgen/configured_feature/{id}.json',self.jar)

    def test_templates_palettes_loot_and_bounds(self):
        registered={f"shadowsovereign:{b['id']}" for b in self.spec['blocks']}
        for name,content in self.jar.items():
            if not name.startswith('data/shadowsovereign/structures/expansion/'):continue
            n=nbtlib.File.parse(io.BytesIO(gzip.decompress(content)))
            self.assertEqual(int(n['DataVersion']),3465);size=list(map(int,n['size']))
            for state in n['palette']:
                id=str(state['Name'])
                if not id.startswith('minecraft:'):self.assertIn(id,registered)
                self.assertNotEqual(id,'minecraft:structure_block')
            self.assertFalse(n['entities'])
            for block in n['blocks']:
                self.assertTrue(0<=int(block['state'])<len(n['palette']))
                self.assertTrue(all(0<=int(v)<bound for v,bound in zip(block['pos'],size)))
                loot=str(block.get('nbt',{}).get('LootTable',''))
                entity=block.get('nbt',{})
                if str(entity.get('id',''))=='minecraft:banner':
                    self.assertNotIn('patterns',entity)
                    for pattern in entity.get('Patterns',[]):
                        self.assertTrue(0<=int(pattern['Color'])<=15)
                        self.assertIn(str(pattern['Pattern']),['mr','mc','cr','bo'])
                if loot and not loot.startswith('minecraft:'):
                    ns,id=loot.split(':');self.assertIn(f'data/{ns}/loot_tables/{id}.json',self.jar)
                for stack in entity.get('Items',[]):
                    self.assertNotIn('count',stack)
                    self.assertIsInstance(stack['Count'],nbtlib.Byte)
                    self.assertTrue(1<=int(stack['Count'])<=127)

    def test_structure_worldgen_links_and_spawn_clearance(self):
        for id in self.spec['structures']:
            root='data/shadowsovereign/worldgen/'
            s=json.loads(self.jar[root+'structure/'+id+'.json'])
            pool=s['start_pool'].split(':')[1]
            p=json.loads(self.jar[root+'template_pool/'+pool+'.json'])
            template=p['elements'][0]['element']['location'].split(':')[1]
            self.assertIn(f'data/shadowsovereign/structures/{template}.nbt',self.jar)
            ss=json.loads(self.jar[root+'structure_set/'+id+'.json'])
            self.assertLess(ss['placement']['separation'],ss['placement']['spacing'])
        sites=json.loads(self.jar['data/shadowsovereign/mythic_sites.json'])[-11:]
        passable={'minecraft:air','minecraft:structure_void','minecraft:spore_blossom'}
        for site,profile in zip(sites,self.spec['profiles']):
            structure=site['structure'].removeprefix('expansion_')
            n=nbtlib.File.parse(io.BytesIO(gzip.decompress(self.jar[f'data/shadowsovereign/structures/expansion/{structure}.nbt'])))
            blocks={tuple(map(int,b['pos'])):str(n['palette'][int(b['state'])]['Name']) for b in n['blocks']}
            allowed=passable|({'minecraft:water'} if profile['aquatic'] else set())
            x,y,z=site['altar'];found=False
            # Conservative full-body empty-cell box in the same 6-block radius
            # used by native MythicSpawn. Actual collision shapes need the game.
            half=math.ceil((profile['width']-1)/2);height=math.ceil(profile['height'])
            size=list(map(int,n['size']))
            for dx in range(-6,7):
                for dz in range(-6,7):
                    for dy in [0,1,-1,2,-2,3,-3]:
                        X,Y,Z=x+dx,y+dy,z+dz;floor=(X,Y-1,Z)
                        if X-half<0 or X+half>=size[0] or Z-half<0 or Z+half>=size[2] or Y<0 or Y+height>size[1]:continue
                        if blocks.get(floor,'minecraft:air') in allowed:continue
                        if all(blocks.get((X+a,Y+h,Z+c),'minecraft:air') in allowed for a in range(-half,half+1) for c in range(-half,half+1) for h in range(height)):found=True
            self.assertTrue(found,profile['id'])

    def test_localization_and_registry_assets(self):
        for locale in ['en_us','ar_sa']:
            d=json.loads(self.jar[f'assets/shadowsovereign/lang/{locale}.json'])
            for p in self.spec['profiles']:
                for key in ['entity.shadowsovereign.','grimoire.shadowsovereign.lore.','grimoire.shadowsovereign.abilities.']:self.assertIn(key+p['id'],d)
            for block in self.spec['blocks']:
                id=block['id'];self.assertIn('block.shadowsovereign.'+id,d)
                for path in ['blockstates/'+id,'models/block/'+id,'models/item/'+id]:self.assertIn(f'assets/shadowsovereign/{path}.json',self.jar)

    def test_preserved_original_classes_and_protocol_change(self):
        patched={'dev/shadowsovereign/'+p+'.class' for p in ['mythic/MythicProfiles','mythic/MythicExpedition','gloom/GloomContent','mythic/MythicSpawn','mythic/MythicBeast','network/ShadowNetwork','mythic/MythicService','mythic/MythicEvents','gloom/GloomEvents','expanse/ExpanseWorld','ShadowSovereign','events/ShadowEvents','mythic/MythicContent','mythic/MythicSites','mythic/LegendCombat','client/MythicBeastRenderer','discovery/AtlasService','mastery/MasteryCatalog','army/ArmyData','adventure/AdventureData']}
        for name,content in self.base.items():
            if name.startswith('dev/shadowsovereign/knighthaven/'):continue
            if name.endswith('.class') and name not in patched:self.assertEqual(self.jar[name],content,name)
        self.assertTrue(b'33-royal-relics' in self.jar['dev/shadowsovereign/network/ShadowNetwork.class'],'Wrong client/server protocol')

    def test_knight_content_removed_and_other_temples_preserved(self):
        retired=('villager_knight','village_knight_lodge','knighthaven_villages')
        for name,raw in self.jar.items():
            self.assertFalse(any(s in name for s in retired),name)
            self.assertFalse(name.startswith('dev/shadowsovereign/knighthaven/'),name)
            if name.endswith('.json'):
                self.assertFalse(any(s.encode() in raw for s in retired),name)
        for name in self.spec['removed_resources']:self.assertNotIn(name,self.jar)
        self.assertIn('dev/shadowsovereign/expansion/CrystalCombat.class',self.jar)
        for name in self.base:
            if '/knighthaven_' in name and not any(s in name for s in retired):self.assertIn(name,self.jar)

    def test_all_eggs_have_unique_authored_art(self):
        eggs=self.spec['eggs'];self.assertEqual(len(eggs),54)
        actual={Path(n).stem for n in self.jar if n.startswith('assets/shadowsovereign/models/item/') and n.endswith('_spawn_egg.json')}
        self.assertEqual(set(eggs),actual)
        cells=[]
        for id,entry in eggs.items():
            d=json.loads(self.jar[f'assets/shadowsovereign/models/item/{id}.json'])
            self.assertNotEqual(d.get('parent'),'minecraft:item/template_spawn_egg')
            if 'uv' in entry:
                cells.append(tuple(entry['uv']))
                for face in d['elements'][0]['faces'].values():
                    self.assertNotIn('tintindex',face)
                    self.assertTrue(all(0<=v<=16 for v in face['uv']))
                self.assertEqual(d['elements'][0]['faces']['north']['uv'],entry['uv'])
            texture=entry['texture']
            raw=self.jar[f'assets/shadowsovereign/textures/item/{texture}.png']
            self.assertEqual(raw[25],6) # PNG RGBA, original generated transparency
        self.assertEqual(len(cells),52);self.assertEqual(len(set(cells)),52)

    def test_amin_loot_survives_120_conversion(self):
        raw=self.jar['data/shadowsovereign/structures/expansion/amin_el_nemr_workshop.nbt']
        n=nbtlib.File.parse(io.BytesIO(gzip.decompress(raw)))
        chests=[b['nbt'] for b in n['blocks'] if str(b.get('nbt',{}).get('id',''))=='minecraft:chest']
        self.assertEqual(len(chests),9)
        self.assertEqual(sum(len(c.get('Items',[])) for c in chests),42)

    def test_ocean_density_is_limited_to_sunken_climate(self):
        path='data/shadowsovereign/worldgen/noise_settings/echo_expanses.json'
        old=json.loads(self.base[path])['noise_router'];new=json.loads(self.jar[path])['noise_router']
        biomes=json.loads(self.jar['data/shadowsovereign/dimension/echo_realms.json'])['generator']['biome_source']['biomes']
        def distance(v,span):
            lo,hi=span if isinstance(span,list) else (span,span)
            return max(lo-v,0,v-hi)**2
        for key in ['final_density','initial_density_without_jaggedness']:
            outer=new[key];inner=outer['when_in_range']
            self.assertEqual(outer['input'],new['temperature'])
            self.assertEqual((outer['min_inclusive'],outer['max_exclusive']),(-.4,.4))
            self.assertEqual(outer['when_out_of_range'],old[key])
            self.assertEqual(inner['when_out_of_range'],old[key])
            self.assertEqual(inner['input'],new['vegetation'])
            self.assertEqual(inner['min_inclusive'],1.15)
        for temp in [-.39,-.2,0,.2,.39]:
            for humidity in [1.15,1.3,1.6,1.8]:
                sample=dict(temperature=temp,humidity=humidity,continentalness=0,erosion=0,depth=0,weirdness=0)
                nearest=min(biomes,key=lambda b:sum(distance(v,b['parameters'][k]) for k,v in sample.items())+b['parameters']['offset']**2)
                self.assertEqual(nearest['biome'],'shadowsovereign:sunken_kingdom')

    def test_decorations_registered_and_placed(self):
        self.assertEqual(len(self.spec['blocks']),100)
        self.assertEqual(len(self.spec['decor_ids']),60)
        self.assertEqual({p['id'] for p in self.spec['decor_placements']},set(self.spec['decor_ids']))
        for placement in self.spec['decor_placements']:
            n=nbtlib.File.parse(io.BytesIO(gzip.decompress(self.jar[f"data/shadowsovereign/structures/expansion/{placement['structure']}.nbt"])))
            matches=[b for b in n['blocks'] if list(map(int,b['pos']))==placement['position']]
            self.assertEqual(len(matches),1)
            self.assertEqual(str(n['palette'][int(matches[0]['state'])]['Name']),'shadowsovereign:'+placement['id'])

    def test_no_newer_or_invented_vanilla_block_ids(self):
        unsupported=set(self.spec['vanilla_1_20_remaps'])
        for name,content in self.jar.items():
            if name.startswith('data/shadowsovereign/structures/expansion/'):
                n=nbtlib.File.parse(io.BytesIO(gzip.decompress(content)))
                self.assertTrue(unsupported.isdisjoint(str(s['Name']) for s in n['palette']),name)

    def test_new_decor_models_use_native_anchor_states(self):
        self.assertEqual({k:len(v) for k,v in self.spec['decor_packs'].items()},dict(mob1=20,hollow_reaper=20,soul_devourer=20))
        self.assertEqual(len(set(self.spec['decor_ids'])),60)
        for b in self.spec['blocks']:
            if 'pack' not in b:continue
            id=b['id'];d=json.loads(self.jar[f'assets/shadowsovereign/models/block/{id}.json'])
            state=json.loads(self.jar[f'assets/shadowsovereign/blockstates/{id}.json'])
            self.assertEqual(set(state['variants']),{f'facing={v}' for v in ['north','east','south','west']})
            for e in d['elements']:
                self.assertTrue(all(-16<=v<=32 and math.isfinite(v) for v in e['from']+e['to']))
                self.assertIn(e.get('rotation',{}).get('angle',0),[-45,-22.5,0,22.5,45])
                for face in e['faces'].values():self.assertTrue(all(0<=v<=16 for v in face['uv']))
            for suffix in ['', '_e']:
                raw=self.jar[f'assets/shadowsovereign/textures/block/{id}{suffix}.png']
                self.assertEqual(struct.unpack('>II',raw[16:24]),(64,64))
            self.assertTrue(0<=b['light']<=15)
            loot=json.loads(self.jar[f'data/shadowsovereign/loot_tables/blocks/{id}.json'])
            self.assertEqual(loot['pools'][0]['entries'],[{'type':'minecraft:item','name':'shadowsovereign:'+id}])

    def test_new_decor_ruins_have_support_and_visual_clearance(self):
        props={b['id']:b for b in self.spec['blocks'] if 'pack' in b}
        for structure in ['hollow_reaper_reliquary','devourer_relic_sanctum']:
            n=nbtlib.File.parse(io.BytesIO(gzip.decompress(self.jar[f'data/shadowsovereign/structures/expansion/{structure}.nbt'])))
            blocks={tuple(map(int,b['pos'])):str(n['palette'][int(b['state'])]['Name']) for b in n['blocks']}
            placements=[p for p in self.spec['decor_placements'] if p['structure']==structure]
            self.assertEqual(len(placements),20)
            for p in placements:
                b=props[p['id']];x,y,z=p['position'];mount=b['mount']
                support=(x,y+1,z) if mount=='ceiling' else (x,y,z+1) if mount=='wall' else (x,y-1,z)
                self.assertNotEqual(blocks[support],'minecraft:air',p['id'])
                lo,hi=b['visual_bounds']
                # Exclude deliberate contact with supporting floor/ceiling/wall;
                # negative subpixel base details are authored to meet the floor.
                for X in range(math.floor(x+lo[0]/16+.00001),math.ceil(x+hi[0]/16-.00001)):
                    for Y in range(max(y,math.floor(y+lo[1]/16+.00001)),math.ceil(y+hi[1]/16-.00001)):
                        for Z in range(math.floor(z+lo[2]/16+.00001),math.ceil(z+hi[2]/16-.00001)):
                            if (X,Y,Z)==(x,y,z):continue
                            self.assertEqual(blocks.get((X,Y,Z)),'minecraft:air',(p['id'],(X,Y,Z)))
            for X in range(10,15):
                for Y in range(1,5):self.assertEqual(blocks[(X,Y,0)],'minecraft:air')

    def test_king_effect_bones_are_hidden_during_locomotion(self):
        path='assets/shadowsovereign/mythic/king_of_corny_memes.json'
        d=json.loads(self.jar[path]);self.assertEqual(len(d['bones']),61)
        self.assertEqual(sum(len(b['cubes']) for b in d['bones']),117)
        self.assertEqual(hashlib.sha256(self.jar['assets/shadowsovereign/textures/entity/king_of_corny_memes_glow.png']).hexdigest(),'7f0d11be8e7576758e90916f9970058fea35529cac6dd29d145334bfbaecc651')
        effects={i for i,b in enumerate(d['bones']) if b['name'].startswith('fx_')}
        self.assertTrue(effects)
        for clip in ['idle','walk','run']:
            scales={t['bone']:t['keys'] for t in d['animations'][clip]['tracks'] if t['channel']=='scale'}
            for bone in effects:
                self.assertIn(bone,scales)
                self.assertTrue(all(max(k[1:])<=.001 for k in scales[bone]),(clip,bone))
        for p in self.spec['profiles']:
            if p['id']=='king_of_corny_memes':self.assertEqual(len(p['moves']),5)

    def test_king_palace_chests_and_spawn_registry(self):
        n=nbtlib.File.parse(io.BytesIO(gzip.decompress(self.jar['data/shadowsovereign/structures/expansion/palace_of_eternal_cringe.nbt'])))
        self.assertEqual(list(map(int,n['size'])),[55,38,55])
        chests=[b['nbt'] for b in n['blocks'] if str(b.get('nbt',{}).get('id',''))=='minecraft:chest']
        self.assertEqual(len(chests),5)
        self.assertTrue(all(str(c['LootTable'])=='minecraft:chests/end_city_treasure' for c in chests))
        sites=json.loads(self.jar['data/shadowsovereign/mythic_sites.json'])
        self.assertEqual(sites[-1],dict(kind=47,structure='expansion_palace_of_eternal_cringe',altar=[27,6,33],rootOffset=[0,0,0]))

    def test_previous_release_content_preserved(self):
        if ARGS.previous is None:self.skipTest('Previous candidate JAR not supplied')
        with ZipFile(ARGS.previous) as z:
            for name in z.namelist():
                if name.endswith('/') or name.endswith('.class'):continue
                self.assertIn(name,self.jar,name)
                # Catalogues/locales/metadata are extended; old artwork,
                # templates, worldgen data and item definitions stay identical.
                if name.startswith(('assets/shadowsovereign/lang/','data/minecraft/tags/')) or name in ['META-INF/mods.toml','data/shadowsovereign/mythic_sites.json']:continue
                self.assertEqual(z.read(name),self.jar[name],name)


if __name__=='__main__':
    p=argparse.ArgumentParser();p.add_argument('--resources',type=Path,required=True);p.add_argument('--jar',type=Path,required=True);p.add_argument('--base',type=Path,required=True);p.add_argument('--previous',type=Path)
    ARGS=p.parse_args();unittest.main(argv=['verify.py'],verbosity=2)
