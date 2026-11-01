"""1.20.1 PlayerTrigger LOCATION consumes the player context predicate.
Legacy conditions.location is silently ignored by that trigger.
"""
import copy
def fix_location_criteria(advancement):
 d=copy.deepcopy(advancement);count=0
 for criterion in d.get('criteria',{}).values():
  conditions=criterion.get('conditions',{})
  if criterion.get('trigger')!='minecraft:location' or not isinstance(conditions.get('location'),dict):continue
  location=conditions.pop('location');player=conditions.get('player',[])
  if isinstance(player,dict):player=[{'condition':'minecraft:entity_properties','entity':'this','predicate':player}]
  if not isinstance(player,list):raise ValueError('Unexpected existing player predicate')
  conditions['player']=[*player,{'condition':'minecraft:location_check','predicate':location}];count+=1
 return d,count
