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
        statement.setString(1, tournament.getName());
        statement.setDate(2, Date.valueOf(tournament.getStartDate()));
        statement.setDate(3, Date.valueOf(tournament.getEndDate()));
        statement.setString(4, tournament.getRank().name());
        statement.setDouble(5, tournament.getEntryFee());
        statement.setInt(6, tournament.getRankingRequirement());
        statement.setString(7, tournament.getStatus().name());

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
        statement.setString(1, tournament.getName());
        statement.setDate(2, Date.valueOf(tournament.getStartDate()));
        statement.setDate(3, Date.valueOf(tournament.getEndDate()));
        statement.setString(4, tournament.getRank().name());
        statement.setDouble(5, tournament.getEntryFee());
        statement.setInt(6, tournament.getRankingRequirement());
        statement.setString(7, tournament.getStatus().name());
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
        tournament.setStartDate(rs.getDate("start_date").toLocalDate());
        tournament.setEndDate(rs.getDate("end_date").toLocalDate());
        tournament.setStatus(TournamentStatus.valueOf(rs.getString("status")));
        tournament.setEntryFee(rs.getDouble("entry_fee"));
        tournament.setRankingRequirement(rs.getInt("min_ranking_required"));
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