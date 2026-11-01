execute store result score #active sbx1_count if entity @e[type=minecraft:item_display,tag=sb_x1_fx]
execute if score #active sbx1_count matches ..23 run summon minecraft:item_display ~ ~ ~ {"Tags":["sb_x1_fx","sb_x1_new","sbx1_e08_medium"],"billboard":"fixed","item_display":"none","brightness":{"block":15,"sky":15},"view_range":0.75,"shadow_radius":0.0,"shadow_strength":0.0,"Invulnerable":1b,"item":{"id":"shadowsovereign:x1_visual","Count":1,"tag":{"CustomModelData":211}},"transformation":{"translation":[0.0,0.035,0.0],"scale":[2.8,1.0,2.8],"left_rotation":[0.0,0.0,0.0,1.0],"right_rotation":[0.0,0.0,0.0,1.0]}}
scoreboard players set @e[type=minecraft:item_display,tag=sb_x1_new] sbx1_age 0
tag @e[type=minecraft:item_display,tag=sb_x1_new] remove sb_x1_new
scoreboard players add #active sbx1_count 1
