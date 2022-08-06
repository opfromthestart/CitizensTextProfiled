package fr.skytasul.citizenstext.command;

import fr.skytasul.citizenstext.options.OptionMessageStates;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArgumentPredicateEdit extends TextCommandArgument<OptionMessageStates> {

	public ArgumentPredicateEdit() {
		super("editpred", "editpred", OptionMessageStates.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessageStates option) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "You must specify a tag and a value.");
			return false;
		}

		HashMap<String, Object> data = new HashMap<>();
		data.put(args[0], args[1]);
		option.editPredicate(data);
		sender.sendMessage(ChatColor.GREEN + "Succesfully edited field \"" + args[0] + "\"§r§a to " + args[1] + ".");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args, OptionMessageStates option) {
		if (option != null && args.length == 1) {
			return IntStream.range(0, option.messagesSize()).mapToObj(Integer::toString).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
	
	@Override
	public String getHelpSyntax() {
		return super.getHelpSyntax() + " <id> <message>";
	}
	
	@Override
	protected String getHelpDescription() {
		return "Edit a previously created message";
	}
	
}
