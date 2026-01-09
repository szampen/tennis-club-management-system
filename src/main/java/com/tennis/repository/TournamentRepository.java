package com.tennis.repository;

import com.tennis.domain.Tournament;
import com.tennis.domain.TournamentStatus;
import com.tennis.mapper.TournamentMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

@Repository
public class TournamentRepository {
    private final TournamentMapper mapper = new TournamentMapper();

    public Tournament findById(Long id, Connection connection) {
        try {
            return mapper.findById(id, connection);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching tournament", e);
        }
    }

    public Tournament findByIdForUpdate(Long id, Connection connection) {
        try {
            return mapper.findByIdForUpdate(id, connection);
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

    public List<Tournament> findByStartDateAndStatus(LocalDate targetDate, TournamentStatus status, Connection connection) {
        try {
            return mapper.findByStartDateAndStatus(targetDate,status, connection);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching tournaments by status and target date.", e);
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

    public boolean isUserInTournament(Long userId, Long tournamentId, Connection connection){
        try{
            return mapper.isUserInTournament(userId,tournamentId,connection);
        } catch (Exception e){
            throw new RuntimeException("Error retrieving participant in tournament data.");
        }
    }

    public List<Tournament> tournamentsWonByUser(Long userId, Connection connection){
        try{
            return mapper.tournamentsWonList(userId,connection);
        } catch (Exception e){
            throw new RuntimeException("Error retrieving won tournaments.");
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
