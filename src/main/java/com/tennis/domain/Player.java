package com.tennis.domain;

public class Player extends User{
    private int rankingPoints;

    public Player(){
        super();
        this.rankingPoints = 0;
        setUserType(UserType.PLAYER);
    }

    @Override
    public boolean canManageCourts(){
        return false;
    }

    public void addRankingPoints(int points){
        this.rankingPoints += points;
    }

    public void subtractRankingPoints(int points){
        this.rankingPoints -= points;
    }

    public boolean meetsRankingRequirement(int requiredPoints){
        return this.rankingPoints >= requiredPoints;
    }

    public int getRankingPoints(){
        return rankingPoints;
    }

    public void setRankingPoints(int points){
        this.rankingPoints = points;
    }

}
