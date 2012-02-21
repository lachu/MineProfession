package com.github.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.lachu.MineProfession.MineProfession;

public class MinorSelf implements MyCommand{

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		if(!mp.data.isAProfession(args[1])){
			sender.sendMessage(ChatColor.YELLOW+"No such profession.");
		}else if(mp.data.setMinor(sender.getName(), args[1])){
			sender.sendMessage(ChatColor.GREEN+"Your major profession is now "+args[1]+".");
		}else if(mp.data.getMinor(sender.getName())==null){
			sender.sendMessage(ChatColor.YELLOW+"You cannot have a minor profession.");
		}else{
			sender.sendMessage(ChatColor.YELLOW+"You already have a minor profession.");
		}
		return true;
	}

}
