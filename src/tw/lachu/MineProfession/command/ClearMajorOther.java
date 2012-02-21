package tw.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tw.lachu.MineProfession.MineProfession;


public class ClearMajorOther implements MyCommand {

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		Player target;
		if((target=mp.getServer().getPlayer(args[1]))!=null){
			if(mp.data.clearMajor(args[1])){
				sender.sendMessage(ChatColor.GREEN+args[1]+"'s major and minor profession are cleared.");
				target.sendMessage(ChatColor.YELLOW+"Your major and minor profession are cleared by "+sender.getName()+".");
			}else{
				sender.sendMessage(ChatColor.RED+args[1]+" do not have a major profession.");
			}
		}else{
			sender.sendMessage(ChatColor.RED+"Cannot find player "+args[1]+".");
		}
		return true;
	}

}
