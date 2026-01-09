package com.tennis.dto;

public class CreateTournamentRequest {
    private String name;
    private String tournamentRank;
    private Double entryFee;
    private Integer rankingRequirement;

    public ApiResponse validate(){
        if(name == null || name.trim().isEmpty()){
            return new ApiResponse(false, "Tournament name cannot be empty.");
        }
        if(tournamentRank == null || tournamentRank.isEmpty()){
            return new ApiResponse(false, "You have to choose tournament rank from the list.");
        }
        if(entryFee != null && entryFee < 0){
            return new ApiResponse(false, "Entry fee cannot be negative.");
        }
        if(rankingRequirement != null && rankingRequirement < 0){
            return new ApiResponse(false, "Requirement of ranking points cannot be negative.");
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTournamentRank() {
        return tournamentRank;
    }

    public void setTournamentRank(String tournamentRank) {
        this.tournamentRank = tournamentRank;
    }

    public Double getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(Double entryFee) {
        this.entryFee = entryFee;
    }

    public Integer getRankingRequirement() {
        return rankingRequirement;
    }

    public void setRankingRequirement(Integer rankingRequirement) {
        this.rankingRequirement = rankingRequirement;
    }
}
