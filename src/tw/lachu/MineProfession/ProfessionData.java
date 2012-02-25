package tw.lachu.MineProfession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import tw.lachu.MineProfession.util.SerialData;

public class ProfessionData extends SerialData<HashMap<String, ProfessionData.PlayerEntry>>{
	public static class PlayerEntry implements Serializable{
		private static final long serialVersionUID = 1L;
		String major_profession;
		int major_level;
		double major_experience;
		String minor_profession;
		int minor_level;
		double minor_experience;
	}
	private HashMap<String,PlayerEntry> data;
	private MineProfession mp;
	private File dbFile;
	private Set<String> pros;
	private HashMap<String,String> descriptions;
	
	public ProfessionData(MineProfession mp, File dataFile, File proFile){
		this.mp = mp;
		this.dbFile = dataFile;
		
		data = super.load(dbFile);
		if(data != null){
			mp.log.info("MineProfession: Finish reading player data.");
		}else{
			data = new HashMap<String,PlayerEntry>();
			mp.log.info("MineProfession: Cannot read player data from "+dbFile.toString()+". Create an empty one.");
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
	
	public synchronized boolean saveTable(boolean backup){
		mp.log.info("MineProfession: Going to save player data.");
		boolean success = false;
		
		if(super.save(data, dbFile,backup)){
			mp.log.info("MineProfession: player data saved.");
		}else{
			mp.log.info("MineProfession: Cannot save player data to "+dbFile.toString());
		}
		
		return success;
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
	
	public synchronized boolean clearMajor(String playerName){
		return data.remove(playerName.toLowerCase())!=null;
	}
	
	public synchronized boolean clearMinor(String playerName){
		PlayerEntry pe;
		if((pe=data.get(playerName.toLowerCase()))!=null && pe.minor_profession!=null){
			pe.minor_profession=null;
			pe.minor_experience=0;
			pe.minor_level=0;
			return true;
		}
		return false;
	}
	
	public synchronized boolean isAProfession(String profession){
		return pros.contains(profession);
	}
	
	public synchronized String getMajor(String playerName){
		PlayerEntry pe;
		if((pe=data.get(playerName.toLowerCase()))!=null){
			return pe.major_profession;
		}
		return null;
	}
	
	public synchronized String getMinor(String playerName){
		PlayerEntry pe;
		if((pe=data.get(playerName.toLowerCase()))!=null){
			return pe.minor_profession;
		}
		return null;
	}

	public synchronized int getMajorLevel(String playerName){
		PlayerEntry pe;
		if((pe=data.get(playerName.toLowerCase()))!=null){
			return pe.major_level;
		}
		return 0;
	}
	
	public synchronized int getMinorLevel(String playerName){
		PlayerEntry pe;
		if((pe=data.get(playerName.toLowerCase()))!=null){
			return pe.minor_level;
		}
		return 0;
	}
	
	public synchronized int getMajorExperience(String playerName){
		PlayerEntry pe;
		if((pe=data.get(playerName.toLowerCase()))!=null){
			if(pe.major_level == mp.getConfig().getInt("max-major-level")){
				return 0;
			}
			return (int)pe.major_experience;
		}
		return 0;
	}
	
	public synchronized int getMinorExperience(String playerName){
		PlayerEntry pe;
		if((pe=data.get(playerName.toLowerCase()))!=null){
			if(pe.minor_level == mp.getConfig().getInt("max-minor-level")){
				return 0;
			}
			return (int)pe.minor_experience;
		}
		return 0;
	}
	
	public synchronized boolean setMajorLevel(String playerName, int level){
		PlayerEntry pe;
		if((pe=data.get(playerName.toLowerCase()))!=null && pe.major_profession!=null){
			if(level>0 && level<mp.getConfig().getInt("max-major-level")){
				pe.major_level = level;
				return true;
			}
		}
		return false;
	}
	
	public synchronized boolean setMinorLevel(String playerName, int level){
		PlayerEntry pe;
		if((pe=data.get(playerName.toLowerCase()))!=null && pe.minor_profession!=null){
			if(level>0 && level<mp.getConfig().getInt("max-minor-level")){
				pe.minor_level = level;
				return true;
			}
		}
		return false;
	}
	
	public synchronized boolean setMajor(String playerName, String profession){
		PlayerEntry pe = data.get(playerName.toLowerCase());
		if(isAProfession(profession) && (pe==null || pe.major_profession==null)){
			if(pe==null){
				pe = new PlayerEntry();
				data.put(playerName.toLowerCase(),pe);
			}
			pe.major_profession = profession;
			pe.major_level = 1;
			pe.major_experience = 0;
			//mp.log.info(playerName+","+pe.major_profession+","+pe.major_level+","+pe.major_experience);	
			return true;
		}
		return false;
	}
	
	public synchronized boolean setMinor(String playerName, String profession){
		PlayerEntry pe = data.get(playerName.toLowerCase());
		if(isAProfession(profession) && pe!=null && pe.major_level>=mp.getConfig().getInt("minor-require") && pe.minor_profession==null){
			pe.minor_profession = profession;
			pe.minor_level = 1;
			pe.minor_experience = 0;
			return true;
		}
		return false;
	}
	
	public synchronized void gainExperience(String playerName, String professionName, double expAmount){
		
		PlayerEntry pe = data.get(playerName.toLowerCase());
		if(professionName == null || pe==null){
			return;
		}
		
		double exp = expAmount;
		double thisLevel = getExperienceForLevel(pe.major_level);
		double prevLevel = getExperienceForLevel(pe.major_level-1);
		
		if(professionName.equals(getMajor(playerName))){
			pe.major_experience += exp;
			while(pe.major_experience<0){
				if(pe.major_level==1){
					pe.major_experience = 0;
				}else{
					pe.major_experience += prevLevel;
					pe.major_level = pe.major_level-1;
				}
			}
			while(pe.major_experience>=thisLevel && pe.major_level<mp.getConfig().getInt("max-major-level")){
				pe.major_experience -= thisLevel;
				pe.major_level = pe.major_level+1;
			}
		}else if(professionName.equals(getMinor(playerName))){
			pe.minor_experience += exp;
			while(pe.minor_experience<0){
				if(pe.minor_level==1){
					pe.minor_experience = 0;
				}else{
					pe.minor_experience += prevLevel;
					pe.minor_level = pe.minor_level-1;
				}
			}
			while(pe.minor_experience>=thisLevel && pe.minor_level<mp.getConfig().getInt("max-minor-level")){
				pe.minor_experience -= thisLevel;
				pe.minor_level = pe.minor_level+1;
			}
		}
	}
	
	public synchronized double getProfessionPower(String playerName, String professionName){
		PlayerEntry pe = data.get(playerName.toLowerCase());
		if(pe==null){
			return 0;
		}
		if(professionName.equals(pe.major_profession)){
			return powerFunction(pe.major_level);
		}else if(professionName.equals(pe.minor_profession)){
			return powerFunction(pe.minor_level);
		}
		return 0;
	}
	
	private double powerFunction(int level){
		int max = Math.max(mp.getConfig().getInt("max-major-level"), mp.getConfig().getInt("max-minor-level"));
		return Math.sin(level*Math.PI/max/2);
	}
	
	public static int getExperienceForLevel(int level){
		return 10*level;
	}
}