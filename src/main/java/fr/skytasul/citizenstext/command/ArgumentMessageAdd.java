package fr.skytasul.citizenstext.command;

import fr.skytasul.citizenstext.options.OptionMessageStates;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentMessageAdd extends TextCommandArgument<OptionMessageStates> {
	
	public ArgumentMessageAdd() {
		super("add", "add", OptionMessageStates.class);
	}
	
	@Override
	public boolean createTextInstance() {
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessageStates option) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "You must specify a message.");
			return false;
		}
		String msg = String.join(" ", args);
		option.addMessage(msg);
		sender.sendMessage(ChatColor.GREEN + "Succesfully added message \"" + msg + "\".");
		return true;
	}
	
	@Override
	public String getHelpSyntax() {
		return super.getHelpSyntax() + " <message>";
	}
	
	@Override
	protected String getHelpDescription() {
		return "Add a message (use {nl} to skip a line, or {PLAYER} to use the players name)";
	}
	
}
