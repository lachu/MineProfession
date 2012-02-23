package tw.lachu.MineProfession.profession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import tw.lachu.MineProfession.MineProfession;

public class Profession{
	
	private final String professionName;
	private final ConfigurationSection config;
	private MineProfession mp;
	
	public HashMap<String, HashMap<String,Double>> mapOfMaps;
	
	public Profession(MineProfession mp, String name, ConfigurationSection con0){
		this.mp = mp;
		this.professionName = name;
		this.config = con0;
		this.mapOfMaps = new HashMap<String, HashMap<String,Double>>();
	}
	
	public void load(){
		Set<String> depth1 = config.getKeys(false);
		for(String str1 : depth1){
			ConfigurationSection config1 = config.getConfigurationSection(str1);
			Set<String> depth2 = config1.getKeys(false);
			for(String str2 : depth2){
				ConfigurationSection config2 = config1.getConfigurationSection(str2);
				Set<String> depth3 = config2.getKeys(false);
				for(String str3 : depth3){
					try {
						HashMap<String, Double> map = mapOfMaps.get(str1+str2);
						if(map == null){
							map = new HashMap<String, Double>();
							mapOfMaps.put(str1+str2, map);
						}
						map.put(str3, Double.valueOf(config2.getDouble(str3)));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void onEvent(BlockBreakEvent event){
		gainExperience(event.getPlayer().getName(), event, event.getBlock().getType().name());
	}
	
	public void onEvent(PlayerShearEntityEvent event){
		gainExperience(event.getPlayer().getName(), event, event.getEntity().toString());
	}

	public void onEvent(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player){
			for(int i=event.getDamage();i>0;--i){
				gainExperience(((Player)event.getDamager()).getName(), event, event.getEntity().toString().split("\\{")[0]);
			}
		}
	}
	
	public void onEvent(EntityTameEvent event){
		if(event.getOwner() instanceof Player){
			gainExperience(((Player)event.getOwner()).getName(), event, event.getEntity().toString().split("\\{")[0]);
		}
	}
	
	public void onEvent(CreatureSpawnEvent event, Player player){
		gainExperience(player.getName(), event, event.getCreatureType().name());
	}

	public void onEvent(EnchantItemEvent event){
		Map<Enchantment, Integer> levelMap = event.getEnchantsToAdd();
		Map<String, Double> expMap = mapOfMaps.get("experienceEnchantItem");
		Set<Enchantment> set = levelMap.keySet();
		double exp = 0;
		for(Enchantment ench:set){
			exp += expMap.get(ench.getName()) * levelMap.get(ench);
		}
		exp *= expMap.get("CONST");
		
		String[] materialNames = event.getItem().getType().name().split("_");
		for(String material:materialNames){
			exp *= expMap.get(material);
		}
		mp.data.gainExperience(event.getEnchanter().getName(), this.professionName, exp);
	}
	
	public void gainExperience(String playerName, Event event, String key){
		String name = event.getEventName();
		name = name.substring(name.lastIndexOf('.')+1).replace("Event", "");
		try {
			
			HashMap<String, Double> map = mapOfMaps.get("experience"+name); 
			if(map!=null){
				Double gain = map.get(key);
				if(gain!=null){
					mp.data.gainExperience(playerName, this.professionName, gain);
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
}