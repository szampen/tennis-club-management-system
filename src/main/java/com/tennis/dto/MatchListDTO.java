package com.tennis.dto;


import java.util.List;

public class MatchListDTO {
    private Long id;
    private List<String> sets;
    private String opponentName;
    private String winOrLoss;
    private String tournamentName;
    private String roundName;

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getSets() {
        return sets;
    }

    public void setSets(List<String> sets) {
        this.sets = sets;
    }

    public String getWinOrLoss() {
        return winOrLoss;
    }

    public void setWinOrLoss(String winOrLoss) {
        this.winOrLoss = winOrLoss;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }
}
