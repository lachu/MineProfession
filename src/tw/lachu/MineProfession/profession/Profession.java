package tw.lachu.MineProfession.profession;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import tw.lachu.MineProfession.MineProfession;

public class Profession implements Listener{
	
	private final String name;
	private final ConfigurationSection con0;
	private MineProfession mp;
	
	public HashMap<String, Double> experienceBlockBreak;
	public HashMap<String, Double> experienceBlockPlace;
	public Profession(MineProfession mp, String name, ConfigurationSection con0){
		this.mp = mp;
		this.name = name;
		this.con0 = con0;
		experienceBlockBreak = new HashMap<String,Double>();
		experienceBlockPlace = new HashMap<String,Double>();
	}
	
	@SuppressWarnings("unchecked")
	public void load(){
		Set<String> depth1 = con0.getKeys(false);
		for(String str1 : depth1){
			ConfigurationSection con1 = con0.getConfigurationSection(str1);
			Set<String> depth2 = con1.getKeys(false);
			for(String str2 : depth2){
				ConfigurationSection con2 = con1.getConfigurationSection(str2);
				Set<String> depth3 = con2.getKeys(false);
					for(String str3 : depth3){
						try {
							((HashMap<String, Double>)(Profession.class.getField(str1+str2).get(this))).put(str3, Double.valueOf(con2.getDouble(str3)));
							mp.log.info(str1+str2+".put("+str3+", "+con2.getDouble(str3)+")");
						} catch (IllegalArgumentException e) {
							mp.log.info("IllegalArgumentException");
							// TODO Auto-generated catch block
							//e.printStackTrace();
						} catch (IllegalAccessException e) {
							mp.log.info("IllegalAccessException");
							// TODO Auto-generated catch block
							//e.printStackTrace();
						} catch (NoSuchFieldException e) {
							mp.log.info("NoSuchFieldException");
							// TODO Auto-generated catch block
							//e.printStackTrace();
						} catch (SecurityException e) {
							mp.log.info("SecurityException");
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Double gain = experienceBlockBreak.get(event.getBlock().getType().name()); 
		if(gain!=null){
			mp.data.gainExperience(event.getPlayer().getName(), this.name, gain.doubleValue());
		}
	}
}



//BIG LISTENER - receiving all event and pass it to "Profession"s according to the player
//Profession - dealt with it


