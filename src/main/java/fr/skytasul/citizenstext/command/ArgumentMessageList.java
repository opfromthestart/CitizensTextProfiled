package fr.skytasul.citizenstext.command;

import fr.skytasul.citizenstext.options.OptionMessageStates;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentMessageList extends TextCommandArgument<OptionMessageStates> {
	
	public ArgumentMessageList() {
		super("list", "list", OptionMessageStates.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessageStates option) {
		String list = ChatColor.GREEN + "List of messages for §6" + option.getTextInstance().getNPCName() + "§a and predicate §6 " + option.getPredicateText() + " §a:\n§r" + option.listMessages();
		sender.sendMessage(list);
		return false;
	}
	
	@Override
	public String getHelpDescription() {
		return "List all messages/IDs";
	}
	
}
