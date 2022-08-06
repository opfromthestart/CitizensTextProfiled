package fr.skytasul.citizenstext.texts;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import fr.skytasul.citizenstext.options.*;
import fr.skytasul.citizenstext.predicates.NamedPredicate;
import fr.skytasul.citizenstext.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.skytasul.citizenstext.CitizensText;
import fr.skytasul.citizenstext.CitizensTextConfiguration;
import fr.skytasul.citizenstext.CitizensTextConfiguration.ClickType;
import fr.skytasul.citizenstext.event.TextSendEvent;
import fr.skytasul.citizenstext.message.Message;
import fr.skytasul.citizenstext.options.TextOptionsRegistry.TextOptionType;
import fr.skytasul.citizenstext.players.CTPlayer;
import fr.skytasul.citizenstext.players.CTPlayerText;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class TextInstance implements Listener{

	private final Map<Class<?>, TextOption<?>> options = new HashMap<>();
	
	private NPC npc;
	private final int npc_id;
	private boolean created = false;
	private ConfigurationSection config;
	
	public TextInstance(NPC npc){
		this(npc, null);
	}
	
	public TextInstance(NPC npc, ConfigurationSection config) {
		this.npc_id = npc.getId();
		this.npc = npc;
		this.config = config;
	}
	
	public void create() {
		if (!created) {
			Bukkit.getPluginManager().registerEvents(this, CitizensText.getInstance());
			CitizensText.getInstance().getTexts().addText(this);
			created = true;
			if (config == null) {
				CitizensText.getInstance().getTexts().createConfig(this);
			}else {
				for (String key : config.getKeys(false)) {
					TextOptionType<?> optionType = CitizensText.getInstance().getOptionsRegistry().getOptionType(key);
					if (optionType == null) {
						if (!key.equalsIgnoreCase("npc")) CitizensText.getInstance().getLogger().warning("Unknown key " + key + " in " + npc_id + " data section");
					}else {
						TextOption<?> option = optionType.createOption(this);
						option.loadValue(config);
						options.put(optionType.getOptionClass(), option);
					}
				}
			}
		}
	}
	
	protected void createConfig(ConfigurationSection config) {
		this.config = config;
		for (TextOption<?> option : options.values()) {
			option.saveValue();
		}
		config.set("npc", npc_id);
	}
	
	public void saveOption(TextOption<?> option) throws IOException {
		if (config == null) return;
		option.saveValue();
		CitizensText.getInstance().getTexts().save();
	}
	
	public <O extends TextOption<?>> O getOption(Class<O> optionClass) {
		return (O) options.computeIfAbsent(optionClass, x -> {
			O option = CitizensText.getInstance().getOptionsRegistry().createOption(optionClass, this);
			option.setDefaultValue();
			return option;
		});
	}

	public NPC getNPC(){
		return npc;
	}
	
	public ConfigurationSection getConfigurationSection() {
		return config;
	}
	
	public boolean isEmpty(){
		return options.values().stream().allMatch(TextOption::isEmpty);
	}
	
	public boolean isRandom() {
		return getOption(OptionRandom.class).getOrDefault();
	}
	
	public boolean isRepeat() {
		return getOption(OptionRepeat.class).getOrDefault();
	}
	
	public String getNPCName() {
		return getOption(OptionName.class).getOrDefault();
	}
	
	public OptionMessageStates getMessages() {
		return getOption(OptionMessageStates.class);
	}
	
	public void unload(){
		if (!created) return;
		HandlerList.unregisterAll(this);
		CitizensText.getInstance().getTexts().removeText(this);
	}
	
	public void delete() {
		unload();
		Objects.requireNonNull(config.getParent()).set(config.getName(), null);
		try {
			CitizensText.getInstance().getTexts().save();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if (e.getFrom().getBlockX() == Objects.requireNonNull(e.getTo()).getBlockX() && e.getFrom().getBlockY() == e.getTo().getBlockY() && e.getFrom().getBlockZ() == e.getFrom().getBlockZ()) return;
		if (!getOption(OptionNear.class).getOrDefault() || isRandom()) return;
		Player p = e.getPlayer();
		if (!npc.isSpawned() || npc.getEntity().getWorld() != e.getTo().getWorld()) return;
		CTPlayerText playerText = CTPlayer.getPlayer(p).getText(this);
		if (playerText.hasNextMessageTask()) return;
		if (e.getTo().distance(npc.getEntity().getLocation()) < CitizensTextConfiguration.getDistanceToContinue()) {
			send(p, playerText);
		}
	}
	
	@EventHandler
	public void onRemove(NPCRemoveEvent e){
		if (e.getNPC() == npc){
			unload();
			npc = null;
			CitizensText.getInstance().getLogger().info("Text instance of NPC " + npc_id + " is now dead.");
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRightClick(NPCRightClickEvent e){
		click(e, ClickType.RIGHT);
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLeftClick(NPCLeftClickEvent e){
		click(e, ClickType.LEFT);
	}
	
	private void click(NPCClickEvent e, ClickType click) {
		if (!CitizensTextConfiguration.getClicks().contains(click)) return;
		if (getOption(OptionNear.class).getOrDefault()) return;
		if (e.getNPC() == npc){
			send(e.getClicker(), CTPlayer.getPlayer(e.getClicker()).getText(this));
		}
	}
	
	public void send(Player p, CTPlayerText playerText) {
		OptionMessageStates messageStates = getMessages();
		List<Message> messages = null;
		for (Pair<NamedPredicate, List<Message>> pair : messageStates.getValue())
		{
			if (pair.first.test(p))
			{
				messages = pair.second;
				break;
			}
		}

		if (messages == null || messages.size() == 0) return;
		
		if (playerText.hasTime()) {
			if (playerText.getTime() > System.currentTimeMillis() || (!playerText.canRepeat() && !isRepeat())) return;
			playerText.removeTime();
		}
		if (CitizensTextConfiguration.getClickMinimumTime() > 0) playerText.setTime(System.currentTimeMillis() + CitizensTextConfiguration.getClickMinimumTime() * 1000L);
		
		if (isRandom()) {
			int id = ThreadLocalRandom.current().nextInt(messages.size());
			TextSendEvent event = new TextSendEvent(p, this, messages.get(id));
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) event.getMessage().send(p, id, this);
			return;
		}
		int id;
		if (playerText.hasStarted()) { // player has already started
			if (!playerText.hasResetTime() || playerText.getResetTime() > System.currentTimeMillis()) {
				id = playerText.getMessageIndex();
				if (id >= messages.size()) id = 0;
			}else id = 0;
			playerText.removeNextMessageTask();
		}else { // never started
			id = 0;
		}
		Message message = messages.get(id);
		TextSendEvent event = new TextSendEvent(p, this, message);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return;
		message = event.getMessage();
		message.send(p, id, this);
		
		id++;
		if (messages.size() == id) { // last message
			playerText.removeMessage();
			if (isRepeat()) {
				int playback = getOption(OptionPlaybackTime.class).getOrDefault();
				if (playback > 0) playerText.setTime(System.currentTimeMillis() + playback * 1000L);
			}else {
				playerText.setNoRepeat();
			}
			return;
		}
		
		// not last message
		if (CitizensTextConfiguration.getKeepTime() != -1) playerText.setResetTime(System.currentTimeMillis() + CitizensTextConfiguration.getKeepTime() * 1000L);
		playerText.setMessage(id);
		if (message.getDelay() >= 0) { // TASK SYSTEM
			playerText.setNextMessageTask(Bukkit.getScheduler().runTaskLater(CitizensText.getInstance(), () -> { // create the task
				playerText.removeNextMessageTask();
				if (CitizensTextConfiguration.getDistanceToContinue() > 0) {
					Entity entity = npc.getEntity();
					if (entity == null) return;
					if (p.getWorld() != entity.getWorld()) return;
					if (p.getLocation().distance(entity.getLocation()) > CitizensTextConfiguration.getDistanceToContinue()) return; // player too far
				}
				send(p, playerText);
			}, message.getDelay()));
		}
	}
	
	public static void load(ConfigurationSection data) {
		int npcID = data.getInt("npc");
		NPC npc = CitizensAPI.getNPCRegistry().getById(npcID);
		if (npc == null) {
			CitizensText.getInstance().getLogger().warning("NPC with the id " + npcID + " doesn't exist. Consider removing this text instance.");
			return;
		}
		
		new TextInstance(npc, data).create();
	}

}
