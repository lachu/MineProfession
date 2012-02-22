package tw.lachu.MineProfession;

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
	
	public synchronized boolean saveTable(boolean backup){
		mp.log.info("MineProfession: Going to save player data.");
		boolean success = false;
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
			success = true;
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
			return (int)pe.major_experience;
		}
		return 0;
	}
	
	public synchronized int getMinorExperience(String playerName){
		PlayerEntry pe;
		if((pe=data.get(playerName.toLowerCase()))!=null){
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
			if(pe.major_experience<0){
				if(pe.major_level==1){
					pe.major_experience = 0;
				}else{
					pe.major_experience += prevLevel;
					pe.major_level = pe.major_level-1;
				}
			}else if(pe.major_experience>=thisLevel && pe.major_level<mp.getConfig().getInt("max-major-level")){
				pe.major_experience -= thisLevel;
				pe.major_level = pe.major_level+1;
			}
		}else if(professionName.equals(getMinor(playerName))){
			pe.minor_experience += exp;
			if(pe.minor_experience<0){
				if(pe.minor_level==1){
					pe.minor_experience = 0;
				}else{
					pe.minor_experience += prevLevel;
					pe.minor_level = pe.minor_level-1;
				}
			}else if(pe.minor_experience>=thisLevel && pe.minor_level<mp.getConfig().getInt("max-minor-level")){
				pe.minor_experience -= thisLevel;
				pe.minor_level = pe.minor_level+1;
			}
		}
	}
	
	public int getExperienceForLevel(int level){
		return 10*level;
	}
}