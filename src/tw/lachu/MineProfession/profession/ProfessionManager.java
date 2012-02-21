package tw.lachu.MineProfession.profession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

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
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		Profession major = proMap.get(mp.data.getMajor(player.getName()));
		Profession minor = proMap.get(mp.data.getMinor(player.getName()));
		if(major != null){
			major.onBlockBreak(event);
		}
		if(minor != null){
			minor.onBlockBreak(event);
		}
	}
}
