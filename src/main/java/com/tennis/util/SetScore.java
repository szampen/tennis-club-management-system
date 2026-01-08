package com.tennis.util;

public class SetScore {
    private final int player1Games;
    private final int player2Games;

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
}
