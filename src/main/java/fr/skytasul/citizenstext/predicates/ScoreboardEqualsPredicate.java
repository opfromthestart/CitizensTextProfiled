package fr.skytasul.citizenstext.predicates;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScoreboardEqualsPredicate extends NamedPredicate {
    String name;
    int value;

    public ScoreboardEqualsPredicate(String n, int val) {
        super("scoreequals");
        name = n;
        value = val;
    }

    public ScoreboardEqualsPredicate()
    {
        super("scoreequals");
        name = "";
        value = Integer.MIN_VALUE;
    }

    @Override
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("value", value);
        return data;
    }

    @Override
    public void fromConfig(ConfigurationSection data) {
        if (data.contains("name"))
        {
            name = (String) data.get("name");
        }
        if (data.contains("value"))
        {
            value = (int) data.get("value");
        }
    }

    @Override
    public void fromMap(Map<String, Object> data) {
        if (data.containsKey("name"))
        {
            name = (String) data.get("name");
        }
        if (data.containsKey("value"))
        {
            value = Integer.parseInt((String) data.get("value"));
        }
        if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective(name) == null)
            Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(name, "dummy", "");
    }

    @Override
    public boolean test(Player player) {
        Objective objective = player.getScoreboard().getObjective(name);
        if (objective != null) {
            Score score = objective.getScore(player.getName());
            if (!score.isScoreSet())
                score.setScore(0);
            return score.getScore() == value;
        }
        return false;
    }

    @Override
    public String getDesc() {
        return ((NamedPredicate)this).name + "{name:" + name + ",value:" + value + "}";
    }
}