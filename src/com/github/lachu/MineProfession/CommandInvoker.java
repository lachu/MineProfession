package com.github.lachu.MineProfession;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.lachu.MineProfession.command.Help;
import com.github.lachu.MineProfession.command.MyCommand;

public class CommandInvoker implements CommandExecutor{
	private MineProfession mp;
	private final HashMap<String,String> commands;
	private final HashMap<String,String> permissions;
	
	public CommandInvoker(MineProfession mineProfession){
		mp = mineProfession;
		commands = new HashMap<String,String>();
		permissions = new HashMap<String,String>();
		{
			String usage = mp.getCommand("mineprofession").getUsage();
			String[] lines = usage.split("\n");
			for(String line:lines){
				String[] temp = line.split("-");
				String command = temp[0].trim();
				
				temp = temp[1].trim().split(" *\\( *");
				String permission = temp[1].substring(0, temp[1].length()-1).trim();
				String className = temp[2].substring(0, temp[2].length()-1).trim();
				
				temp = command.split(" ");
				if(temp.length>1){
					String key = temp[1]+" "+Integer.toString(temp.length-1);
					permissions.put(key, permission);
					commands.put(key, className);
					//mp.log.info("'"+key+"'");
					//mp.log.info("'"+permission+"'");
					//mp.log.info("'"+className+"'");
				}
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if (sender instanceof Player) {
			if (args.length == 0) {
				args = new String[] { "help" };
			}
			String cmdKey = args[0] + " " + Integer.toString(args.length);
			if(!commands.containsKey(cmdKey)){
				sender.sendMessage("MineProfession: Wrong Usage.");
				MyCommand cmdInstance = new Help();
				return cmdInstance.execute(mp, sender, cmd, args);
			}
			try {
				String cmdName = "com.github.lachu.MineProfession.command." + commands.get(cmdKey);
				Class<?> cmdClass = Class.forName(cmdName);
				MyCommand cmdInstance = (MyCommand) cmdClass.newInstance();
				if(sender.hasPermission(permissions.get(cmdKey))){
					return cmdInstance.execute(mp, sender, cmd, args);
				}else{
					sender.sendMessage(ChatColor.RED+"You don't have the permission required.");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				sender.sendMessage("MineProfession: Wrong Usage.");
				MyCommand cmdInstance = new Help();
				return cmdInstance.execute(mp, sender, cmd, args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
