package tw.lachu.MineProfession.profession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import tw.lachu.MineProfession.MineProfession;
import tw.lachu.MineProfession.util.Chance;
import tw.lachu.MineProfession.util.DoubleValue;

public class Profession{
	
	private final String professionName;
	private final ConfigurationSection config;
	private MineProfession mp;
	
	
	private HashMap<String, HashMap<String,Double>> mapOfExpMaps;
	private HashMap<String, List<String>> abilityMap;
	private HashMap<String, Double> abilityRatios;
	
	public Profession(MineProfession mp, String name, ConfigurationSection conf){
		this.mp = mp;
		this.professionName = name;
		this.config = conf;
		this.mapOfExpMaps = new HashMap<String, HashMap<String,Double>>();
		this.abilityMap = new HashMap<String, List<String>>();
		this.abilityRatios = new HashMap<String, Double>();
	}
	
	public void load(){
		//load experience gaining data
		ConfigurationSection expSection = config.getConfigurationSection("experience");
		Set<String> eventNames = expSection.getKeys(false);
		for(String eventName : eventNames){
			ConfigurationSection eventSection = expSection.getConfigurationSection(eventName);
			Set<String> eventVarNames = eventSection.getKeys(false);
			for(String eventVarName : eventVarNames){
				HashMap<String, Double> map = mapOfExpMaps.get("experience"+eventName);
				if(map == null){
					map = new HashMap<String,Double>();
					mapOfExpMaps.put("experience"+eventName, map);
				}
				map.put(eventVarName, eventSection.getDouble(eventVarName));
			}
		}

		//load ability data
		ConfigurationSection abilitySection = config.getConfigurationSection("abilities");
		Set<String> abilities = abilitySection.getKeys(false);
		for(String ability : abilities){
			if(ability.lastIndexOf("Ratio") == ability.length()-5){
				//mp.log.info("AbilityRatio: "+ability.substring(0,ability.lastIndexOf("Ratio")));
				abilityRatios.put(ability.substring(0,ability.lastIndexOf("Ratio")), abilitySection.getDouble(ability));
			}else{
				//mp.log.info("AbilityName: "+ability);
				abilityMap.put(ability, abilitySection.getStringList(ability));
			}
		}
	}
	
	public void onEvent(BlockBreakEvent event){
		gainExperience(event.getPlayer().getName(), event, event.getBlock().getType().name());
		
		if(abilityMap.get("BlockBreakFortune")!=null && abilityMap.get("BlockBreakFortune").contains(event.getBlock().getType().name())){
			Collection<ItemStack> drops = event.getBlock().getDrops(event.getPlayer().getItemInHand());
			for(ItemStack drop:drops){
				double expect = abilityRatios.get("BlockBreakFortune")*drop.getAmount()*mp.data.getProfessionPower(event.getPlayer().getName(), this.professionName);
				int max = (int)(Math.ceil(3*expect)+0.1);
				double probability = expect*6/max/(max+1)/(max+2);
				int happen = Chance.contribute(probability, max);
				if(happen>0){
					ItemStack bonus = new ItemStack(drop.getType(), happen);
					event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), bonus);
				}
			}
		}
	}
	
	public void onEvent(PlayerShearEntityEvent event){
		gainExperience(event.getPlayer().getName(), event, event.getEntity().toString());
		//if(abilityMap.get("PlayerShearSheepFortune")!=null) mp.log.info("a");
		//if(event.getEntity() instanceof Sheep) mp.log.info("b");
		if(abilityMap.get("PlayerShearSheepFortune")!=null && event.getEntity() instanceof Sheep){
			Sheep sheep  = (Sheep) event.getEntity();
			DyeColor color = sheep.getColor();
			Wool wool = new Wool(color);
			
			double expect = abilityRatios.get("PlayerShearSheepFortune")*mp.data.getProfessionPower(event.getPlayer().getName(), this.professionName);
			int max = (int)(Math.ceil(3*expect)+0.1);
			double probability = expect*6/max/(max+1)/(max+2);
			int happen = Chance.contribute(probability, max);
			//mp.log.info(expect+" "+max+" "+probability+" "+happen);
			if(happen>0){
				ItemStack bonus = wool.toItemStack(happen);
				event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), bonus);
			}
		}
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
	
	public void onEvent(EntityDamageEvent event, Player player){
		if(abilityMap.get("ProtectTamed")!=null){
			double ratio = abilityRatios.get("ProtectTamed")*mp.data.getProfessionPower(player.getName(), this.professionName);
			ratio = DoubleValue.bind(ratio, 0, 1);
			event.setDamage(event.getDamage()-(int)(event.getDamage()*ratio) );
		}
	}
	
	public void onEvent(CreatureSpawnEvent event, Player player){
		gainExperience(player.getName(), event, event.getCreatureType().name());
	}
	
	
	public void onEvent(EntityDeathEvent event, Player player){
		if(abilityMap.get("EntityDeathFortune")!=null && abilityMap.get("EntityDeathFortune").contains(event.getEntity().toString().split("\\{")[0])){
			List<ItemStack> drops = event.getDrops();
			for(ItemStack drop:drops){
				double expect = abilityRatios.get("EntityDeathFortune")*drop.getAmount()*mp.data.getProfessionPower(player.getName(), this.professionName);
				int max = ((int)(3*expect))+1;
				double probability = expect*6/max/(max+1)/(max+2);
				int happen = Chance.contribute(probability, max);
				if(happen>0){
					ItemStack bonus = new ItemStack(drop.getType(), happen);
					event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), bonus);
				}
			}
		}
	}

	public void onEvent(EnchantItemEvent event){
		Map<Enchantment, Integer> levelMap = event.getEnchantsToAdd();
		Map<String, Double> expMap = mapOfExpMaps.get("experienceEnchantItem");
		Set<Enchantment> enchSet = levelMap.keySet();
		double exp = 0;
		for(Enchantment ench:enchSet){
			exp += expMap.get(ench.getName()) * levelMap.get(ench);
		}
		exp *= expMap.get("CONST");
		
		String[] materialNames = event.getItem().getType().name().split("_");
		for(String material:materialNames){
			exp *= expMap.get(material);
		}
		mp.data.gainExperience(event.getEnchanter().getName(), this.professionName, exp);
		
		if(abilityMap.get("BetterEnchantment")!=null){
			double power = abilityRatios.get("BetterEnchantment")*mp.data.getProfessionPower(event.getEnchanter().getName(), this.professionName);
			double probability = power;
			for(Enchantment ench:enchSet){
				if(ench.getMaxLevel() > levelMap.get(ench)){
					if(Chance.happen(probability)){
						levelMap.put(ench, levelMap.get(ench)+1);
						event.getEnchanter().sendMessage(ChatColor.GOLD+ench.getName()+" level surprisingly increase to "+levelMap.get(ench)+".");
						probability /= 2;
					}
				}
			}
			if(Chance.happen(probability)){
				Enchantment[] enchants = Enchantment.values();
				ArrayList<Enchantment> enchs = new ArrayList<Enchantment>();
				for(Enchantment ench:enchants){
					if((levelMap.get(ench)==null || levelMap.get(ench).equals(0)) && ench.canEnchantItem(event.getItem())){
						enchs.add(ench);
					}
				}
				Enchantment toAdd = enchs.get((new Random()).nextInt(enchs.size()));
				levelMap.put(toAdd, ((new Random())).nextInt(toAdd.getMaxLevel()-1)+1);
			}
		}
	}
	
	public void gainExperience(String playerName, Event event, String key){
		String name = event.getEventName();
		name = name.substring(name.lastIndexOf('.')+1).replace("Event", "");
		try {
			
			HashMap<String, Double> map = mapOfExpMaps.get("experience"+name); 
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