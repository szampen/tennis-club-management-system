package com.tennis.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennis.domain.Match;
import com.tennis.util.SetScore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MatchMapper implements DataMapper<Match> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Long insert(Match match, Connection connection) throws SQLException, JsonProcessingException {
        String sql = "INSERT INTO matches (tournament_id, player1_id, player2_id, winner_id, next_match_id, sets, points, court_id, scheduled_time, p1_sets_won, p2_sets_won, round)" +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setPreparedStatement(statement, match);
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getLong(1);
                match.setId(id);
                return id;
            }
        } catch (Exception e) {
            throw new SQLException("Fetching match id failed",e);
        }
        return null;
    }

    @Override
    public void update(Match match, Connection connection) throws SQLException {
        String sql = "UPDATE matches SET tournament_id = ?, player1_id = ?, player2_id = ?, winner_id = ?, next_match_id = ?, sets = ?, points = ?, court_id = ?, scheduled_time = ?, p1_sets_won = ?, p2_sets_won = ?, round = ? WHERE id = ?";

        try{
            PreparedStatement statement = connection.prepareStatement(sql);
            setPreparedStatement(statement,match);
            statement.setLong(13, match.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            throw new SQLException("Error updating match.", e);
        }

    }

    @Override
    public void delete(Match match, Connection connection) throws SQLException {
        String sql = "DELETE FROM matches WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, match.getId());
        statement.executeUpdate();
    }

    private Match mapResultSetToMatch(ResultSet rs) throws SQLException, JsonProcessingException {
        Match match = new Match();
        match.setId(rs.getLong("id"));
        match.setTournamentId(rs.getLong("tournament_id"));
        match.setPlayer1Id(getLongOrNull(rs, "player1_id"));
        match.setPlayer2Id(getLongOrNull(rs, "player2_id"));
        match.setWinnerId(getLongOrNull(rs, "winner_id"));
        match.setNextMatchId(getLongOrNull(rs, "next_match_id"));

        String json = rs.getString("sets");
        if(json != null && !json.isEmpty()){
            List<SetScore> sets = objectMapper.readValue(json, new TypeReference<>() {
            });
            match.setSets(sets);
        }

        match.setPoints(getIntOrNull(rs, "points"));
        match.setCourtId(getLongOrNull(rs, "court_id"));

        Timestamp ts = rs.getTimestamp("scheduled_time");
        if(ts != null) match.setScheduledTime(ts.toLocalDateTime());

        match.setP1SetsWon(getIntOrNull(rs, "p1_sets_won"));
        match.setP2SetsWon(getIntOrNull(rs, "p2_sets_won"));
        match.setRound(rs.getInt("round"));

        return match;
    }

    public Match findById(Long id, Connection connection) throws SQLException, JsonProcessingException {
        String sql = "SELECT * FROM matches WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, id);

        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            return mapResultSetToMatch(rs);
        }
        return null;
    }

    public List<Match> findByTournament(Long tournamentId, Connection connection) throws SQLException, JsonProcessingException {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches WHERE tournament_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, tournamentId);

        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            matches.add(mapResultSetToMatch(rs));
        }
        return matches;
    }

    public List<Match> findByPlayer(Long userId, Connection connection) throws SQLException, JsonProcessingException {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches WHERE player1_id = ? OR player2_id = ? ";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, userId);
        statement.setLong(2, userId);

        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            matches.add(mapResultSetToMatch(rs));
        }
        return matches;
    }


    public int countWins(Long userId, Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM matches WHERE winner_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, userId);

        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public int countLosses(Long userId, Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM matches WHERE (player1_id = ? OR player2_id = ?) AND winner_id IS NOT NULL AND winner_id != ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, userId);
        statement.setLong(2, userId);
        statement.setLong(3, userId);

        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public int setsWon(Long userId, Connection connection) throws SQLException{
        int sum = 0;
        String sql = "SELECT SUM(p1_sets_won) FROM matches WHERE player1_id = ?";

        PreparedStatement statement1 = connection.prepareStatement(sql);
        statement1.setLong(1,userId);
        ResultSet rs = statement1.executeQuery();
        if(rs.next()){
            sum += rs.getInt(1);
        }

        String sql2 = "SELECT SUM(p2_sets_won) FROM matches WHERE player2_id = ?";

        PreparedStatement statement2 = connection.prepareStatement(sql2);
        statement2.setLong(1,userId);
        ResultSet rs2 = statement2.executeQuery();
        if(rs2.next()){
            sum += rs2.getInt(1);
        }
        return sum;
    }

    public int setsLost(Long userId, Connection connection) throws SQLException{
        int sum = 0;
        String sql = "SELECT SUM(p2_sets_won) FROM matches WHERE player1_id = ?";

        PreparedStatement statement1 = connection.prepareStatement(sql);
        statement1.setLong(1,userId);
        ResultSet rs = statement1.executeQuery();
        if(rs.next()){
            sum += rs.getInt(1);
        }

        String sql2 = "SELECT SUM(p1_sets_won) FROM matches WHERE player2_id = ?";

        PreparedStatement statement2 = connection.prepareStatement(sql2);
        statement2.setLong(1,userId);
        ResultSet rs2 = statement2.executeQuery();
        if(rs2.next()){
            sum += rs2.getInt(1);
        }
        return sum;
    }

    // Helper methods
    private void setLongOrNull(PreparedStatement stmt, int index, Long value) throws SQLException {
        if (value != null) {
            stmt.setLong(index, value);
        } else {
            stmt.setNull(index, Types.BIGINT);
        }
    }

    private Long getLongOrNull(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    private void setIntOrNull(PreparedStatement stmt, int index, Integer value) throws SQLException {
        if (value != null) {
            stmt.setInt(index, value);
        } else {
            stmt.setNull(index, Types.INTEGER);
        }
    }

    private Integer getIntOrNull(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }

    private void setPreparedStatement(PreparedStatement statement, Match match) throws Exception{
        statement.setLong(1, match.getTournamentId());
        setLongOrNull(statement, 2, match.getPlayer1Id());
        setLongOrNull(statement, 3, match.getPlayer2Id());
        setLongOrNull(statement, 4, match.getWinnerId());
        setLongOrNull(statement,5,match.getNextMatchId());

        if(match.getSets() != null) statement.setString(6, objectMapper.writeValueAsString(match.getSets()));
        else statement.setNull(6,Types.VARCHAR);

        setIntOrNull(statement,7,match.getPoints());
        setLongOrNull(statement, 8, match.getCourtId());

        if(match.getScheduledTime() != null) statement.setTimestamp(9, Timestamp.valueOf(match.getScheduledTime()));
        else statement.setNull(9, Types.TIMESTAMP);

        setIntOrNull(statement,10, match.getP1SetsWon());
        setIntOrNull(statement,11, match.getP2SetsWon());
        statement.setInt(12,match.getRound());
    }
}