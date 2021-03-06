package tw.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import tw.lachu.MineProfession.MineProfession;


public class QueryOther implements MyCommand{

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		String major; 
		String name = args[1];
		synchronized(mp.data){
			if((major=mp.data.getMajor(name))!=null){
				sender.sendMessage("Major: "+ChatColor.GREEN+major);
				sender.sendMessage("Level: "+ChatColor.GREEN+mp.data.getMajorLevel(name));
				sender.sendMessage("Experience: "+ChatColor.GREEN+mp.data.getMajorExperience(name));
				if(mp.data.getMajorLevel(name) < mp.data.getMajorMaxLevel()){
					sender.sendMessage("To Next Level: "+ChatColor.GREEN+(mp.data.getExperienceToLevel(mp.data.getMajorLevel(name)+1)-mp.data.getMajorExperience(name)));
				}
				String minor;
				if((minor=mp.data.getMinor(name))!=null){
					sender.sendMessage("Minor: "+ChatColor.GREEN+minor);
					sender.sendMessage("Level: "+ChatColor.GREEN+mp.data.getMinorLevel(name));
					sender.sendMessage("Experience: "+ChatColor.GREEN+mp.data.getMinorExperience(name));
					if(mp.data.getMinorLevel(name) < mp.data.getMinorMaxLevel()){
						sender.sendMessage("To Next Level: "+ChatColor.GREEN+(mp.data.getExperienceToLevel(mp.data.getMinorLevel(name)+1)-mp.data.getMinorExperience(name)));
					}
				}else{
					sender.sendMessage("Minor: "+ChatColor.GREEN+"none");
				}
			}else{
				sender.sendMessage(ChatColor.RED+name+" do not have a profession.");
			}
		}
		return true;
	}
	
}
