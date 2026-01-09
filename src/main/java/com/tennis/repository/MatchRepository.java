package com.tennis.repository;

import com.tennis.domain.Match;
import com.tennis.mapper.MatchMapper;

import java.sql.Connection;
import java.util.List;

public class MatchRepository {
    private final MatchMapper mapper = new MatchMapper();

    public Match findById(Long id, Connection connection) {
        try {
            return mapper.findById(id, connection);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching match", e);
        }
    }

    public List<Match> findByTournament(Long tournamentId, Connection connection) {
        try {
            return mapper.findByTournament(tournamentId, connection);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching tournament matches", e);
        }
    }

    public List<Match> findByPlayer(Long userId, Connection connection) {
        try {
            return mapper.findByPlayer(userId, connection);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching player matches", e);
        }
    }

    public int countWins(Long userId, Connection connection) {
        try {
            return mapper.countWins(userId, connection);
        } catch (Exception e) {
            throw new RuntimeException("Error counting wins", e);
        }
    }

    public int countLosses(Long userId, Connection connection) {
        try {
            return mapper.countLosses(userId, connection);
        } catch (Exception e) {
            throw new RuntimeException("Error counting losses", e);
        }
    }

    public int countSetWon(Long userId, Connection connection){
        try{
            return mapper.setsWon(userId,connection);
        } catch (Exception e){
            throw new RuntimeException("Error counting won sets.");
        }
    }

    public int countSetLost(Long userId, Connection connection){
        try{
            return mapper.setsLost(userId,connection);
        } catch (Exception e){
            throw new RuntimeException("Error counting lost sets.");
        }
    }

    public void save(Match match, Connection connection) {
        try {
            if (match.getId() == null) {
                mapper.insert(match, connection);
            } else {
                mapper.update(match, connection);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving match", e);
        }
    }

    public void delete(Match match, Connection connection) {
        try {
            if (match != null) {
                mapper.delete(match, connection);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting match", e);
        }
    }
}
