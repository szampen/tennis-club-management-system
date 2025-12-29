package com.tennis.database;

import java.util.HashMap;
import java.util.Map;

/*
    Identity Map contains objects loaded from database in memory, guaranteeing that the same object will be loaded only once
 */

public class IdentityMap {
    private Map<IdentityKey, Object> map = new HashMap<>();

    public <T> void put(Class<T> class_, Long id, T object){
        map.put(new IdentityKey(class_,id), object);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> class_, Long id){
        return (T) map.get(new IdentityKey(class_,id));
    }

    public <T> boolean contains(Class<T> class_, Long id){
        return map.containsKey(new IdentityKey(class_, id));
    }

    public void clear(){
        map.clear();
    }
}
