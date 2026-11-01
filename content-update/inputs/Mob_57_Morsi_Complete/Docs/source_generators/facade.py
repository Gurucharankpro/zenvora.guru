from PIL import Image, ImageDraw
import structure as S
import struct_export as E
def facade(path='Morsi_Mobile_Shop_Facade.png',sc=26):
    W=S.SX*sc; H=(S.SY)*sc
    im=Image.new('RGB',(W,H),(120,170,220)); d=ImageDraw.Draw(im)
    for y in range(S.SY):
        for x in range(S.SX):
            for z in range(0,S.SZ):
                key=(x,y,z)
                if key in S.B and S.B[key][0] not in('air',):
                    n,p,nb=S.B[key]
                    if n in E.NONSOLID and n not in('glass_pane',) : 
                        continue
                    c=E.color(n); px=x*sc; py=(S.SY-1-y)*sc
                    # viewer on street (z small) looks +z : viewer-left is +x -> mirror x
                    px=W-(x+1)*sc
                    if n.endswith('_slab'):
                        if p.get('type')=='top': d.rectangle([px,py,px+sc,py+sc//2],fill=c)
                        else: d.rectangle([px,py+sc//2,px+sc,py+sc],fill=c)
                    else: d.rectangle([px,py,px+sc,py+sc],fill=c)
                    break
    im.save(path)
facade()
