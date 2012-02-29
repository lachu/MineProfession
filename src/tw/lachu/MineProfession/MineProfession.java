package tw.lachu.MineProfession;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import tw.lachu.MineProfession.profession.ProfessionManager;


public class MineProfession extends JavaPlugin{
	
	public final Logger log = Logger.getLogger("minecraft");
	private CommandInvoker mineproCI;
	private CommandInvoker mineproadminCI;
	public ProfessionData data;
	public ProfessionManager pm;
	public TrackPlacement tp;
	public TrackSpawner ts;
	
	public void onEnable(){
		//print a message.
		log.info("MineProfession has been enabled!");
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		reloadConfig();
		
		//initialize command handler
		mineproCI = new CommandInvoker(this, "mineprofession", "tw.lachu.MineProfession.command", true, false);
		getCommand("mineprofession").setExecutor(mineproCI);
		
		mineproadminCI = new CommandInvoker(this, "mineprofessionadmin", "tw.lachu.MineProfession.command", true, true);
		getCommand("mineprofessionadmin").setExecutor(mineproadminCI);
		
		//read profession file
		pm = new ProfessionManager(this, getDataFile("profession.yml",true));
		pm.load();
		
		//read player data
		data = new ProfessionData(this, getDataFile("playerTable",false), getDataFile("profession.yml",true));
		tp = new TrackPlacement(this, getDataFile("trackPlacement",false));
		ts = new TrackSpawner();
		
		//schedule saveTable
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable(){
			public void run(){
				data.saveTable(getConfig().getBoolean("backup"));
				tp.save(getConfig().getBoolean("backup"));
			}
		}, getConfig().getLong("auto-save-cycle")*20, getConfig().getLong("auto-save-cycle")*20);
		
		//register listener
		getServer().getPluginManager().registerEvents(pm, this);
		getServer().getPluginManager().registerEvents(tp, this);
		getServer().getPluginManager().registerEvents(ts, this);
		
	}
	
	public void onDisable(){
		log.info("MineProfession has been disabled.");
		data.saveTable(getConfig().getBoolean("backup"));
		tp.save(getConfig().getBoolean("backup"));
		saveConfig();
	}
	
	private boolean createDataDirectory() {
	    File file = this.getDataFolder();
	    if (!file.isDirectory()){
	        if (!file.mkdirs()) {
	            // failed to create the non existent directory, so failed
	            return false;
	        }
	    }
	    return true;
	}
	private File getDataFile(String filename, boolean mustAlreadyExist) {
	    if (createDataDirectory()) {
	        File file = new File(this.getDataFolder(), filename);
	        if (mustAlreadyExist) {
	            if (file.exists()) {
	                return file;
	            }
	        } else {
	            return file;
	        }
	    }
	    return null;
	}
}
