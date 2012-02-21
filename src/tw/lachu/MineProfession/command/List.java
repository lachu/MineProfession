package tw.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import tw.lachu.MineProfession.MineProfession;


public class List implements MyCommand{

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		//TODO
		String[] list = mp.data.getProfessions();
		for(String pro:list){
			sender.sendMessage(ChatColor.GREEN+pro+ChatColor.WHITE+" - "+mp.data.getDescription(pro));
		}
		return true;
	}

}
