package com.tennis.database;

import java.util.Objects;

public class IdentityKey {
    private final Class<?> type;
    private final Long id;

    public IdentityKey(Class<?> type, Long id){
        this.type = type;
        this.id = id;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof IdentityKey)) return false;
        IdentityKey that = (IdentityKey) o;
        return Objects.equals(type, that.type) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(type,id);
    }
}
