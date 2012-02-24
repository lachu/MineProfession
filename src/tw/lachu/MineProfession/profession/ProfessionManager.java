package tw.lachu.MineProfession.profession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import tw.lachu.MineProfession.MineProfession;


public class ProfessionManager implements Listener{
	
	private static List<String> humanKill;
	{
		humanKill = new ArrayList<String>();
		humanKill.add("CUSTOM");
		humanKill.add("ENTITY_ATTACK");
		humanKill.add("PROJECTILE");
	}
	
	private HashSet<UUID> spawnerChild = new HashSet<UUID>();
	
	private File proFile;
	private MineProfession mp;
	private YamlConfiguration proYaml;
	public ProfessionManager(MineProfession mp, File file){
		this.mp = mp;
		this.proFile = file;
	}
	private HashMap<String, Profession> proMap;
	
	public void load(){
		proYaml = new YamlConfiguration();
		proMap = new HashMap<String, Profession>();
		try {
			proYaml.load(proFile);
			Set<String> keys = proYaml.getKeys(false);
			for(String profession:keys){
				Profession pro = new Profession(mp, profession, proYaml.getConfigurationSection(profession));
				pro.load();
				proMap.put(profession, pro);
			}
		} catch (FileNotFoundException e) {
			mp.log.info("MineProfession: Missing MineProfession/profession.yml");
		} catch (IOException e) {
			mp.log.info("MineProfession: MineProfession/profession.yml cannot be read.");
		} catch (InvalidConfigurationException e) {
			mp.log.info("MineProfession: wrong format: MineProfession/profession.yml.");
		}
	}
	
	public void generalListener(Event event, String playerName){
	 	Profession major = proMap.get(mp.data.getMajor(playerName));
		Profession minor = proMap.get(mp.data.getMinor(playerName));
		try {
			Method method = Profession.class.getMethod("onEvent", event.getClass());
			if(major != null){
				try {
					method.invoke(major, event);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			if(minor != null){
				try {
					method.invoke(minor, event);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	
	@EventHandler
	public void onEvent(BlockBreakEvent event){
		if(!event.getPlayer().getItemInHand().containsEnchantment(Enchantment.getById(33)) || mp.getConfig().getStringList("track-placement").contains(event.getBlock().getType().name())){
			mp.log.info(event.getEventName()+": '"+event.getBlock().getType().name()+"'");
			this.generalListener(event, event.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void onEvent(PlayerShearEntityEvent event){
		//mp.log.info(event.getEventName()+": '"+event.getEntity().toString()+"'");
		this.generalListener(event, event.getPlayer().getName());
	}
	
	@EventHandler
	public void onEvent(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player && !spawnerChild.contains(event.getEntity().getUniqueId())){
			//mp.log.info(event.getEventName()+": '"+event.getEntity().toString()+" "+event.getDamager().toString()+"'");
			this.generalListener(event, ((Player)event.getDamager()).getName());
		}
	}
	
	@EventHandler
	public void onEvent(EntityDamageEvent event){
		if(event.getEntity() instanceof Tameable && ((Tameable)event.getEntity()).isTamed() && ((Tameable)event.getEntity()).getOwner() instanceof Player){
			Player player = (Player)((Tameable)event.getEntity()).getOwner();
			String playerName = player.getName();
		 	Profession major = proMap.get(mp.data.getMajor(playerName));
			Profession minor = proMap.get(mp.data.getMinor(playerName));
			if(major != null){
				major.onEvent(event, player);
			}
			if(minor != null){
				minor.onEvent(event, player);
			}
		}
	}
	
	@EventHandler
	public void onEvent(EntityTameEvent event){
		if(event.getOwner() instanceof Player){
			//mp.log.info(event.getEventName()+": '"+event.getEntity().toString()+" "+event.getOwner().toString()+"'");
			this.generalListener(event, ((Player)event.getOwner()).getName());
		}
	}
	
	@EventHandler
	public void onEvent(EntityDeathEvent event){
		spawnerChild.remove(event.getEntity().getUniqueId());
		if(humanKill.contains(event.getEntity().getLastDamageCause().getCause().name()) && event.getEntity().getLocation().getWorld()!=null){
			List<Player> players = event.getEntity().getLocation().getWorld().getPlayers();
			for(Player player:players){
				if(player.getLocation().distance(event.getEntity().getLocation())<10){
					if(player!=null){
					 	Profession major = proMap.get(mp.data.getMajor(player.getName()));
						Profession minor = proMap.get(mp.data.getMinor(player.getName()));
						if(major!=null){
							major.onEvent(event, player);
						}
						if(minor!=null){
							minor.onEvent(event, player);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEvent(EnchantItemEvent event){
		/*{
			Map<Enchantment, Integer> map = event.getEnchantsToAdd();
			Set<Enchantment> set = map.keySet();
			StringBuilder sb = new StringBuilder();
			sb.append(event.getEventName());
			sb.append(": ");
			sb.append(event.getEnchanter().getName());
			sb.append(", ");
			sb.append(event.getItem().getType().name());

			for (Enchantment ench : set) {
				sb.append(", ");
				sb.append(ench.getName());
				sb.append(" ");
				sb.append(map.get(ench));
			}

			mp.log.info(sb.toString());
		}*/
		this.generalListener(event, event.getEnchanter().getName());
	}
	
	@EventHandler
	public void onEvent(CreatureSpawnEvent event){
		if(event.getSpawnReason().name().equals("CUSTOM") && event.getLocation().getWorld()!=null){
			
			List<Player> players = event.getLocation().getWorld().getPlayers();
			for(Player player:players){
				if(player.getLocation().distance(event.getLocation())<10){
					if(player!=null){
					 	Profession major = proMap.get(mp.data.getMajor(player.getName()));
						Profession minor = proMap.get(mp.data.getMinor(player.getName()));
						if(major!=null){
							major.onEvent(event, player);
						}
						if(minor!=null){
							minor.onEvent(event, player);
						}
					}
				}
			}
		}else if(event.getSpawnReason().name().equals("SPAWNER")){
			spawnerChild.add(event.getEntity().getUniqueId());
		}
	}
}
