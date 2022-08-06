package fr.skytasul.citizenstext.command.message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.skytasul.citizenstext.options.OptionMessageStates;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.message.Message;
import fr.skytasul.citizenstext.message.TextSender;
import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentMessageSetSender extends MessageCommandArgument {
	
	private static final List<String> SENDERS = Arrays.asList("npc", "player", "noSender", "othernpc", "otherformat");
	
	public ArgumentMessageSetSender() {
		super("sender", "sender");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessageStates option, Message message) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "You must specify a sender. Available: " + String.join(" ", SENDERS));
			return false;
		}
		TextSender newSender;
		String senderArg = args[0].toLowerCase();
		switch (senderArg) {
			case "npc":
				newSender = TextSender.NPC_SENDER;
				break;
			case "player":
				newSender = TextSender.PLAYER_SENDER;
				break;
			case "nosender":
				newSender = TextSender.NO_SENDER;
				break;
			case "othernpc":
				if (args.length == 1) {
					sender.sendMessage(ChatColor.RED + "You must specify a NPC name.");
					return false;
				}
				newSender = new TextSender.FixedNPCSender(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
				break;
			case "otherformat":
				if (args.length == 1) {
					sender.sendMessage(ChatColor.RED + "You must specify a format. Placeholders: {1} = message, {2} = message id, {3} = messages size");
					return false;
				}
				newSender = new TextSender.FixedFormatSender(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
				break;
			default:
				sender.sendMessage(ChatColor.RED + "This sender does not exist. Available: " + String.join(" ", SENDERS));
				return false;
		}
		message.setSender(newSender);
		sender.sendMessage(ChatColor.GREEN + "You have modified the sender of the message.");
		return true;
	}
	
	@Override
	public List<String> onTabCompleteMessage(CommandSender sender, String[] args, OptionMessageStates option, String argCmdId) {
		if (args.length == 1) return SENDERS;
		return Collections.emptyList();
	}
	
	@Override
	public String getHelpSyntax() {
		return super.getHelpSyntax() + " <sender type> ...";
	}
	
	@Override
	protected String getHelpDescription() {
		return "Set the sender of a message";
	}
	
}
