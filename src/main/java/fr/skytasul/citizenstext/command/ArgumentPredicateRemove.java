package fr.skytasul.citizenstext.command;

import fr.skytasul.citizenstext.options.OptionMessageStates;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ArgumentPredicateRemove extends TextCommandArgument<OptionMessageStates> {

	public ArgumentPredicateRemove() {
		super("removepred", "removepred", OptionMessageStates.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessageStates option) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "You must specify an ID.");
			return false;
		}
		int id;
		try {
			id = Integer.parseInt(args[0]);
		}catch (IllegalArgumentException ex) {
			sender.sendMessage(ChatColor.RED + "\"" + args[0] + "\" isn't a valid number.");
			return false;
		}
		try {
			sender.sendMessage(ChatColor.GREEN + "Succesfully removed predicate \"" + option.removePredicate(id) + "\".");
			return true;
		}catch (IndexOutOfBoundsException ex) {
			sender.sendMessage(ChatColor.RED + "The number you have entered (" + id + ") is too big. It must be between 0 and " + (option.predicatesSize() - 1) + ".");
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args, OptionMessageStates option) {
		if (option != null && args.length == 1) {
			return IntStream.range(0, option.predicatesSize()).mapToObj(Integer::toString).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
	
	@Override
	public String getHelpSyntax() {
		return super.getHelpSyntax() + " <id>";
	}
	
	@Override
	protected String getHelpDescription() {
		return "Remove a message";
	}
	
}
