package com.tennis.util;

public enum CourtSort {
    COURTNUMBER("court_number"),
    FIRSTDATE("firstdate"),
    PRICE("price_per_hour");


    private final String sql;

    CourtSort(String sql){
        this.sql = sql;
    }

    public String toSql(){
        return sql;
    }
}
