package fr.skytasul.citizenstext.predicates;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public abstract class NamedPredicate implements Predicate<Player> {
    String name;

    public NamedPredicate(String n)
    {
        name = n;
    }

    public String getName()
    {
        return name;
    }

    public abstract HashMap<String, Object> toMap();
    public abstract void fromConfig(ConfigurationSection data);
    public abstract void fromMap(Map<String, Object> data);

    public String getDesc()
    {
        return name;
    }
}
