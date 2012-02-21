package tw.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import tw.lachu.MineProfession.MineProfession;


public class ClearMinorSelf implements MyCommand {

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		if(mp.data.clearMinor(sender.getName())){
			sender.sendMessage(ChatColor.GREEN+"Your minor profession is cleared.");
		}else{
			sender.sendMessage(ChatColor.RED+"You do not have a minor profession.");
		}
		return true;
	}

}
