package fr.skytasul.citizenstext.command;

import fr.skytasul.citizenstext.options.OptionMessageStates;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentMessageClear extends TextCommandArgument<OptionMessageStates> {
	
	public ArgumentMessageClear() {
		super("clear", "clear", OptionMessageStates.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessageStates option) {
		sender.sendMessage(ChatColor.GREEN.toString() + option.clear() + " messages removed.");
		return true;
	}
	
	@Override
	public String getHelpDescription() {
		return "Clear all messages";
	}
	
}
