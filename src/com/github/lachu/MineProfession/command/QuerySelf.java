package com.github.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.lachu.MineProfession.MineProfession;

public class QuerySelf implements MyCommand{

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		String major;
		String name = sender.getName();
		synchronized(mp.data){
			if((major=mp.data.getMajor(name))!=null){
				sender.sendMessage("Major: "+ChatColor.GREEN+major);
				sender.sendMessage("Level: "+ChatColor.GREEN+mp.data.getMajorLevel(name));
				sender.sendMessage("Experience: "+ChatColor.GREEN+mp.data.getMajorExperience(name));
				String minor;
				if((minor=mp.data.getMinor(name))!=null){
					sender.sendMessage("Minor: "+ChatColor.GREEN+minor);
					sender.sendMessage("Level: "+ChatColor.GREEN+mp.data.getMinorLevel(name));
					sender.sendMessage("Experience: "+ChatColor.GREEN+mp.data.getMinorExperience(name));
				}else{
					sender.sendMessage("Minor: "+ChatColor.GREEN+"none");
				}
			}else{
				sender.sendMessage(ChatColor.YELLOW+"You do not have a profession.");
			}
		}
		return true;
	}

}
