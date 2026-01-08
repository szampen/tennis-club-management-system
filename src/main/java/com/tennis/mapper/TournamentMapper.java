package com.tennis.mapper;

import com.tennis.domain.Tournament;
import com.tennis.domain.TournamentRank;
import com.tennis.domain.TournamentStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentMapper implements DataMapper<Tournament> {

    @Override
    public Long insert(Tournament tournament, Connection connection) throws SQLException {
        String sql = "INSERT INTO tournaments (name, start_date, end_date, 'rank', entry_fee, ranking_requirement, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        setPreparedStatement(statement,tournament);

        statement.executeUpdate();

        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            Long id = generatedKeys.getLong(1);
            tournament.setId(id);
            return id;
        }

        throw new SQLException("Fetching tournament id failed");
    }

    @Override
    public void update(Tournament tournament, Connection connection) throws SQLException {
        String sql = "UPDATE tournaments SET name = ?, start_date = ?, end_date = ?, 'rank' = ?, entry_fee = ?, ranking_requirement = ?, status = ? WHERE id = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        setPreparedStatement(statement,tournament);
        statement.setLong(8, tournament.getId());

        statement.executeUpdate();
    }

    @Override
    public void delete(Tournament tournament, Connection connection) throws SQLException {
        String sql = "DELETE FROM tournaments WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, tournament.getId());
        statement.executeUpdate();
    }

    private Tournament mapResultSetToTournament(ResultSet rs) throws SQLException {
        Tournament tournament = new Tournament(TournamentRank.valueOf(rs.getString("rank")));
        tournament.setId(rs.getLong("id"));
        tournament.setName(rs.getString("name"));
        tournament.setStatus(TournamentStatus.valueOf(rs.getString("status")));

        Date ts = rs.getDate("start_date");
        if(ts != null) tournament.setStartDate(ts.toLocalDate());

        Date ts2 = rs.getDate("end_date");
        if(ts2 != null) tournament.setEndDate(ts2.toLocalDate());

        Double entry = rs.getDouble("entry_fee");
        if(!rs.wasNull()) tournament.setEntryFee(entry);

        Integer value = rs.getInt("ranking_requirement");
        if(!rs.wasNull()) tournament.setRankingRequirement(value);

        return tournament;
    }

    public Tournament findById(Long id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM tournaments WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, id);

        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            return mapResultSetToTournament(rs);
        }
        return null;
    }

    public List<Tournament> findAll(Connection connection) throws SQLException {
        List<Tournament> tournaments = new ArrayList<>();
        String sql = "SELECT * FROM tournaments WHERE status != ? ORDER BY start_date DESC";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, TournamentStatus.DRAFT.name());

        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            tournaments.add(mapResultSetToTournament(rs));
        }
        return tournaments;
    }

    public List<Tournament> findByStatus(TournamentStatus status, Connection connection) throws SQLException {
        List<Tournament> tournaments = new ArrayList<>();
        String sql = "SELECT * FROM tournaments WHERE status = ? ORDER BY start_date DESC";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, status.name());

        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            tournaments.add(mapResultSetToTournament(rs));
        }
        return tournaments;
    }

    private void setPreparedStatement(PreparedStatement statement, Tournament tournament) throws SQLException{
        statement.setString(1, tournament.getName());

        if(tournament.getStartDate() != null) statement.setDate(2, Date.valueOf(tournament.getStartDate()));
        else statement.setNull(2, Types.DATE);

        if(tournament.getEndDate() != null) statement.setDate(3, Date.valueOf(tournament.getEndDate()));
        else statement.setNull(3, Types.DATE);

        statement.setString(4, tournament.getRank().name());

        statement.setDouble(5, tournament.getEntryFee());

        Double entry = tournament.getEntryFee();
        if(entry != null) statement.setDouble(5, entry);
        else statement.setNull(5,Types.DOUBLE);

        Integer ranking = tournament.getRankingRequirement();
        if(ranking != null) statement.setInt(6, ranking);
        else statement.setNull(6, Types.INTEGER);

        statement.setString(7, tournament.getStatus().name());
    }

    //TODO: i dont know if id's or objects are better
    public void insertParticipant(Long userId, Long tournamentId, Connection connection) throws SQLException{
        String sql = "INSERT INTO tournament_participants (user_id, tournament_id) VALUES (?, ?)";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, userId);
        statement.setLong(2, tournamentId);

        statement.executeUpdate();
    }

    public void deleteParticipant(Long userId, Long tournamentId, Connection connection) throws SQLException{
        String sql = "DELETE FROM tournament_participants WHERE user_id = ? AND tournament_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1,userId);
        statement.setLong(2, tournamentId);
        statement.executeUpdate();
    }

    public int getNumberOfParticipants(Long tournamentId, Connection connection) throws SQLException{
        String sql = "SELECT COUNT(*) FROM tournament_participants WHERE tournament_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, tournamentId);

        ResultSet rs = statement.executeQuery();
        if(rs.next()){
            return rs.getInt(1);
        }
        throw new SQLException("Failed to retrieve number of participants.");
    }
}