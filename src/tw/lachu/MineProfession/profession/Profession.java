package tw.lachu.MineProfession.profession;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
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
	
	private final String proName;
	private final ConfigurationSection con0;
	private MineProfession mp;
	
	public HashMap<String, HashMap<String,Double>> maps;
	
	public Profession(MineProfession mp, String name, ConfigurationSection con0){
		this.mp = mp;
		this.proName = name;
		this.con0 = con0;
		this.maps = new HashMap<String, HashMap<String,Double>>();
	}
	
	public void load(){
		Set<String> depth1 = con0.getKeys(false);
		for(String str1 : depth1){
			ConfigurationSection con1 = con0.getConfigurationSection(str1);
			Set<String> depth2 = con1.getKeys(false);
			for(String str2 : depth2){
				ConfigurationSection con2 = con1.getConfigurationSection(str2);
				Set<String> depth3 = con2.getKeys(false);
				for(String str3 : depth3){
					try {
						HashMap<String, Double> map = maps.get(str1+str2);
						if(map == null){
							map = new HashMap<String, Double>();
							maps.put(str1+str2, map);
						}
						map.put(str3, Double.valueOf(con2.getDouble(str3)));
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
		
	}
	
	public void gainExperience(String playerName, Event event, String key){
		String name = event.getEventName();
		name = name.substring(name.lastIndexOf('.')+1).replace("Event", "");
		try {
			
			HashMap<String, Double> map = maps.get("experience"+name); 
			if(map!=null){
				Double gain = map.get(key);
				if(gain!=null){
					mp.data.gainExperience(playerName, this.proName, gain);
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
}