package fr.skytasul.citizenstext.command;

import fr.skytasul.citizenstext.options.OptionMessageStates;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentTextDelete extends TextCommandArgument<OptionMessageStates> {
	
	public ArgumentTextDelete() {
		super("delete", "delete", OptionMessageStates.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessageStates option) {
		if (option.messagesSize() > 0) {
			sender.sendMessage("§cFor security, please clear all messages before deleting.");
		}else {
			option.getTextInstance().delete();
			sender.sendMessage("§aText instance deleted.");
		}
		return false;
	}
	
	@Override
	public String getHelpDescription() {
		return "Delete this text instance to free space";
	}
	
}
