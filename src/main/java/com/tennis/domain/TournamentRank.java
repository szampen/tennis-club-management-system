package com.tennis.domain;

public enum TournamentRank {
    TIER_3(50,4),
    TIER_2(100,8),
    TIER_1(250,16);

    private final int basePoints;
    private final int participants;

    TournamentRank(int basePoints, int participants){
        this.basePoints = basePoints;
        this.participants = participants;
    }

    public int getBasePoints() {
        return basePoints;
    }
    public int getParticipants() {return participants;}
}
