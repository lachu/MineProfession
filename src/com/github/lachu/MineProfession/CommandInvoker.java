package com.github.lachu.MineProfession;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.lachu.MineProfession.playercommand.PlayerCommand;

public class CommandInvoker implements CommandExecutor{
	private MineProfession mp;
	private static final HashMap<String,String> playerCmdMap;
	static{
		playerCmdMap = new HashMap<>();
		playerCmdMap.put("profession 1", "Profession");
		playerCmdMap.put("player 1", "PlayerSelf");
		playerCmdMap.put("player 2", "PlayerOther");
		playerCmdMap.put("transfer 2", "TransferSelf");
		playerCmdMap.put("transfer 3", "TransferOther");
		playerCmdMap.put("second 2", "SecondSelf");
		playerCmdMap.put("second 3", "SecondOther");
	}
	
	public CommandInvoker(MineProfession mineProfession){
		mp = mineProfession;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if(sender instanceof Player){
			String cmdKey = args[0]+" "+Integer.toString(args.length);
				try {
					String cmdName ="com.github.lachu.MineProfession.playercommand."+playerCmdMap.get(cmdKey);
					Class<?> cmdClass = Class.forName(cmdName);
					PlayerCommand cmdInstance = (PlayerCommand)cmdClass.newInstance();
					return cmdInstance.execute(mp, sender, cmd, args);
				} catch (ClassNotFoundException e) {
					mp.log.info(cmd.getUsage());
					e.printStackTrace();
				} catch (Exception e){
					e.printStackTrace();
				}
		}else{
			
		}
		return false;
	}
}
