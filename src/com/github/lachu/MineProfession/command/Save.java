package com.github.lachu.MineProfession.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.lachu.MineProfession.MineProfession;

public class Save implements MyCommand {

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		mp.data.saveTable(mp.getConfig().getBoolean("backup"));
		return true;
	}

}
