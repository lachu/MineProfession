package tw.lachu.MineProfession.profession;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class ExperienceTrigger extends Trigger{
	Profession pro;
	protected HashMap<String, Double> amounts;
	
	public void load(Profession pro, ConfigurationSection config){
		this.pro = pro;
		Set<String> keys = config.getKeys(false);
		amounts = new HashMap<String, Double>();
		for(String key : keys){
			amounts.put(key, config.getDouble(key));
		}
	}
	
	public void onEvent(BlockBreakEvent event){
	}
	public void onEvent(PlayerShearEntityEvent event){
	}
	public void onEvent(EntityDamageByEntityEvent event){
	}
	public void onEvent(EntityTameEvent event){
	}
	public void onEvent(EnchantItemEvent event){
	}

	
	public void onEvent(EntityDamageEvent event, Player player){
	}
	public void onEvent(CreatureSpawnEvent event, Player player){
	}
	public void onEvent(EntityDeathEvent event, Player player){
	}
}
