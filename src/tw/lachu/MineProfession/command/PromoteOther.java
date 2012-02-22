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
			synchronized(mp.data){
				String pro = mp.data.getMinor(name);
				int level = mp.data.getMinorLevel(name);
				double exp = mp.data.getMinorExperience(name);
				if(pro!=null){
					mp.data.clearMajor(name);
					mp.data.setMajor(name, pro);
					mp.data.setMajorLevel(name, level);
					mp.data.gainExperience(name, pro, exp);
					sender.sendMessage(ChatColor.GREEN+name+"'s major profession is set to "+pro+".");
					target.sendMessage(ChatColor.YELLOW+"Your are promoted by "+sender.getName()+".");
				}else{
					sender.sendMessage(ChatColor.RED+"You do not have a minor profession.");
				}
			}
		}else{
			sender.sendMessage(ChatColor.RED+"Cannot find player "+args[2]+".");
		}
		return true;
	}

}
