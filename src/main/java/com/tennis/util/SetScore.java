package com.tennis.util;

public class SetScore {
    private int player1Games;
    private int player2Games;

    public SetScore(){}

    public SetScore(int p1, int p2){
        this.player2Games = p2;
        this.player1Games = p1;
    }

    public int getPlayer1Games() {
        return player1Games;
    }

    public int getPlayer2Games() {
        return player2Games;
    }

    public void setPlayer1Games(int player1Games) {
        this.player1Games = player1Games;
    }

    public void setPlayer2Games(int player2Games) {
        this.player2Games = player2Games;
    }
}
