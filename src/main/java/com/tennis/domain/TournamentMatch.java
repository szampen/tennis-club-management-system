package com.tennis.domain;

import com.tennis.util.SetScore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TournamentMatch {
    private Long id;
    private Long tournamentId;
    private Long player1Id;
    private Long player2Id;
    private Long winnerId;  // null - if not played
    private Long nextMatchId;
    private List<SetScore> sets;
    private Integer points;
    private Long reservationId;
    private LocalDateTime scheduledTime;

    public TournamentMatch(){
        this.sets = new ArrayList<>();
    }

    public void addSet(int p1, int p2){
        sets.add(new SetScore(p1,p2));
    }

    public void getWinner(){
        int p1 = 0;
        int p2 = 0;
        for(SetScore s : sets){
            if(s.getPlayer1Games() > s.getPlayer2Games()) p1++;
            else p2++;
        }
        if(p1 > p2) winnerId = player1Id;
        else winnerId = player2Id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Long getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(Long player1Id) {
        this.player1Id = player1Id;
    }

    public Long getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(Long player2Id) {
        this.player2Id = player2Id;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public Long getNextMatchId() {
        return nextMatchId;
    }

    public void setNextMatchId(Long nextMatchId) {
        this.nextMatchId = nextMatchId;
    }

    public List<SetScore> getSets() {
        return sets;
    }

    public void setSets(List<SetScore> sets) {
        this.sets = sets;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

}
