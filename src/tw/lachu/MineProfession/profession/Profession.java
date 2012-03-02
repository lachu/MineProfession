package tw.lachu.MineProfession.profession;

import java.util.ArrayList;
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

import tw.lachu.MineProfession.MineProfession;

public class Profession{
	
	protected final String profession;
	private final ConfigurationSection config;
	protected final MineProfession mp;
	
	private ArrayList<ExperienceTrigger> expList;
	private ArrayList<AbilityTrigger> abiList;
	
	public Profession(MineProfession mp, String name, ConfigurationSection conf){
		this.mp = mp;
		this.profession = name;
		this.config = conf;
		this.expList = new ArrayList<ExperienceTrigger>();
		this.abiList = new ArrayList<AbilityTrigger>();
	}
	
	public void load(){
		ConfigurationSection expSection = config.getConfigurationSection("experience");
		Set<String> expSources = expSection.getKeys(false);
		for(String source : expSources){
			try {
				Class<?> clazz = Class.forName("tw.lachu.MineProfession.profession."+source+"Experience");
				ExperienceTrigger trigger = (ExperienceTrigger)clazz.newInstance();
				trigger.load(this, expSection.getConfigurationSection(source));
				expList.add(trigger);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		ConfigurationSection abiSection = config.getConfigurationSection("abilities");
		Set<String> abilities = abiSection.getKeys(false);
		for(String ability : abilities){
			Class<?> clazz;
			try {
				clazz = Class.forName("tw.lachu.MineProfession.profession."+ability+"Ability");
				AbilityTrigger trigger = (AbilityTrigger)clazz.newInstance();
				trigger.load(this, abiSection.getConfigurationSection(ability));
				abiList.add(trigger);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}		
	}
	
	

	
	public void onEvent(BlockBreakEvent event){
		for(ExperienceTrigger trigger: expList){
			trigger.onEvent(event);
		}
		for(AbilityTrigger trigger: abiList){
			trigger.onEvent(event);
		}
	}
	public void onEvent(PlayerShearEntityEvent event){
		for(ExperienceTrigger trigger: expList){
			trigger.onEvent(event);
		}
		for(AbilityTrigger trigger: abiList){
			trigger.onEvent(event);
		}
	}
	public void onEvent(EntityDamageByEntityEvent event){
		for(ExperienceTrigger trigger: expList){
			trigger.onEvent(event);
		}
		for(AbilityTrigger trigger: abiList){
			trigger.onEvent(event);
		}
	}
	public void onEvent(EntityTameEvent event){
		for(ExperienceTrigger trigger: expList){
			trigger.onEvent(event);
		}
		for(AbilityTrigger trigger: abiList){
			trigger.onEvent(event);
		}
	}
	public void onEvent(EnchantItemEvent event){
		for(ExperienceTrigger trigger: expList){
			trigger.onEvent(event);
		}
		for(AbilityTrigger trigger: abiList){
			trigger.onEvent(event);
		}
	}

	
	public void onEvent(EntityDamageEvent event, Player player){
		for(ExperienceTrigger trigger: expList){
			trigger.onEvent(event, player);
		}
		for(AbilityTrigger trigger: abiList){
			trigger.onEvent(event, player);
		}
	}
	public void onEvent(CreatureSpawnEvent event, Player player){
		for(ExperienceTrigger trigger: expList){
			trigger.onEvent(event, player);
		}
		for(AbilityTrigger trigger: abiList){
			trigger.onEvent(event, player);
		}
	}
	public void onEvent(EntityDeathEvent event, Player player){
		for(ExperienceTrigger trigger: expList){
			trigger.onEvent(event, player);
		}
		for(AbilityTrigger trigger: abiList){
			trigger.onEvent(event, player);
		}
	}
}