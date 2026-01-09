package com.tennis.dto;

import java.util.List;

public class TournamentDraftDTO {
    private Long tournamentId;
    private String tournamentName;
    private List<MatchDTO> matches;

    public TournamentDraftDTO() {}

    // Gettery i Settery
    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public List<MatchDTO> getMatches() { return matches; }
    public void setMatches(List<MatchDTO> matches) { this.matches = matches; }
}