package com.tennis.mapper;

import com.tennis.domain.Payment;
import com.tennis.domain.PaymentStatus;

import java.sql.*;

public class PaymentMapper implements DataMapper<Payment>{
    @Override
    public Long insert(Payment payment, Connection connection) throws SQLException {
        String sql = "INSERT INTO payments (reservation_id, amount, status, payment_date, transaction_id) VALUES (?, ?, ?, ?, ?)".formatted();

        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setLong(1, payment.getReservationId());
        statement.setDouble(2, payment.getAmount());
        statement.setString(3, payment.getPaymentStatus().name());

        if(payment.getPaymentDate() != null){
            statement.setTimestamp(4, Timestamp.valueOf(payment.getPaymentDate()));
        } else{
            statement.setNull(4, Types.TIMESTAMP);
        }

        statement.setString(5, payment.getTransactionId());

        statement.executeUpdate();

        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            Long id = generatedKeys.getLong(1);
            payment.setId(id);
            return id;
        }

        throw new SQLException("Error fetching payment's id.");
    }

    @Override
    public void update(Payment payment, Connection connection)throws SQLException {
        String sql = "UPDATE payments SET amount = ?, status = ?, payment_date = ?, transaction_id = ? WHERE id = ?".formatted();

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setDouble(1, payment.getAmount());
        stmt.setString(2, payment.getPaymentStatus().name());

        if (payment.getPaymentDate() != null) {
            stmt.setTimestamp(3, Timestamp.valueOf(payment.getPaymentDate()));
        } else {
            stmt.setNull(3, Types.TIMESTAMP);
        }

        stmt.setString(4, payment.getTransactionId());
        stmt.setLong(5, payment.getId());

        stmt.executeUpdate();
    }

    @Override
    public void delete(Payment payment, Connection connection) throws SQLException{
        String sql = "DELETE FROM payments WHERE id = ?".formatted();
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, payment.getId());
        stmt.executeUpdate();
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getLong("id"));
        payment.setReservationId(rs.getLong("reservation_id"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setPaymentStatus(PaymentStatus.valueOf(rs.getString("status")));

        Timestamp timestamp = rs.getTimestamp("payment_date");
        if (timestamp != null) {
            payment.setPaymentDate(timestamp.toLocalDateTime());
        }

        payment.setTransactionId(rs.getString("transaction_id"));
        return payment;
    }

    public Payment findById(Long id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM payments WHERE id = ?".formatted();
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, id);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return mapResultSetToPayment(rs);
        }

        return null;
    }

    public Payment findByReservationId(Long reservationId, Connection connection) throws SQLException {
        String sql = "SELECT * FROM payments WHERE reservation_id = ?".formatted();
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, reservationId);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return mapResultSetToPayment(rs);
        }

        return null;
    }
}
