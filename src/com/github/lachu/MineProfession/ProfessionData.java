package com.github.lachu.MineProfession;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class ProfessionData {
	public static class PlayerEntry implements Serializable{
		private static final long serialVersionUID = 1L;
		String major_profession;
		Integer major_level;
		Integer major_experience;
		String minor_profession;
		Integer minor_level;
		Integer minor_experience;
	}
	private HashMap<String,PlayerEntry> data;
	private MineProfession mp;
	private File dbFile;
	private Set<String> pros;
	private HashMap<String,String> descriptions;
	
	@SuppressWarnings("unchecked")
	public ProfessionData(MineProfession mp, File dataFile, File proFile){
		this.mp = mp;
		this.dbFile = dataFile;
		
		if(!dbFile.exists()){
			mp.log.info("MineProfession: player data not exist. create new.");
			data = new HashMap<String,PlayerEntry>();
		}else{
			ObjectInputStream ois;
			try {
				mp.log.info("MineProfession: Going to read player data.");
				ois = new ObjectInputStream(new FileInputStream(dbFile));
				Object result = ois.readObject();
				ois.close();
				data = (HashMap<String,PlayerEntry>)result;
				mp.log.info("MineProfession: Finish reading player data.");
			} catch (FileNotFoundException e) {
				mp.log.info("MineProfession: Cannot read player data from "+dbFile.toString());
			} catch (IOException e) {
				mp.log.info("MineProfession: Cannot read player data from "+dbFile.toString());
			} catch (ClassNotFoundException e) {
				mp.log.info("MineProfession: Cannot read player data from "+dbFile.toString());
			}
		}
		
		YamlConfiguration proYaml = new YamlConfiguration();
		descriptions = new HashMap<String,String>();
		try {
			proYaml.load(proFile);
			pros = proYaml.getKeys(false);
			for(String pro:pros){
				descriptions.put(pro, proYaml.getString(pro+".description"));
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
		mp.log.info("MineProfession: Going to save player data.");

		{
			Set<String> set = data.keySet();
			for(String str:set){
				mp.log.info(str+" "+data.get(str).major_profession);
			}
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dbFile));
			oos.writeObject(this.data);
			oos.flush();
			oos.close();
			mp.log.info("MineProfession: player data saved.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			mp.log.info("MineProfession: Cannot save player data to "+dbFile.toString());
		} catch (IOException e) {
			e.printStackTrace();
			mp.log.info("MineProfession: Cannot save player data to "+dbFile.toString());
		}
		if(backup){
			Calendar currentTime = Calendar.getInstance();
			StringBuilder sb = new StringBuilder();
			sb.append(currentTime.get(Calendar.YEAR));
			sb.append("_");
			sb.append(currentTime.get(Calendar.MONTH+1));
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
				oos.writeObject(this.data);
				oos.flush();
				oos.close();
				mp.log.info("MineProfession: Player data backup saved.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				mp.log.info("MineProfession: Backup player data to "+backFile.toString()+" failed.");
			} catch (IOException e) {
				e.printStackTrace();
				mp.log.info("MineProfession: Backup player data to "+backFile.toString()+" failed.");
			}
		}
	}
	
	public String[] getProfessions(){
		return pros.toArray(new String[]{});
	}
	
	public String getDescription(String profession){
		String des = descriptions.get(profession);
		if(des == null){
			des = "";
		}
		return des;
	}
	
	public boolean clearMajor(String playerName){
		return data.remove(playerName)!=null;
	}
	
	public boolean clearMinor(String playerName){
		PlayerEntry pe;
		if((pe=data.get(playerName))!=null && pe.minor_profession!=null){
			pe.minor_profession=null;
			pe.minor_experience=new Integer(0);
			pe.minor_level=new Integer(0);
			return true;
		}
		return false;
	}
	
	public boolean hasProfession(String profession){
		return pros.contains(profession);
	}
	
	public String getMajor(String playerName){
		PlayerEntry pe;
		if((pe=data.get(playerName))!=null){
			return pe.major_profession;
		}
		return null;
	}
	
	public String getMinor(String playerName){
		PlayerEntry pe;
		if((pe=data.get(playerName))!=null){
			return pe.minor_profession;
		}
		return null;
	}

	public int getMajorLevel(String playerName){
		PlayerEntry pe;
		if((pe=data.get(playerName))!=null){
			return pe.major_level.intValue();
		}
		return 0;
	}
	
	public int getMinorLevel(String playerName){
		PlayerEntry pe;
		if((pe=data.get(playerName))!=null){
			return pe.minor_level.intValue();
		}
		return 0;
	}
	public int getMajorExperience(String playerName){
		PlayerEntry pe;
		if((pe=data.get(playerName))!=null){
			return pe.major_experience.intValue();
		}
		return 0;
	}
	
	public int getMinorExperience(String playerName){
		PlayerEntry pe;
		if((pe=data.get(playerName))!=null){
			return pe.minor_experience.intValue();
		}
		return 0;
	}
	
	public boolean setMajor(String playerName, String profession){
		PlayerEntry pe = data.get(playerName);
		if(hasProfession(profession) && (pe==null || pe.major_profession==null)){
			if(pe==null){
				pe = new PlayerEntry();
				data.put(playerName,pe);
			}
			pe.major_profession = profession;
			pe.major_level = new Integer(0);
			pe.major_experience = new Integer(0);
			mp.log.info(playerName+","+pe.major_profession+","+pe.major_level+","+pe.major_experience);	
			return true;
		}
		return false;
	}
	
	public boolean setMinor(String playerName, String profession){
		PlayerEntry pe = data.get(playerName);
		if(hasProfession(profession) && pe!=null && pe.major_level>=25 && pe.minor_profession==null){
			pe.minor_profession = profession;
			pe.minor_level = new Integer(0);
			pe.minor_experience = new Integer(0);
			return true;
		}
		return false;
	}
	
}