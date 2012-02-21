package com.github.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.lachu.MineProfession.MineProfession;

public class ClearMinorOther implements MyCommand {

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		Player target;
		if((target=mp.getServer().getPlayer(args[1]))!=null){
			if(mp.data.clearMinor(args[1])){
				sender.sendMessage(ChatColor.GREEN+args[1]+"'s minor profession is cleared.");
				target.sendMessage(ChatColor.YELLOW+"Your minor profession is cleared by "+sender.getName()+".");
			}else{
				sender.sendMessage(ChatColor.RED+args[1]+" do not have a minor profession.");
			}
		}else{
			sender.sendMessage(ChatColor.RED+"Cannot find player "+args[1]+".");
		}
		return true;
	}

}
