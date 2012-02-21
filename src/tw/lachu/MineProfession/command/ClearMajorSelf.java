package tw.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import tw.lachu.MineProfession.MineProfession;


public class ClearMajorSelf implements MyCommand {

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		if(mp.data.clearMajor(sender.getName())){
			sender.sendMessage(ChatColor.GREEN+"Your major and minor profession are cleared.");
		}else{
			sender.sendMessage(ChatColor.RED+"You do not have a major profession.");
		}
		return true;
	}
	
}
