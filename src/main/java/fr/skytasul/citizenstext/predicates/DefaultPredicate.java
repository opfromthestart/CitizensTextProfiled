package fr.skytasul.citizenstext.predicates;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DefaultPredicate extends NamedPredicate{
    public DefaultPredicate() {
        super("default");
    }

    @Override
    public boolean test(Player player) {
        return true;
    }

    @Override
    public HashMap<String, Object> toMap() {
        return new HashMap<>();
    }

    @Override
    public void fromConfig(ConfigurationSection data) {

    }

    @Override
    public void fromMap(Map<String, Object> data) {

    }
}
