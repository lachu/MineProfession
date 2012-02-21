package tw.lachu.MineProfession.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import tw.lachu.MineProfession.MineProfession;


public interface MyCommand {

	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args);
}