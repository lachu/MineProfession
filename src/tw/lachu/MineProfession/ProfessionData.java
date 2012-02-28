package tw.lachu.MineProfession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import tw.lachu.MineProfession.math.MathExpression;
import tw.lachu.MineProfession.math.MathExpressionFactory;
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
	
	public static class ProfessionEntry implements Serializable{
		private static final long serialVersionUID = 1L;
		String profession;
		int level;
		double experience;
		
		public ProfessionEntry(String pro, double exp){
			this.profession = pro;
			this.experience = exp;
		}
	}
	
	//private HashMap<String,PlayerEntry> oldData;
	private HashMap<String, ArrayList<ProfessionEntry>> data; 
	private MineProfession mp;
	//private File dbFile;
	private File newDbFile;
	private Set<String> professionNames;
	private HashMap<String,String> descriptions;
	
	private MathExpression powerFormula;
	private MathExpression expFormula;
	
	
	public ProfessionData(MineProfession mp, File dataFile, File proFile){
		HashMap<String,PlayerEntry> oldData = null;
		File dbFile;
		this.mp = mp;
		dbFile = dataFile;
		
		newDbFile = new File(dbFile.getParentFile(), "ProfessionData");
		
		SerialData<HashMap<String, ArrayList<ProfessionEntry>>> sd = new SerialData<HashMap<String, ArrayList<ProfessionEntry>>>();
		data = sd.load(newDbFile);
		if(data == null){
			oldData = super.load(dbFile);
			if(oldData != null){
				mp.log.info("MineProfession: Finish reading player data.");
				data = new HashMap<String, ArrayList<ProfessionEntry>>();
				Set<String> players = oldData.keySet();
				for(String player : players){
					if(oldData.get(player)!=null){
						data.put(player, new ArrayList<ProfessionEntry>());
						if(oldData.get(player).major_profession!=null){
							data.get(player).set(0, new ProfessionEntry(oldData.get(player).major_profession, oldData.get(player).major_experience));
						}
						if(oldData.get(player).minor_profession!=null){
							data.get(player).set(1, new ProfessionEntry(oldData.get(player).minor_profession, oldData.get(player).minor_experience));
						}
					}
				}
			}else{
				//oldData = new HashMap<String,PlayerEntry>();
				data = new HashMap<String, ArrayList<ProfessionEntry>>();
				mp.log.info("MineProfession: Cannot read player data from "+dbFile.toString()+". Create an empty one.");
			}
		}else{
			mp.log.info("MineProfession: Finish reading player data.");
		}
		
		YamlConfiguration proYaml = new YamlConfiguration();
		descriptions = new HashMap<String,String>();
		try {
			proYaml.load(proFile);
			professionNames = proYaml.getKeys(false);
			for(String pro:professionNames){
				descriptions.put(pro, proYaml.getString(pro+".description"));
			}
		} catch (FileNotFoundException e) {
			mp.log.info("MineProfession: Missing MineProfession/profession.yml");
		} catch (IOException e) {
			mp.log.info("MineProfession: MineProfession/profession.yml cannot be read.");
		} catch (InvalidConfigurationException e) {
			mp.log.info("MineProfession: wrong format: MineProfession/profession.yml.");
		}
		
		powerFormula = (new MathExpressionFactory()).parse(mp.getConfig().getString("power-formula"));
		expFormula = (new MathExpressionFactory()).parse(mp.getConfig().getString("experience-formula"));
		
		{
			Set<String> players = data.keySet();
			for(String player : players){
				judgeLevel(player);
			}
		}
		
	}
	
	public synchronized boolean saveTable(boolean backup){
		mp.log.info("MineProfession: Going to save player data.");
		
		SerialData<HashMap<String, ArrayList<ProfessionEntry>>> sd = new SerialData<HashMap<String, ArrayList<ProfessionEntry>>>();
		
		if(sd.save(data, newDbFile, backup)){
			mp.log.info("MineProfession: player data saved.");
			return true;
		}else{
			mp.log.info("MineProfession: Cannot save player data to "+newDbFile.toString());
			return false;
		}
		
	}
	
	public String[] getProfessions(){
		return professionNames.toArray(new String[]{});
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
		return data.get(playerName)!=null && data.get(playerName).remove(1)!=null;
	}
	
	private synchronized ProfessionEntry getEntry(String playerName, String professionName){
		ProfessionEntry major = getMajorEntry(playerName);
		if(major != null && major.profession.equals(professionName)){
			return major;
		}
		ProfessionEntry minor = getMinorEntry(playerName);
		if(minor != null && major.profession.equals(professionName)){
			return minor;
		}
		return null;
	}
	
	private synchronized ProfessionEntry getMajorEntry(String playerName){
		ArrayList<ProfessionEntry> list = data.get(playerName);
		return ((list!=null && !list.isEmpty())?(list.get(0)):(null));
	}
	
	private synchronized ProfessionEntry getMinorEntry(String playerName){
		ArrayList<ProfessionEntry> list = data.get(playerName);
		return ((list!=null && list.size()>1)?(list.get(1)):(null));
	}
	
	public synchronized boolean setMajor(String playerName, String profession){
		if(!isAProfession(profession) || getMajorEntry(playerName)!=null){
			return false;
		}
		data.get(playerName).set(0, new ProfessionEntry(profession, 0));
		return true;
	}
	
	public synchronized boolean setMinor(String playerName, String profession){
		if(!isAProfession(profession) || getMajorLevel(playerName)<mp.getConfig().getInt("minor-require") || getMajorEntry(playerName)!=null){
			return false;
		}

		data.get(playerName).set(1, new ProfessionEntry(profession, 0));
		return true;
	}

	public synchronized boolean promote(String playerName){
		if(data.get(playerName)!=null && data.get(playerName).size()>1 && data.get(playerName).get(1)!=null){
			data.get(playerName).set(0, data.get(playerName).remove(1));
			return true;
		}
		return false;
	}
	
	public boolean isAProfession(String profession){
		return professionNames.contains(profession);
	}
	
	private synchronized String getProfessionName(ProfessionEntry entry){
		return ((entry!=null)?(entry.profession):(null));
	}
	
	private synchronized int getLevel(ProfessionEntry entry){
		return ((entry!=null)?(entry.level):(0));
	}
	
	private synchronized double getExperience(ProfessionEntry entry){
		return ((entry!=null)?(entry.experience):(0));
	}
	
	public synchronized String getMajor(String playerName){
		return getProfessionName(getMajorEntry(playerName));
	}
	
	public synchronized String getMinor(String playerName){
		return getProfessionName(getMinorEntry(playerName));
	}

	public synchronized int getMajorLevel(String playerName){
		return getLevel(getMajorEntry(playerName));
	}
	
	public synchronized int getMinorLevel(String playerName){
		return getLevel(getMinorEntry(playerName));
	}
	
	public synchronized int getMajorExperience(String playerName){
		return (int)getExperience(getMajorEntry(playerName));
	}
	
	public synchronized int getMinorExperience(String playerName){
		return (int)getExperience(getMinorEntry(playerName));
	}
	
	public synchronized void gainExperience(String playerName, String professionName, double expAmount){
		
		ProfessionEntry entry = getEntry(playerName, professionName);
		if(entry!=null){
			entry.experience += expAmount;
			int levelLimit;
			if(entry == getMajorEntry(playerName)){
				levelLimit = mp.getConfig().getInt("max-major-level");
			}else{
				levelLimit = mp.getConfig().getInt("max-minor-level");
			}
			
			while(entry.experience >= getExperienceToLevel(entry.level+1) && entry.level < levelLimit){
				++entry.level;
				mp.getServer().getPlayer(playerName).sendMessage(ChatColor.GOLD+"Your profession, "+professionName+", has levelled up!");
			}
			
			/*while(entry.experience < getExperienceToLevel(entry.level) && entry.level>1){
				--entry.level;
			}*/
		}
		
	}
	
	public synchronized void judgeLevel(String playerName){
		ProfessionEntry major = getMajorEntry(playerName);
		ProfessionEntry minor = getMinorEntry(playerName);
		if(major!=null){
			int levelLimit = mp.getConfig().getInt("max-major-level");
		
			while(major.experience >= getExperienceToLevel(major.level+1) && major.level < levelLimit){
				++major.level;
			}
		
			while(major.experience < getExperienceToLevel(major.level) && major.level>1){
				--major.level;
			}
		}
		if(minor!=null){
			int levelLimit = mp.getConfig().getInt("max-minor-level");
		
			while(minor.experience >= getExperienceToLevel(minor.level+1) && minor.level < levelLimit){
				++minor.level;
			}
		
			while(minor.experience < getExperienceToLevel(minor.level) && minor.level>1){
				--minor.level;
			}
		}
	}
	
	public synchronized double getProfessionPower(String playerName, String professionName){
		ProfessionEntry entry = getEntry(playerName, professionName);
		if(entry!=null){
			return powerFunction(entry.level);
		}
		return 0;
	}
	
	private double powerFunction(int level){
		HashMap<String, Double> map = new HashMap<String, Double>();
		map.put("level", (double)level);
		map.put("maxLevel", (double)(Math.max(mp.getConfig().getInt("max-major-level"), mp.getConfig().getInt("max-minor-level"))));
		return powerFormula.value(map);
	}
	
	public int getExperienceToLevel(int level){
		HashMap<String, Double> map = new HashMap<String, Double>();
		map.put("maxLevel", (double)Math.max(mp.getConfig().getInt("max-major-level"), mp.getConfig().getInt("max-minor-level")));
		map.put("level", (double)level);
		return (int)expFormula.value(map);
	}
}