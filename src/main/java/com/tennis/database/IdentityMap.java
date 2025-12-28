package com.tennis.database;

import java.util.HashMap;
import java.util.Map;

/*
    Identity Map contains objects loaded from database in memory, guaranteeing that the same object will be loaded only once
 */

public class IdentityMap {
    private Map<String, Object> map = new HashMap<>();

    private <T> String createKey(Class<T> class_, Long id){
        return class_.getSimpleName() + "#" + id; //ex. User#5
    }

    public <T> void put(Class<T> class_, Long id, T object){
        map.put(createKey(class_,id),object);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> class_, Long id){
        return (T) map.get(createKey(class_,id));
    }

    public <T> boolean contains(Class<T> class_, Long id){
        return map.containsKey(createKey(class_,id));
    }

    public void clear(){
        map.clear();
    }
}
