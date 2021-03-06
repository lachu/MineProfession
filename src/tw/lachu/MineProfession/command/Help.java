package tw.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import tw.lachu.MineProfession.MineProfession;


public class Help implements MyCommand{

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		// TODO Auto-generated method stub
		String usage = cmd.getUsage();
		String[] lines = usage.split("\n");
		for(String line:lines){
			line = line.replaceAll("\\<command\\>",cmd.getName());
			String[] temp = line.split(" \\(");
			if( sender.hasPermission( temp[1].trim().substring(0, temp[1].length()-1).trim() ) ){
				temp = temp[0].split("-");
				sender.sendMessage(ChatColor.GREEN+temp[0]+ChatColor.WHITE+"-"+temp[1]);
			}
		}
		return true;
	}
}
