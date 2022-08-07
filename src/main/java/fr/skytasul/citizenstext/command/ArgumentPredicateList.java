package fr.skytasul.citizenstext.command;

import fr.skytasul.citizenstext.options.OptionMessageStates;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ArgumentPredicateList extends TextCommandArgument<OptionMessageStates> {

	public ArgumentPredicateList() {
		super("listpred", "listpred", OptionMessageStates.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessageStates option) {
		String list = ChatColor.GREEN + "List of predicates for §6" + option.getTextInstance().getNPCName() + "§a is " + option.listPredicates();
		sender.sendMessage(list);
		return false;
	}
	
	@Override
	public String getHelpDescription() {
		return "List all predicates/IDs";
	}
	
}
