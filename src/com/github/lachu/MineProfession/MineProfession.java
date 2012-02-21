package com.github.lachu.MineProfession;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.lachu.MineProfession.profession.ProfessionManager;

public class MineProfession extends JavaPlugin{
	
	public final Logger log = Logger.getLogger("minecraft");
	private CommandInvoker ci;
	public ProfessionData data;
	public ProfessionManager pm;
	
	public void onEnable(){
		//print a message.
		log.info("MineProfession has been enabled!");
		if(getDataFile("config.yml",true)==null){
			saveDefaultConfig();
		}
		reloadConfig();
		
		//initialize command handler
		ci = new CommandInvoker(this);
		getCommand("mineprofession").setExecutor(ci);
		
		//read profession file
		pm = new ProfessionManager(this, getDataFile("profession.yml",true));
		pm.load();
		
		//read player data
		data = new ProfessionData(this, getDataFile("playerTable",false), getDataFile("profession.yml",true));
		
		//schedule saveTable
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable(){
			public void run(){
				data.saveTable(getConfig().getBoolean("backup"));
			}
		}, getConfig().getLong("auto-save-cycle")*20, getConfig().getLong("auto-save-cycle")*20);
	}
	
	public void onDisable(){
		log.info("MineProfession has been disabled.");
		data.saveTable(getConfig().getBoolean("backup"));
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
