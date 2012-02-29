package tw.lachu.MineProfession.profession;

import java.util.HashSet;

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

public class AbilityTrigger extends Trigger{
	Profession pro;
	private Double power;
	protected HashSet<String> types;
	
	public void load(Profession pro, ConfigurationSection config){
		this.pro = pro;
		types = new HashSet<String>(config.getStringList("types"));
		power = config.getDouble("power");
	}
	
	public double getPower(String playerName){
		return power*pro.mp.data.getProfessionPower(playerName, pro.profession);
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
