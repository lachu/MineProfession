package com.github.lachu.MineProfession;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class ProfessionData {
	public class PlayerEntry{
		String first_profession;
		int first_level;
		int first_experience;
		String second_profession;
		int second_level;
		int second_experience;
	}
	private HashMap<String,PlayerEntry> data;
	private MineProfession mp;
	private File dbFile;
	private Set<String> pros;
	private Set<String> prosWithDescription;
	
	@SuppressWarnings("unchecked")
	public ProfessionData(MineProfession mp, File dataFile, File proFile){
		this.mp = mp;
		this.dbFile = dataFile;
		
		if(dbFile.exists()){
			data = new HashMap<String,PlayerEntry>();
		}else{
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new FileInputStream(dbFile));
				Object result = ois.readObject();
				ois.close();
				data = (HashMap<String,PlayerEntry>)result;
			} catch (FileNotFoundException e) {
				mp.log.info("MineProfession: Cannot read player data from "+dbFile.toString());
			} catch (IOException e) {
				mp.log.info("MineProfession: Cannot read player data from "+dbFile.toString());
			} catch (ClassNotFoundException e) {
				mp.log.info("MineProfession: Cannot read player data from "+dbFile.toString());
			}
		}
		
		YamlConfiguration proYaml = new YamlConfiguration();
		prosWithDescription = new HashSet<String>();
		try {
			proYaml.load(proFile);
			pros = proYaml.getKeys(false);
			for(String pro:pros){
				prosWithDescription.add(pro+" - "+proYaml.getString(pro+".description"));
			}
		} catch (FileNotFoundException e) {
			mp.log.info("MineProfession: Missing MineProfession/profession.yml");
		} catch (IOException e) {
			mp.log.info("MineProfession: MineProfession/profession.yml cannot be read.");
		} catch (InvalidConfigurationException e) {
			mp.log.info("MineProfession: wrong format: MineProfession/profession.yml.");
		}
	}
	
	public void saveTable(boolean backup){
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dbFile));
			oos.writeObject(data);
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			mp.log.info("MineProfession: Cannot save player data to "+dbFile.toString());
		} catch (IOException e) {
			mp.log.info("MineProfession: Cannot save player data to "+dbFile.toString());
		}
		if(backup){
			Calendar currentTime = Calendar.getInstance();
			StringBuilder sb = new StringBuilder();
			sb.append(currentTime.get(Calendar.YEAR));
			sb.append("_");
			sb.append(currentTime.get(Calendar.MONTH));
			sb.append("_");
			sb.append(currentTime.get(Calendar.DATE));
			sb.append("_");
			sb.append(currentTime.get(Calendar.HOUR_OF_DAY));
			sb.append("_");
			sb.append(currentTime.get(Calendar.MINUTE));
			sb.append("_");
			sb.append(currentTime.get(Calendar.SECOND));
			sb.append(".playerTable");
			File backDir = new File(dbFile.getParentFile(),"backup");
			backDir.mkdir();
			File backFile = new File(backDir, sb.toString() );
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(new FileOutputStream(backFile));
				oos.writeObject(data);
				oos.flush();
				oos.close();
			} catch (FileNotFoundException e) {
				mp.log.info("MineProfession: Backup player data to "+backFile.toString()+" failed.");
			} catch (IOException e) {
				mp.log.info("MineProfession: Backup player data to "+backFile.toString()+" failed.");
			}
		}
	}
	
	public String[] getProfessionsWithDescription(){
		return prosWithDescription.toArray(new String[]{});
	}
}
