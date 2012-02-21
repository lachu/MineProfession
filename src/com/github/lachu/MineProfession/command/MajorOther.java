package com.github.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.lachu.MineProfession.MineProfession;

public class MajorOther implements MyCommand{

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		Player target;
		if((target=mp.getServer().getPlayer(args[2]))==null){
			sender.sendMessage(ChatColor.RED+"Cannot find player "+args[2]+".");
		}else if(!mp.data.isAProfession(args[1])){
			sender.sendMessage(ChatColor.RED+"No such profession.");
		}else if(mp.data.setMajor(target.getName(), args[1])){
			sender.sendMessage(ChatColor.GREEN+args[2]+"'s major is set to "+args[1]+".");
			target.sendMessage(ChatColor.YELLOW+"Your major is set to "+args[1]+" by "+sender.getName()+".");
		}else{
			sender.sendMessage(ChatColor.RED+args[2]+" already has a major profession.");
		}
		return true;
	}

}
