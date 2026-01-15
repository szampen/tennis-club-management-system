package com.tennis.dto;

import java.util.List;
import java.util.Map;

public class PlayerStatsDTO {
    private int wins;
    private int losses;
    private int setWins;
    private int setLosses;
    private double match_percentage;
    private double set_percentage;

    private Map<Long, String> tournamentsWon;

    private List<MatchListDTO> matches;

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getSetWins() {
        return setWins;
    }

    public void setSetWins(int setWins) {
        this.setWins = setWins;
    }

    public int getSetLosses() {
        return setLosses;
    }

    public void setSetLosses(int setLosses) {
        this.setLosses = setLosses;
    }

    public double getSet_percentage() {
        return set_percentage;
    }

    public void setSet_percentage(double set_percentage) {
        this.set_percentage = set_percentage;
    }

    public double getMatch_percentage() {
        return match_percentage;
    }

    public void setMatch_percentage(double match_percentage) {
        this.match_percentage = match_percentage;
    }

    public List<MatchListDTO> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchListDTO> matches) {
        this.matches = matches;
    }

    public Map<Long, String> getTournamentsWon() {
        return tournamentsWon;
    }

    public void setTournamentsWon(Map<Long, String> tournamentsWon) {
        this.tournamentsWon = tournamentsWon;
    }
}
