package tw.lachu.MineProfession.profession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import tw.lachu.MineProfession.MineProfession;


public class ProfessionManager implements Listener{
	
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
		mp.log.info(event.getEventName()+": '"+event.getEntity().toString()+"'");
		this.generalListener(event, event.getPlayer().getName());
	}
	
	@EventHandler
	public void onEvent(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player){
			mp.log.info(event.getEventName()+": '"+event.getEntity().toString()+" "+event.getDamager().toString()+"'");
			this.generalListener(event, ((Player)event.getDamager()).getName());
		}
	}
	
	@EventHandler
	public void onEvent(EntityTameEvent event){
		if(event.getOwner() instanceof Player){
			mp.log.info(event.getEventName()+": '"+event.getEntity().toString()+" "+event.getOwner().toString()+"'");
			this.generalListener(event, ((Player)event.getOwner()).getName());
		}
	}
	
}
