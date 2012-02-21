package tw.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import tw.lachu.MineProfession.MineProfession;


public class MajorSelf implements MyCommand{

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		if(!mp.data.isAProfession(args[1])){
			sender.sendMessage(ChatColor.RED+"No such profession.");
		}else if(mp.data.setMajor(sender.getName(), args[1])){
			sender.sendMessage(ChatColor.GREEN+"Your major profession is set to "+args[1]+".");
		}else{
			sender.sendMessage(ChatColor.RED+"You already have a major profession.");
		}
		return true;
	}

}
