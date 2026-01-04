package com.tennis.util;

public enum CourtSort {
    //TODO: ways of sorting - the closest one to reserve by time
    NAME("name");

    private final String sql;

    CourtSort(String sql){
        this.sql = sql;
    }

    public String toSql(){
        return sql;
    }
}
