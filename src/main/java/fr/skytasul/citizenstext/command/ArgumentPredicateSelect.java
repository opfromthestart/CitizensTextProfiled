package fr.skytasul.citizenstext.command;

import fr.skytasul.citizenstext.options.OptionMessageStates;
import fr.skytasul.citizenstext.predicates.NamedPredicate;
import fr.skytasul.citizenstext.predicates.PredicateManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArgumentPredicateSelect extends TextCommandArgument<OptionMessageStates> {

	public ArgumentPredicateSelect() {
		super("selectpred", "selectpred", OptionMessageStates.class);
	}
	
	@Override
	public boolean createTextInstance() {
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessageStates option) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "You must specify a predicate.");
			return false;
		}
		int id = Integer.parseInt(args[0]);
		option.selectPredicate(id);
		sender.sendMessage(ChatColor.GREEN + "Predicate \"" + option.getPredicateText() + "\" selected.");
		return true;
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
		return super.getHelpSyntax() + " <message>";
	}
	
	@Override
	protected String getHelpDescription() {
		return "Add a predicate for text. Make sure default is the last one added.";
	}
	
}
