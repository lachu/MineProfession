package com.github.lachu.MineProfession.playercommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.lachu.MineProfession.MineProfession;

public abstract class PlayerCommand {
	public abstract boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args);
}
