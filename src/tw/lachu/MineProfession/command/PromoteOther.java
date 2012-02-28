package tw.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tw.lachu.MineProfession.MineProfession;

public class PromoteOther implements MyCommand {

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		String name = args[1];
		Player target;
		if((target=mp.getServer().getPlayer(name))!=null){
			if(mp.data.promote(name)){
				sender.sendMessage(ChatColor.GREEN+name+"'s major profession is set to "+mp.data.getMajor(name)+".");
				target.sendMessage(ChatColor.YELLOW+"Your are promoted by "+sender.getName()+".");
			}else{
				sender.sendMessage(ChatColor.RED+name+" do not have a minor profession.");
			}
		}else{
			sender.sendMessage(ChatColor.RED+"Cannot find player "+args[2]+".");
		}
		return true;
	}

}
