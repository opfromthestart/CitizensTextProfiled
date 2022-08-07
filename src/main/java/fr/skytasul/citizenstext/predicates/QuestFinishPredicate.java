package fr.skytasul.citizenstext.predicates;

import me.blackvein.quests.Quests;
import me.blackvein.quests.player.IQuester;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QuestFinishPredicate extends NamedPredicate {
    String questName;

    public QuestFinishPredicate(String name)
    {
        super("finishquest");
        questName = name;
    }

    public QuestFinishPredicate()
    {
        super("finishquest");
        questName = "";
    }

    @Override
    public boolean test(Player player) {
        Quests quest = ((Quests) Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Quests")));
        IQuester quester = quest.getQuester(player.getUniqueId());
        System.out.println(quester.getCompletedQuestsTemp().contains(quest.getQuestTemp(questName)));
        return quester.getCompletedQuestsTemp().contains(quest.getQuestTemp(questName));
    }

    @Override
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("questName", questName);
        return data;
    }

    @Override
    public void fromConfig(ConfigurationSection data) {
        questName = (String) data.get("questName");
    }

    @Override
    public void fromMap(Map<String, Object> data) {
        if (data.containsKey("questName")) questName = (String) data.get("questName");
    }

    @Override
    public String getDesc() {
        return ((NamedPredicate)this).name + "{questName:"+questName+"}";
    }
}
