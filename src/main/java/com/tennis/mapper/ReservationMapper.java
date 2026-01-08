package com.tennis.mapper;

import com.tennis.domain.Reservation;
import com.tennis.domain.ReservationStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationMapper implements DataMapper<Reservation>{
    public Long insert(Reservation reservation, Connection connection) throws SQLException {
        String sql = "INSERT INTO reservations (user_id, court_id, start_time, end_time, status) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setLong(1, reservation.getUserId());

        Long courtId = reservation.getCourtId();
        if(courtId != null) stmt.setLong(2,courtId);
        else stmt.setNull(2, Types.BIGINT);

        stmt.setTimestamp(3, Timestamp.valueOf(reservation.getStartTime()));
        stmt.setTimestamp(4, Timestamp.valueOf(reservation.getEndTime()));
        stmt.setString(5, reservation.getStatus().name());

        stmt.executeUpdate();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            Long id = generatedKeys.getLong(1);
            reservation.setId(id);
            return id;
        }

        throw new SQLException("Error retrieving reservation's id.");
    }

    @Override
    public void update(Reservation reservation, Connection connection){}

    @Override
    public void delete(Reservation reservation, Connection connection) throws SQLException{
        String sql = "DELETE FROM reservations WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, reservation.getId());
        stmt.executeUpdate();
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getLong("id"));
        reservation.setUserId(rs.getLong("user_id"));

        Long courtId = rs.getLong("court_id");
        if(!rs.wasNull()) reservation.setCourtId(courtId);

        reservation.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        reservation.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
        return reservation;
    }

    public Reservation findById(Long id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, id);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return mapResultSetToReservation(rs);
        }

        return null;
    }

    public List<Reservation> findByUserId(Long userId, Connection connection) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE user_id = ? ORDER BY start_time DESC";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, userId);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            reservations.add(mapResultSetToReservation(rs));
        }

        return reservations;
    }

    public List<Reservation> findByCourtId(Long courtId, Connection connection) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE court_id = ? ORDER BY start_time";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, courtId);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            reservations.add(mapResultSetToReservation(rs));
        }

        return reservations;
    }

    public List<Reservation> findByCourtIdAndDateRange(Long courtId, LocalDateTime start, LocalDateTime end, Connection connection) throws SQLException {

        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations " +
                "WHERE court_id = ? " +
                "AND start_time >= ? " +
                "AND end_time <= ? " +
                "ORDER BY start_time";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, courtId);
        stmt.setTimestamp(2, Timestamp.valueOf(start));
        stmt.setTimestamp(3, Timestamp.valueOf(end));

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            reservations.add(mapResultSetToReservation(rs));
        }

        return reservations;
    }
}
