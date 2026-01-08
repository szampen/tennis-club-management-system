package com.tennis.repository;

import com.tennis.domain.Tournament;
import com.tennis.domain.TournamentStatus;
import com.tennis.mapper.TournamentMapper;

import java.sql.Connection;
import java.util.List;

public class TournamentRepository {
    private final TournamentMapper mapper = new TournamentMapper();

    public Tournament findById(Long id, Connection connection) {
        try {
            return mapper.findById(id, connection);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching tournament", e);
        }
    }

    public List<Tournament> findAll(Connection connection) {
        try {
            return mapper.findAll(connection);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching tournaments", e);
        }
    }

    public List<Tournament> findByStatus(TournamentStatus status, Connection connection) {
        try {
            return mapper.findByStatus(status, connection);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching tournaments by status", e);
        }
    }

    public void insertParticipant(Long userId, Long tournamentId, Connection connection){
        try {
            mapper.insertParticipant(userId,tournamentId,connection);
        } catch (Exception e) {
            throw new RuntimeException("Error inserting new participant.", e);
        }
    }

    public void deleteParticipant(Long userId, Long tournamentId, Connection connection){
        try {
            mapper.deleteParticipant(userId,tournamentId,connection);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting new participant.", e);
        }
    }

    public int numberOfParticipants(Long tournamentId, Connection connection){
        try{
            return mapper.getNumberOfParticipants(tournamentId,connection);
        } catch (Exception e){
            throw new RuntimeException("Error retrieving number of participants");
        }
    }


    public void save(Tournament tournament, Connection connection) {
        try {
            if (tournament.getId() == null) {
                mapper.insert(tournament, connection);
            } else {
                mapper.update(tournament, connection);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving tournament", e);
        }
    }

    public void delete(Tournament tournament, Connection connection) {
        try {
            if (tournament != null) {
                mapper.delete(tournament, connection);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting tournament", e);
        }
    }
}
