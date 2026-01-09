package com.tennis.dto;

import java.util.List;

public class MatchDTO {
    private Long id;
    private Integer round;
    private Long nextMatchId;
    private Long tournamentId;
    private boolean isReserved;
    private String startTime;
    private String state;

    private List<MatchParticipantDTO> participants;

    public MatchDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getRound() { return round; }
    public void setRound(Integer round) { this.round = round; }

    public Long getNextMatchId() { return nextMatchId; }
    public void setNextMatchId(Long nextMatchId) { this.nextMatchId = nextMatchId; }

    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }

    public boolean isReserved() { return isReserved; }
    public void setReserved(boolean reserved) { isReserved = reserved; }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<MatchParticipantDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<MatchParticipantDTO> participants) {
        this.participants = participants;
    }
}

