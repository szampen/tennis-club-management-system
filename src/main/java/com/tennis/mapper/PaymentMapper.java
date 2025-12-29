package com.tennis.mapper;

import com.tennis.domain.Payment;

import java.sql.Connection;

public class PaymentMapper implements DataMapper<Payment>{
    @Override
    public Long insert(Payment payment, Connection connection){return null;}

    @Override
    public void update(Payment payment, Connection connection){}

    @Override
    public void delete(Payment payment, Connection connection){}
}
