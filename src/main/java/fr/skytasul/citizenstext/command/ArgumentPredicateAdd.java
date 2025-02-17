package fr.skytasul.citizenstext.command;

import fr.skytasul.citizenstext.options.OptionMessageStates;
import fr.skytasul.citizenstext.predicates.NamedPredicate;
import fr.skytasul.citizenstext.predicates.PredicateManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArgumentPredicateAdd extends TextCommandArgument<OptionMessageStates> {

	public ArgumentPredicateAdd() {
		super("addpred", "addpred", OptionMessageStates.class);
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
		String msg = String.join(" ", args);
		NamedPredicate n = null;
		try {
			n = (NamedPredicate) (PredicateManager.preds.get(msg)).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		option.addPredicate(n);
		sender.sendMessage(ChatColor.GREEN + "Succesfully added predicate \"" + option.getPredicateText() + "\".");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args, OptionMessageStates option) {
		if (option != null && args.length == 1) {
			return new ArrayList<>(PredicateManager.preds.keySet());
		}
		return Collections.emptyList();
	}
	
	@Override
	public String getHelpSyntax() {
		return super.getHelpSyntax() + " <predicate>";
	}
	
	@Override
	protected String getHelpDescription() {
		return "Add a predicate for text. Make sure default is the last one added.";
	}
	
}
