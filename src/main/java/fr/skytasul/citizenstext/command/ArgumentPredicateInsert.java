package fr.skytasul.citizenstext.command;

import fr.skytasul.citizenstext.options.OptionMessageStates;
import fr.skytasul.citizenstext.predicates.NamedPredicate;
import fr.skytasul.citizenstext.predicates.PredicateManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArgumentPredicateInsert extends TextCommandArgument<OptionMessageStates> {

	public ArgumentPredicateInsert() {
		super("insertpred", "insertpred", OptionMessageStates.class);
	}
	
	@Override
	public boolean createTextInstance() {
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessageStates option) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "You must specify an ID and a predicate.");
			return false;
		}
		
		try {
			int id = Integer.parseInt(args[0]);
			if (id < 0 || id > option.predicatesSize()) {
				sender.sendMessage(ChatColor.RED + "The number you have entered (" + id + ") must be between 0 and " + option.predicatesSize() + ".");
				return false;
			}
			String predName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
			NamedPredicate n = (NamedPredicate) PredicateManager.preds.get(predName).newInstance();
			option.insertPredicate(id, n);
			sender.sendMessage(ChatColor.GREEN + "Succesfully inserted predicate \"" + n.getDesc() + "\"§r§a at the position " + id + ".");
			return true;
		}catch (IllegalArgumentException ex) {
			sender.sendMessage(ChatColor.RED + "This is not a valid number.");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args, OptionMessageStates option) {
		if (option != null && args.length == 1) {
			return IntStream.range(0, option.predicatesSize()).mapToObj(Integer::toString).collect(Collectors.toList());
		}
		if (option != null && args.length == 2) {
			return new ArrayList<>(PredicateManager.preds.keySet());
		}
		return Collections.emptyList();
	}
	
	@Override
	public String getHelpSyntax() {
		return super.getHelpSyntax() + " <id> <message>";
	}
	
	@Override
	protected String getHelpDescription() {
		return "Insert a message";
	}
	
}
