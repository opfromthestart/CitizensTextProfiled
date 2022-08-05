package fr.skytasul.citizenstext.options;

import fr.skytasul.citizenstext.message.Message;

import java.util.*;

import fr.skytasul.citizenstext.predicates.NamedPredicate;
import fr.skytasul.citizenstext.predicates.PredicateManager;
import fr.skytasul.citizenstext.texts.TextInstance;
import jdk.internal.net.http.common.Pair;
import org.bukkit.configuration.ConfigurationSection;

public class OptionMessageStates extends TextOption<List<Pair<NamedPredicate, List<Message>>>>{

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
            i++;
        }
        config.set(key, tmp);
    }

    @Override
    protected List<Pair<NamedPredicate, List<Message>>> loadValue(ConfigurationSection config, String key) {
        List<Pair<NamedPredicate, List<Message>>> temp = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection(key);
        assert section != null;
        section.getKeys(false).forEach(skey -> {
            String predName = section.getString("name");
            NamedPredicate pred = null;
            try {
                pred = (NamedPredicate) PredicateManager.preds.get(predName).newInstance();
                pred.fromConfig(section.getConfigurationSection("data"));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            List<Message> messages = new ArrayList<>();
            ConfigurationSection messageSection = section.getConfigurationSection("messages");
            assert messageSection != null;
            messageSection.getKeys(false).forEach(mkey -> messages.add(new Message(Objects.requireNonNull(messageSection.getConfigurationSection(mkey)))));
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
    }

    public void editPredicate(int id, Map<String, Object> data) {
        getValue().get(id).first.fromMap(data);
    }

    public Pair<NamedPredicate, List<Message>> removePredicate(int n) {
        return getValue().remove(n);
    }

    public void addMessage(int n, String msg) {
        getValue().get(n).second.add(new Message(msg));
    }

    public String editMessage(int n, int id, String msg) {
        return getValue().get(n).second.get(id).setText(msg);
    }

    public void insertMessage(int n, int id, String msg) {
        getValue().get(n).second.add(id, new Message(msg));
    }

    public String removeMessage(int n, int id) {
        return getValue().get(n).second.remove(id).getText();
    }

    public int messagesSize(int n) {
        return getValue().get(n).second.size();
    }

    public Message getMessage(int n, int id) {
        return getValue().get(n).second.get(id);
    }
}
