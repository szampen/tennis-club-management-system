package com.tennis.dto;

import java.util.List;

public class MatchDetailsDTO {
    private String tournamentName;
    private String tournamentRank;
    private String tournamentStatus;
    private String roundName;

    private Long player1Id;
    private String player1FullName;
    private Long player2Id;
    private String player2FullName;
    private Long winnerId;

    private String finalScore; // np. "2:1"
    private List<String> sets; // np. ["6:4", "3:6", "7:5"]

    private String courtName;
    private int courtNumber;
    private String courtSurfaceType;
    private String courtLocation;
    private String scheduledTime;

    public MatchDetailsDTO() {}

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public String getTournamentRank() { return tournamentRank; }
    public void setTournamentRank(String tournamentRank) { this.tournamentRank = tournamentRank; }

    public String getTournamentStatus() { return tournamentStatus; }
    public void setTournamentStatus(String tournamentStatus) { this.tournamentStatus = tournamentStatus; }

    public String getRoundName() { return roundName; }
    public void setRoundName(String roundName) { this.roundName = roundName; }

    public Long getPlayer1Id() { return player1Id; }
    public void setPlayer1Id(Long player1Id) { this.player1Id = player1Id; }

    public String getPlayer1FullName() { return player1FullName; }
    public void setPlayer1FullName(String player1FullName) { this.player1FullName = player1FullName; }

    public Long getPlayer2Id() { return player2Id; }
    public void setPlayer2Id(Long player2Id) { this.player2Id = player2Id; }

    public String getPlayer2FullName() { return player2FullName; }
    public void setPlayer2FullName(String player2FullName) { this.player2FullName = player2FullName; }

    public Long getWinnerId() { return winnerId; }
    public void setWinnerId(Long winnerId) { this.winnerId = winnerId; }

    public String getFinalScore() { return finalScore; }
    public void setFinalScore(String finalScore) { this.finalScore = finalScore; }

    public List<String> getSets() { return sets; }
    public void setSets(List<String> sets) { this.sets = sets; }

    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }

    public int getCourtNumber() { return courtNumber; }
    public void setCourtNumber(int courtNumber) { this.courtNumber = courtNumber; }

    public String getCourtSurfaceType() { return courtSurfaceType; }
    public void setCourtSurfaceType(String courtSurfaceType) { this.courtSurfaceType = courtSurfaceType; }

    public String getCourtLocation() { return courtLocation; }
    public void setCourtLocation(String courtLocation) { this.courtLocation = courtLocation; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }
}