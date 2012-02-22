package tw.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import tw.lachu.MineProfession.MineProfession;

public class PromoteSelf implements MyCommand {

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		String name = sender.getName();
		synchronized(mp.data){
			String pro = mp.data.getMinor(name);
			int level = mp.data.getMinorLevel(name);
			double exp = mp.data.getMinorExperience(name);
			if(pro!=null){
				mp.data.clearMajor(name);
				mp.data.setMajor(name, pro);
				mp.data.setMajorLevel(name, level);
				mp.data.gainExperience(name, pro, exp);
				sender.sendMessage(ChatColor.GREEN+"Your major profession is set to "+pro+".");
			}else{
				sender.sendMessage(ChatColor.RED+"You do not have a minor profession.");
			}
		}
		return true;
	}

}
