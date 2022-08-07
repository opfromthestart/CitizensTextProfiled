package fr.skytasul.citizenstext.options;

import fr.skytasul.citizenstext.message.Message;

import java.util.*;

import fr.skytasul.citizenstext.predicates.NamedPredicate;
import fr.skytasul.citizenstext.predicates.PredicateManager;
import fr.skytasul.citizenstext.texts.TextInstance;
import fr.skytasul.citizenstext.utils.Pair;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class OptionMessageStates extends TextOption<List<Pair<NamedPredicate, List<Message>>>>{

    int selectedPredicate = 0;

    protected OptionMessageStates(TextInstance txt) {
        super(txt);
    }

    @Override
    public List<Pair<NamedPredicate, List<Message>>> getDefault() {
        return null;
    }

    @Override
    protected void saveValue(ConfigurationSection config, String key) {
        Map<String, Object> tmp = new HashMap<>();
        for (int i=0; i<getValue().size(); i++) {
            Pair<NamedPredicate, List<Message>> pn = getValue().get(i);
            Map<String, Object> tmp2 = new HashMap<>();
            tmp2.put("name", pn.first.getName());
            tmp2.put("data", pn.first.toMap());
            Map<String, Object> tmp3 = new HashMap<>();
            for (int j=0; j < pn.second.size(); j++)
                tmp3.put(String.valueOf(j), pn.second.get(j).serialize());
            tmp2.put("messages", tmp3);
            tmp.put(String.valueOf(i), tmp2);
        }
        config.set(key, tmp);
    }

    @Override
    protected List<Pair<NamedPredicate, List<Message>>> loadValue(ConfigurationSection config, String key) {
        List<Pair<NamedPredicate, List<Message>>> temp = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection(key);
        assert section != null;
        section.getKeys(false).forEach(skey -> {
            ConfigurationSection subsection = section.getConfigurationSection(skey);
            String predName = subsection.getString("name");
            if (!PredicateManager.preds.containsKey(predName))
            {
                System.out.println("Key " + predName + " not found.");
            }
            NamedPredicate pred = null;
            try {
                pred = (NamedPredicate) PredicateManager.preds.get(predName).newInstance();
                pred.fromConfig(subsection.getConfigurationSection("data"));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            List<Message> messages = new ArrayList<>();
            ConfigurationSection messageSection = subsection.getConfigurationSection("messages");
            assert messageSection != null;
            messageSection.getKeys(false).forEach(mkey -> {
                if (messageSection.getConfigurationSection(mkey) != null)
                    messages.add(new Message(Objects.requireNonNull(messageSection.getConfigurationSection(mkey))));
                else
                    messages.add(new Message(messageSection.getString(mkey)));
            });
            temp.add(new Pair<>(pred, messages));
        });
        return temp;
    }

    @Override
    public void setDefaultValue() {
        setValue(new ArrayList<>());
    }

    public void addPredicate(NamedPredicate n) {
        getValue().add(new Pair<>(n, new ArrayList<>()));
        selectedPredicate = getValue().size()-1;
    }

    public void editPredicate(Map<String, Object> data) {
        getValue().get(selectedPredicate).first.fromMap(data);
    }

    public void selectPredicate(int id) {
        selectedPredicate = id;
    }

    public Pair<NamedPredicate, List<Message>> removePredicate(int n) {
        return getValue().remove(n);
    }

    public void addMessage(String msg) {
        getValue().get(selectedPredicate).second.add(new Message(msg));
    }

    public String editMessage(int id, String msg) {
        return getValue().get(selectedPredicate).second.get(id).setText(msg);
    }

    public void insertMessage(int id, String msg) {
        getValue().get(selectedPredicate).second.add(id, new Message(msg));
    }

    public String removeMessage(int id) {
        return getValue().get(selectedPredicate).second.remove(id).getText();
    }

    public int messagesSize() {
        return getValue().get(selectedPredicate).second.size();
    }

    public int predicatesSize() {
        return getValue().size();
    }

    public Message getMessage(int id) {
        return getValue().get(selectedPredicate).second.get(id);
    }

    public int clear() {
        int i = getValue().get(selectedPredicate).second.size();
        getValue().get(selectedPredicate).second.clear();
        return i;
    }

    public String listMessages() {
        StringJoiner stb = new StringJoiner("\n");
        for (int i = 0; i < getValue().get(selectedPredicate).second.size(); i++) {
            Message msg = getValue().get(selectedPredicate).second.get(i);
            stb.add(ChatColor.AQUA + "" + i + " : "
                    + ChatColor.GREEN + msg.getText()
                    + (msg.getCommands().isEmpty() ? "" : ChatColor.GRAY + " (" + msg.getCommands().size() + " command(s): " + msg.getCommandsList() + "§7)"));
        }
        return stb.toString();
    }

    public String getPredicateText()
    {
        return getValue().get(selectedPredicate).first.getDesc();
    }

    public List<Message> getSelectedMessages()
    {
        return getValue().get(selectedPredicate).second;
    }
}
