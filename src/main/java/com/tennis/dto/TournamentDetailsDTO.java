package com.tennis.dto;

import java.util.List;

public class TournamentDetailsDTO {
    private Long id;
    private String name;
    private String status;
    private double entryFee;
    private int maxParticipants;
    private int currentParticipants;
    private PlayerDTO winner;

    private boolean isCurrentUserRegistered;

    private List<PlayerDTO> participants;

    private List<MatchDTO> matches;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(double entryFee) {
        this.entryFee = entryFee;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public List<PlayerDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<PlayerDTO> participants) {
        this.participants = participants;
    }

    public List<MatchDTO> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchDTO> matches) {
        this.matches = matches;
    }

    public boolean isCurrentUserRegistered() {
        return isCurrentUserRegistered;
    }

    public void setCurrentUserRegistered(boolean currentUserRegistered) {
        isCurrentUserRegistered = currentUserRegistered;
    }

    public int getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(int currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public PlayerDTO getWinner() {
        return winner;
    }

    public void setWinner(PlayerDTO winner) {
        this.winner = winner;
    }
}
