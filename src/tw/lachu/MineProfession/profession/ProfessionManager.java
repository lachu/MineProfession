package tw.lachu.MineProfession.profession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
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
		this.generalListener(event, event.getPlayer().getName());
	}
	
	@EventHandler
	public void onEvent(PlayerShearEntityEvent event){
		this.generalListener(event, event.getPlayer().getName());
	}
	
	@EventHandler
	public void onEvent(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player){
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
			this.generalListener(event, ((Player)event.getOwner()).getName());
		}
	}
	
	@EventHandler
	public void onEvent(EntityDeathEvent event){
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
		this.generalListener(event, event.getEnchanter().getName());
	}
	
	@EventHandler
	public void onEvent(CreatureSpawnEvent event){
		if(event.getSpawnReason().name().equals("CUSTOM") && event.getLocation().getWorld()!=null){
			mp.debug(this, "CreatureSpawn", event.getCreatureType().name());
			List<Player> players = event.getLocation().getWorld().getPlayers();
			double min_distance = 0;
			Player closest = null;
			for(Player player:players){
				double temp = player.getLocation().distance(event.getLocation());
				if(closest==null || min_distance>temp){
					closest = player;
				}
			}
			if(closest.getLocation().distance(event.getLocation())<100){
				if(closest!=null){
				 	Profession major = proMap.get(mp.data.getMajor(closest.getName()));
					Profession minor = proMap.get(mp.data.getMinor(closest.getName()));
					if(major!=null){
						major.onEvent(event, closest);
					}
					if(minor!=null){
						minor.onEvent(event, closest);
					}
				}
			}
		}
	}
}
