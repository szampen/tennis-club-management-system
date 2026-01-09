package com.tennis.repository;

import com.tennis.domain.Reservation;
import com.tennis.mapper.ReservationMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ReservationRepository {
    private final ReservationMapper mapper = new ReservationMapper();

    public Reservation findById(Long id, Connection connection) {
        try {
            return mapper.findById(id, connection);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching reservation", e);
        }
    }

    public List<Reservation> findByUserId(Long userId, Connection connection) {
        try {
            return mapper.findByUserId(userId, connection);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user's reservations.", e);
        }
    }

    public List<Reservation> findByCourtId(Long courtId, Connection connection) {
        try {
            return mapper.findByCourtId(courtId, connection);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching court's reservations.", e);
        }
    }

    public List<Reservation> findByCourtIdAndDateRange(Long courtId, LocalDateTime start, LocalDateTime end, Connection connection) {
        try {
            return mapper.findByCourtIdAndDateRange(courtId, start, end, connection);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching reservation.", e);
        }
    }

    public void cleanupExpiredHolds(Connection connection){
        try{
            mapper.cleanupExpiredHolds(connection);
        } catch (Exception e){
            throw new RuntimeException("Error cleaning up expired holds.",e);
        }
    }

    public void confirmTournamentReservation(Long matchId, Connection connection){
        try{
            mapper.confirmTournamentReservation(matchId,connection);
        } catch (Exception e){
            throw new RuntimeException("Error confirming tournament reservation.");
        }
    }

    public void save(Reservation reservation, Connection connection){
        try{
            if(reservation.getId() == null){
                mapper.insert(reservation, connection);
            } else {
                mapper.update(reservation,connection);
            }
        } catch (Exception e){
            throw new RuntimeException("Error saving reservation.", e);
        }
    }

    public void delete(Reservation reservation, Connection connection){
        try{
            if(reservation != null){
                mapper.delete(reservation,connection);
            }
        } catch (Exception e){
            throw new RuntimeException("Error deleting reservation.", e);
        }
    }
}
