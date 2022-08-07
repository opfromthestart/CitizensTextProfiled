package fr.skytasul.citizenstext.predicates;

import java.util.HashMap;

public class PredicateManager {
    public static HashMap<String, Class<?>> preds = new HashMap<>();

    public static <T extends NamedPredicate> void register(Class<T> pred, String name)
    {
        //System.out.println(name + " " + pred.getName());
        preds.put(name, pred);
    }
}
