package com.github.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.lachu.MineProfession.MineProfession;

public class Save implements MyCommand {

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		if(mp.data.saveTable(mp.getConfig().getBoolean("backup"))){
			sender.sendMessage(ChatColor.GREEN+"Player data successfully saved.");
		}else{
			sender.sendMessage(ChatColor.RED+"Failed to save player data.");
		}
		return true;
	}

}
