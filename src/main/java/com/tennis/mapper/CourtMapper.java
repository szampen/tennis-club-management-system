package com.tennis.mapper;

import com.tennis.domain.Court;
import com.tennis.domain.SurfaceType;
import com.tennis.util.CourtFilter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//TODO: use of identity map
public class CourtMapper implements DataMapper<Court> {

    @Override
    public Long insert(Court court, Connection connection) throws SQLException {
        String sql = "INSERT INTO courts (name, court_number, surface_type, has_roof, location, image_url,available_for_reservations, price_per_hour) VALUES (?,?,?,?,?,?,?,?)".formatted();

        PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setString(1, court.getName());
        statement.setInt(2, court.getCourtNumber());
        statement.setString(3, court.getSurfaceType().name());
        statement.setBoolean(4, court.hasRoof());
        statement.setString(5, court.getLocation());
        statement.setString(6, court.getImageUrl());
        statement.setBoolean(7, court.isAvailableForReservations());
        statement.setDouble(8, court.getPricePerHour());

        statement.executeUpdate();

        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            Long id = generatedKeys.getLong(1);
            court.setId(id);
            return id;
        }

        throw new SQLException("Error fetching court id.");
    }

    @Override
    public void update(Court court, Connection connection) throws SQLException {
        String sql = "UPDATE courts SET name = ?, court_number = ?, surface_type = ?, has_roof = ?, location = ?, image_url = ?, available_for_reservations = ?, price_per_hour = ? WHERE id = ?".formatted();

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, court.getName());
        statement.setInt(2, court.getCourtNumber());
        statement.setString(3, court.getSurfaceType().name());
        statement.setBoolean(4, court.hasRoof());
        statement.setString(5, court.getLocation());
        statement.setString(6, court.getImageUrl());
        statement.setBoolean(7, court.isAvailableForReservations());
        statement.setDouble(8, court.getPricePerHour());
        statement.setLong(9, court.getId());

        statement.executeUpdate();
    }

    @Override
    public void delete(Court court, Connection connection) throws SQLException {
        String sql = "DELETE FROM courts WHERE id = ?".formatted();
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, court.getId());
        statement.executeUpdate();
    }

    private Court mapResultSetToCourt(ResultSet rs) throws SQLException {
        Court court = new Court();
        court.setId(rs.getLong("id"));
        court.setName(rs.getString("name"));
        court.setCourtNumber(rs.getInt("court_number"));
        court.setHasRoof(rs.getBoolean("has_roof"));
        court.setLocation(rs.getString("location"));
        court.setImageUrl(rs.getString("image_url"));
        court.setAvailableForReservations(rs.getBoolean("available_for_reservations"));
        court.setPricePerHour(rs.getDouble("price_per_hour"));
        court.setSurfaceType(SurfaceType.valueOf(rs.getString("surface_type")));
        return court;
    }

    public Court findById(Long id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM courts WHERE id = ?".formatted();
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, id);

        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            return mapResultSetToCourt(rs);
        }
        return null;
    }

    public List<Court> findAllCourts(Connection connection) throws SQLException {
        List<Court> courts = new ArrayList<>();
        String sql = "SELECT * FROM courts".formatted();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            courts.add(mapResultSetToCourt(rs));
        }

        return courts;
    }

    public List<Court> findByFilter(CourtFilter filter, Connection connection) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM courts WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (filter.getSurfaceType() != null) {
            sql.append(" AND surface_type = ?");
            params.add(filter.getSurfaceType().name());
        }

        if (filter.getHasRoof() != null) {
            sql.append(" AND has_roof = ?");
            params.add(filter.getHasRoof());
        }

        if (filter.getAvailableForReservations() != null) {
            sql.append(" AND available_for_reservations = ?");
            params.add(filter.getAvailableForReservations());
        }

        if (filter.getCourtSort() != null) {
            sql.append(" ORDER BY")
                    .append(filter.getCourtSort().toSql())
                    .append(" ")
                    .append(filter.getDirection());
        }

        PreparedStatement statement = connection.prepareStatement(sql.toString());

        for(int i = 0; i < params.size(); i++){
            statement.setObject(i+1, params.get(i));
        }

        ResultSet rs = statement.executeQuery();

        List<Court> courts = new ArrayList<>();
        while(rs.next()){
            courts.add(mapResultSetToCourt(rs));
        }
        return courts;
    }

}
