package fr.skytasul.citizenstext;

import fr.skytasul.citizenstext.command.TextCommand;
import fr.skytasul.citizenstext.options.TextOptionsRegistry;
import fr.skytasul.citizenstext.players.CTPlayersManager;
import fr.skytasul.citizenstext.predicates.DefaultPredicate;
import fr.skytasul.citizenstext.predicates.PredicateManager;
import fr.skytasul.citizenstext.predicates.QuestFinishPredicate;
import fr.skytasul.citizenstext.predicates.ScoreboardEqualsPredicate;
import fr.skytasul.citizenstext.texts.TextsManager;
import net.citizensnpcs.api.event.CitizensPreReloadEvent;
import net.citizensnpcs.api.event.CitizensReloadEvent;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Plugin's main class. Fetch the instance using {@link #getInstance()}
 * @author SkytAsul
 */
public class CitizensText extends JavaPlugin implements Listener {
	
	private static CitizensText instance;

	private boolean enabled = false;
	private boolean disabled = false;

	private fr.skytasul.citizenstext.texts.TextsManager texts;
	private fr.skytasul.citizenstext.players.CTPlayersManager players;
	
	public boolean papi;
	
	private TextCommand command;
	private TextOptionsRegistry optionsRegistry;
	
	@Override
	public void onLoad() {
		instance = this;
	}
	
	@Override
	public void onEnable(){
		command = new TextCommand();
		Objects.requireNonNull(getCommand("text")).setExecutor(command);
		Objects.requireNonNull(getCommand("text")).setTabCompleter(command);

		PredicateManager.register(DefaultPredicate.class, "default");
		PredicateManager.register(QuestFinishPredicate.class, "finishquest");
		
		saveDefaultConfig();

		CitizensTextConfiguration.loadConfig(getConfig());

		optionsRegistry = new TextOptionsRegistry();
		
		papi = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
		if (papi) getLogger().info("Hooked into PlaceholderAPI!");
		
		getServer().getScheduler().runTaskLater(this, this::loadDatas, 3L);
	}
	
	@Override
	public void onDisable(){
		if (disabled) return;
		disable();
		enabled = false;
	}
	
	/**
	 * Returns the options registry. Allows adding/fetching options informations.
	 * @return options registry
	 */
	public TextOptionsRegistry getOptionsRegistry() {
		return optionsRegistry;
	}
	
	/**
	 * Returns player datas manager.
	 * @return player datas manager
	 */
	public CTPlayersManager getPlayers() {
		return players;
	}
	
	/**
	 * Returns NPCs texts manager. Allows adding/fetching/removing texts.
	 * @return texts manager
	 */
	public TextsManager getTexts() {
		return texts;
	}
	
	/**
	 * Returns <code>/text</code> command executor
	 * @return main command executor
	 */
	public TextCommand getCommand() {
		return command;
	}
	
	/**
	 * Returns wether or not PlaceholderAPI is enabled.
	 * @return <code>true</code> if PlaceholderAPI is enabled
	 */
	public boolean isPAPIEnabled() {
		return papi;
	}
	
	public void disable() {
		if (texts != null) {
			texts.disable();
			texts = null;
		}
		if (players != null) {
			players.disable();
			players = null;
		}
	}
	
	public void loadDatas() {
		if (getServer().getPluginManager().isPluginEnabled("Citizens")) {
			try {
				texts = new TextsManager(new File(getDataFolder(), "datas.yml"));
				texts.load(this);
				
				players = new CTPlayersManager(this, new File(getDataFolder(), "players.yml"));
				
				if (!enabled) getServer().getPluginManager().registerEvents(instance, instance);
				enabled = true;
			}catch (Throwable e) {
				e.printStackTrace();
				getLogger().severe("An error occurred during data loading. To preserve data integrity, the plugin will now stop.");
				disabled = true;
			}
		}else {
			getLogger().severe("Citizens has not started properly. CitizensText can not work without it, the plugin will now stop.");
			disabled = true;
		}
		
		if (disabled) getServer().getPluginManager().disablePlugin(instance);
	}
	
	@EventHandler
	public void onCitizensPreReload(CitizensPreReloadEvent e){
		getLogger().info("Citizens is reloading - CitizensText datas are saving");
		disable();
	}
	
	@EventHandler
	public void onCitizensReload(CitizensReloadEvent e){
		getLogger().info("Citizens has reloaded - trying to reload CitizensText");
		loadDatas();
	}
	
	
	/**
	 * Returns the plugin's main class instance auto-generated by the Bukkit server
	 * @return plugin instance
	 */
	public static CitizensText getInstance(){
		return instance;
	}
	
	private static final char COLOR_CHAR = '\u00A7';
	
	public static String translateHexColorCodes(String startTag, String endTag, String message) {
		final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
		Matcher matcher = hexPattern.matcher(message);
		StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
		while (matcher.find()) {
			String group = matcher.group(2);
			matcher.appendReplacement(buffer, COLOR_CHAR + "x"
					+ COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
					+ COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
					+ COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
		}
		return matcher.appendTail(buffer).toString();
	}
	
	public static void sendCommand(Player p, String text, String command) {
		BaseComponent[] clicks = TextComponent.fromLegacyText(text);
		for (BaseComponent click : clicks) {
			click.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/" + command));
			click.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CitizensTextConfiguration.getClickableMessage()).create()));
		}
		p.spigot().sendMessage(clicks);
	}
	
	public static String formatMessage(String format, String msg, String name, int id, int size) {
		return format(format(format(format(format, 0, name), 1, msg), 2, "" + id), 3, "" + size).replace("{nl}", "\n");
	}

	public static String format(String msg, int i, String replace){
		return msg.replace("{" + i + "}", replace);
	}
	
}
