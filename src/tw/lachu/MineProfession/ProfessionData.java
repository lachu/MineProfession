package tw.lachu.MineProfession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import tw.lachu.math.MathExpression;
import tw.lachu.math.MathExpressionFactory;
import tw.lachu.util.SerialData;

public class ProfessionData extends SerialData<HashMap<String, HashMap<Integer, ProfessionData.ProfessionEntry>>>{
	
	public static class ProfessionEntry implements Serializable{
		private static final long serialVersionUID = 1L;
		String profession;
		int level;
		double experience;
		
		public ProfessionEntry(String pro, double exp){
			this.profession = pro;
			this.experience = exp;
			this.level = 1;
		}
	}
	
	//private HashMap<String,PlayerEntry> oldData;
	private HashMap<String, HashMap<Integer, ProfessionEntry>> data; 
	private MineProfession mp;
	//private File dbFile;
	private File dataFile;
	private Set<String> professionNames;
	private HashMap<String,String> descriptions;
	
	private MathExpression powerFormula;
	private MathExpression expFormula;
	
	
	public ProfessionData(MineProfession mp, File inFile, File proFile){
		this.mp = mp;
		this.dataFile = inFile;
		
		data = super.load(inFile);
		if(data == null){
			data = new HashMap<String, HashMap<Integer, ProfessionEntry>>();
			mp.log.info("MineProfession: Cannot read player data from "+dataFile.toString()+". Create an empty one.");
		}else{
			mp.log.info("MineProfession: Finish reading player data from "+dataFile.toString()+".");
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

		
		if(super.save(data, dataFile, backup)){
			mp.log.info("MineProfession: player data saved to "+dataFile.toString()+".");
			return true;
		}else{
			mp.log.info("MineProfession: Cannot save player data to "+dataFile.toString());
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
	
	private HashMap<Integer, ProfessionEntry> getPlayerMap(String playerName){
		HashMap<Integer, ProfessionEntry> map = data.get(playerName.toLowerCase());
		if(map == null){
			map = new HashMap<Integer, ProfessionEntry>();
			data.put(playerName.toLowerCase(), map);
		}
		return map;
	}
	
	private boolean removePlayerMap(String playerName){
		return data.remove(playerName.toLowerCase())!=null;
	}
	
	public synchronized boolean clearMajor(String playerName){
		return removePlayerMap(playerName);
	}
	
	public synchronized boolean clearMinor(String playerName){
		return getPlayerMap(playerName).remove(1)!=null;
	}
	
	private synchronized ProfessionEntry getProfessionEntry(String playerName, String professionName){
		ProfessionEntry major = getMajorEntry(playerName);
		if(major != null && major.profession.equals(professionName)){
			return major;
		}
		ProfessionEntry minor = getMinorEntry(playerName);
		if(minor != null && minor.profession.equals(professionName)){
			return minor;
		}
		return null;
	}
	
	private synchronized ProfessionEntry getMajorEntry(String playerName){
		return getPlayerMap(playerName).get(0);
	}
	
	private synchronized ProfessionEntry getMinorEntry(String playerName){
		return getPlayerMap(playerName).get(1);
	}
	
	public synchronized boolean setMajor(String playerName, String profession){
		if(!isAProfession(profession) || getMajorEntry(playerName)!=null){
			return false;
		}
		getPlayerMap(playerName).put(0, new ProfessionEntry(profession, 0));
		return true;
	}
	
	public synchronized boolean setMinor(String playerName, String profession){
		if(!isAProfession(profession) || 
		   getMinorEntry(playerName)!=null ||  //already has one
		   getMajorEntry(playerName)==null ||  //no major
		   getMajorLevel(playerName)<mp.getConfig().getInt("minor-require") || //major level too low
		   getMajorEntry(playerName).profession.equals(profession)){           //same as major
			return false;
		}

		getPlayerMap(playerName).put(1, new ProfessionEntry(profession, 0));
		return true;
	}

	public synchronized boolean promote(String playerName){
		HashMap<Integer, ProfessionEntry> map = getPlayerMap(playerName);
		if(map.get(1)!=null){
			map.put(0, map.remove(1));
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
	
	public synchronized void gainExperience(String playerName, String professionName, Double expAmount){
		mp.debug(this, "gainExperience: ",playerName, professionName, expAmount);
		if(expAmount == null){
			return;
		}
		ProfessionEntry entry = getProfessionEntry(playerName, professionName);
		if(entry!=null){
			mp.debug(this, "gainExperience.inner: ",playerName, professionName, expAmount);
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
		}
	}
	
	public synchronized void judgeLevel(String playerName){
		judgeLevel(getMajorEntry(playerName), mp.getConfig().getInt("max-major-level"));
		judgeLevel(getMinorEntry(playerName), mp.getConfig().getInt("max-minor-level"));
	}
	
	private void judgeLevel(ProfessionEntry entry, int maxLevel){
		if(entry == null){
			return;
		}
		
		while(entry.experience >= getExperienceToLevel(entry.level+1) && entry.level < maxLevel){
			++entry.level;
		}
	
		while(entry.experience < getExperienceToLevel(entry.level) && entry.level>1){
			--entry.level;
		}
	}
	
	public synchronized double getProfessionPower(String playerName, String professionName){
		ProfessionEntry entry = getProfessionEntry(playerName, professionName);
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
		if(level==1){
			return 0;
		}
		HashMap<String, Double> map = new HashMap<String, Double>();
		map.put("maxLevel", (double)Math.max(mp.getConfig().getInt("max-major-level"), mp.getConfig().getInt("max-minor-level")));
		map.put("level", (double)level);
		return (int)expFormula.value(map);
	}
	
	public int getMajorMaxLevel(){
		return mp.getConfig().getInt("max-major-level");
	}
	
	public int getMinorMaxLevel(){
		return mp.getConfig().getInt("max-minor-level");
	}
}