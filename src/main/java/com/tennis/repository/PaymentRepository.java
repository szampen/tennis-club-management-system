package com.tennis.repository;

import com.tennis.domain.Payment;
import com.tennis.mapper.PaymentMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;

@Repository
public class PaymentRepository {
    private final PaymentMapper mapper = new PaymentMapper();

    public Payment findById(Long id, Connection connection) {
        try {
            return mapper.findById(id, connection);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching payment", e);
        }
    }

    public Payment findByReservationId(Long reservationId, Connection connection) {
        try {
            return mapper.findByReservationId(reservationId, connection);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching payment.", e);
        }
    }

    public void save(Payment payment, Connection connection){
        try{
            if(payment.getId() == null){
                mapper.insert(payment, connection);
            } else {
                mapper.update(payment,connection);
            }
        } catch (Exception e){
            throw new RuntimeException("Error saving payment.", e);
        }
    }

    public void delete(Payment payment, Connection connection){
        try{
            if(payment != null){
                mapper.delete(payment,connection);
            }
        } catch (Exception e){
            throw new RuntimeException("Error deleting payment.", e);
        }
    }
}
