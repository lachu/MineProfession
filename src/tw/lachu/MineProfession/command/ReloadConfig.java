package tw.lachu.MineProfession.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import tw.lachu.MineProfession.MineProfession;

public class ReloadConfig implements MyCommand {

	@Override
	public boolean execute(MineProfession mp, CommandSender sender,	Command cmd, String[] args) {
		mp.reloadConfig();
		sender.sendMessage(ChatColor.GREEN+"MineProfession: Configuration reloaded.");
		return true;
	}

}
