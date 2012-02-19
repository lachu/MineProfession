package com.github.lachu.MineProfession.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.lachu.MineProfession.MineProfession;

public class Help implements MyCommand{

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		// TODO Auto-generated method stub
		String usage = cmd.getUsage();
		String[] lines = usage.split("\n");
		for(String line:lines){
			line = line.replaceAll("\\<command\\>","mineprofession");
			String[] temp = line.split(" \\(");
			if( sender.hasPermission( temp[1].trim().substring(0, temp[1].length()-1).trim() ) ){
				sender.sendMessage(temp[0].trim());
			}
		}
		return true;
	}
}
