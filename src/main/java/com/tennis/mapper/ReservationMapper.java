package com.tennis.mapper;

import com.tennis.domain.Reservation;

import java.sql.Connection;

public class ReservationMapper implements DataMapper<Reservation>{
    public Long insert(Reservation reservation, Connection connection){return null;}

    @Override
    public void update(Reservation reservation, Connection connection){}

    @Override
    public void delete(Reservation reservation, Connection connection){}
}
