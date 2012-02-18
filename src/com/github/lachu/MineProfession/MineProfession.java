package com.github.lachu.MineProfession;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class MineProfession extends JavaPlugin{
	Logger log = Logger.getLogger("minecraft");
	
	public void onEnable(){
		log.info("MineProfession has been enabled!");
	}
	
	public void onDisable(){
		log.info("MineProfession has been disabled.");
	}
}
