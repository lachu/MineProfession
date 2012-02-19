package com.github.lachu.MineProfession.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.lachu.MineProfession.MineProfession;

public class List implements MyCommand{

	@Override
	public boolean execute(MineProfession mp, CommandSender sender, Command cmd, String[] args) {
		//TODO
		String[] list = mp.data.getProfessionsWithDescription();
		for(String pro:list){
			sender.sendMessage(pro);
		}
		return true;
	}

}
