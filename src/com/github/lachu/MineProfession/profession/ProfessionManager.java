package com.github.lachu.MineProfession.profession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.lachu.MineProfession.MineProfession;

public class ProfessionManager {
	
	private File proFile;
	private MineProfession mp;
	private YamlConfiguration proYaml;
	public ProfessionManager(MineProfession mp, File file){
		this.mp = mp;
		this.proFile = file;
	}
	
	public void load(){
		proYaml = new YamlConfiguration();
		try {
			proYaml.load(proFile);
			//TODO
		} catch (FileNotFoundException e) {
			mp.log.info("MineProfession: Missing MineProfession/profession.yml");
		} catch (IOException e) {
			mp.log.info("MineProfession: MineProfession/profession.yml cannot be read.");
		} catch (InvalidConfigurationException e) {
			mp.log.info("MineProfession: wrong format: MineProfession/profession.yml.");
		}
	}
}
