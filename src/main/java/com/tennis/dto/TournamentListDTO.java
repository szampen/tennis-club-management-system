package com.tennis.dto;

public class TournamentListDTO {
    private String tournamentName;
    private String startDate;
    private String endDate;
    private String tournamentRank;
    private String status;

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTournamentRank() {
        return tournamentRank;
    }

    public void setTournamentRank(String tournamentRank) {
        this.tournamentRank = tournamentRank;
    }
}
