package fr.skytasul.citizenstext.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import fr.skytasul.citizenstext.options.OptionMessageStates;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.message.Message;
import fr.skytasul.citizenstext.options.OptionMessages;

import net.citizensnpcs.trait.text.Text;

public class ArgumentTextConvert extends TextCommandArgument<OptionMessageStates> {
	
	public ArgumentTextConvert() {
		super("convert", "convert", OptionMessageStates.class);
	}
	
	@Override
	public boolean createTextInstance() {
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessageStates option) {
		Text trait = option.getTextInstance().getNPC().getTraitNullable(Text.class);
		if (trait == null) {
			sender.sendMessage(ChatColor.RED + "This NPC does not have the Text trait.");
			return false;
		}
		try {
			Field f = trait.getClass().getDeclaredField("text");
			f.setAccessible(true);
			List<String> ls = new ArrayList<>((List<String>) f.get(trait));
			for (Message s : option.getSelectedMessages()) {
				ls.remove(s.getText());
			}
			for (String s : ls) {
				option.addMessage(s);
			}
			sender.sendMessage(ChatColor.GREEN.toString() + ls.size() + " messages added.");
			option.getTextInstance().getNPC().removeTrait(Text.class);
			return true;
		}catch (ReflectiveOperationException e) {
			sender.sendMessage("§cError during conversion. Please contact an administrator.");
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public String getHelpDescription() {
		return "Add all messages from default NPC Text trait";
	}
	
}
