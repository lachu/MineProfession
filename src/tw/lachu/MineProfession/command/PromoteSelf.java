package tw.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import tw.lachu.MineProfession.MineProfession;

public class PromoteSelf implements MyCommand {

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		
		String name = sender.getName();
		if(mp.data.promote(name)){
			sender.sendMessage(ChatColor.GREEN+"Your major profession is set to "+mp.data.getMajor(name)+".");
		}else{
			sender.sendMessage(ChatColor.RED+name+"You do not have a minor profession.");
		}
		return true;
		
	}

}
