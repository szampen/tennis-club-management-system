package com.tennis.mapper;

import com.tennis.domain.Court;

import java.sql.Connection;

public class CourtMapper implements DataMapper<Court>{

    @Override
    public Long insert(Court court, Connection connection){return null;}

    @Override
    public void update(Court court, Connection connection){}

    @Override
    public void delete(Court court, Connection connection){}

}
