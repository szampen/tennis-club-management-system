package com.tennis.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Tournament {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private TournamentRank rank;
    private Double entryFee;
    private Integer rankingRequirement;
    private TournamentStatus status;
    private Integer participants;
    private Long winnerId;

    public Tournament(TournamentRank rank){
        this.rank = rank;
        participants = rank.getParticipants();
        status = TournamentStatus.DRAFT;
    }

    public boolean canPlayerRegister(Player player, int currentParticipants) {
        if (status != TournamentStatus.REGISTRATION_OPEN) {
            return false;
        }
        if (currentParticipants == participants) {
            return false;
        }
        return player.meetsRankingRequirement(rankingRequirement);
    }

    public void openRegistration(){
        if (status == TournamentStatus.REGISTRATION_CLOSED){
            this.status = TournamentStatus.REGISTRATION_OPEN;
        }
    }

    public void closeRegistration() {
        if (status == TournamentStatus.REGISTRATION_OPEN) {
            this.status = TournamentStatus.REGISTRATION_CLOSED;
        }
    }

    public void start() {
        if (status == TournamentStatus.REGISTRATION_CLOSED) {
            this.status = TournamentStatus.ONGOING;
        }
    }

    public void complete() {
        if (status == TournamentStatus.ONGOING) {
            this.status = TournamentStatus.COMPLETED;
        }
    }

    public void cancel() {
        this.status = TournamentStatus.CANCELLED;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public TournamentRank getRank() {
        return rank;
    }

    public void setRank(TournamentRank rank) {
        this.rank = rank;
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

    public Integer getParticipants() {
        return participants;
    }

    public void setParticipants(Integer participants) {
        this.participants = participants;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Tournament that = (Tournament) object;
        if(id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
