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
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
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
		mp.log.info(event.toString());
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
		mp.log.info(event.getEventName()+": '"+event.getBlock().getType().name()+"'");
		this.generalListener(event, event.getPlayer().getName());
	}
	
	@EventHandler
	public void onEvent(BlockPlaceEvent event){
		mp.log.info(event.getEventName()+": '"+event.getBlock().getType().name()+"'");
		this.generalListener(event, event.getPlayer().getName());
	}
	
	@EventHandler
	public void onEvent(PlayerShearEntityEvent event){
		mp.log.info(event.getEventName()+": '"+event.getEntity().toString()+"'");
		this.generalListener(event, event.getPlayer().getName());
	}
	
	@EventHandler
	public void onEvent(EnchantItemEvent event){
		mp.log.info(event.toString());
	}
}
