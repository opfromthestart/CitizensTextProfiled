package fr.skytasul.citizenstext.options;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import fr.skytasul.citizenstext.texts.TextInstance;

public class TextOptionsRegistry {
	
	private final Map<Class<? extends TextOption<?>>, TextOptionType<?>> options = new HashMap<>();
	private final Map<String, TextOptionType<?>> configMapping = new HashMap<>();
	
	public TextOptionsRegistry() {
		register(new TextOptionType<>(OptionMessageStates.class, OptionMessageStates::new, "predicates"));
		register(new TextOptionType<>(OptionName.class, OptionName::new, "customName"));
		register(new TextOptionType<>(OptionRandom.class, OptionRandom::new, "random"));
		register(new TextOptionType<>(OptionRepeat.class, OptionRepeat::new, "repeat"));
		register(new TextOptionType<>(OptionNear.class, OptionNear::new, "near"));
		register(new TextOptionType<>(OptionPlaybackTime.class, OptionPlaybackTime::new, "playbackTime"));
	}
	
	/**
	 * Registers a new option type
	 * @param optionType informations about the option type
	 */
	public void register(TextOptionType<?> optionType) {
		options.put(optionType.clazz, optionType);
		configMapping.put(optionType.configKey, optionType);
	}
	
	/**
	 * Creates a new option for a specific NPC Text
	 * @param <T> option type
	 * @param clazz Class of the option type wanted
	 * @param txt text for which the option will be created
	 * @return a new option instance associated with the text
	 */
	public <T extends TextOption<?>> T createOption(Class<T> clazz, TextInstance txt) {
		return (T) options.get(clazz).createOption(txt);
	}
	
	/**
	 * Get option informations for the passed class
	 * @param <T> option type
	 * @param clazz Class of the option type wanted
	 * @return option type informations
	 */
	public <T extends TextOption<?>> TextOptionType<T> getOptionType(Class<T> clazz) {
		return (TextOptionType<T>) options.get(clazz);
	}
	
	public TextOptionType<?> getOptionType(String configKey) {
		return configMapping.get(configKey);
	}
	
	public Collection<TextOptionType<?>> getOptionTypes() {
		return options.values();
	}
	
	public static class TextOptionType<T extends TextOption<?>> {
		
		private final Class<T> clazz;
		private final Function<TextInstance, T> optionSupplier;
		private final String configKey;
		
		public TextOptionType(Class<T> clazz, Function<TextInstance, T> optionSupplier, String configKey) {
			this.clazz = clazz;
			this.optionSupplier = optionSupplier;
			this.configKey = configKey;
		}
		
		public Class<T> getOptionClass() {
			return clazz;
		}
		
		public T createOption(TextInstance txt) {
			return optionSupplier.apply(txt);
		}
		
		public String getConfigKey() {
			return configKey;
		}
		
	}
	
}
