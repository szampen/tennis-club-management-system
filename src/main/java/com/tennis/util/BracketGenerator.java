package com.tennis.util;

import com.tennis.database.UnitOfWork;
import com.tennis.domain.Match;
import com.tennis.domain.Player;

import java.util.*;

public class BracketGenerator {
    public static List<Match> generateEmptyBracket(int participantsAmount, Long tournamentId, UnitOfWork uow) throws Exception {
        Map<Integer, List<Match>> roundsMap = new HashMap<>();
        List<Match> allMatches = new ArrayList<>();

        int currentSize = participantsAmount/2;
        int roundIndex = 1;
        int pointsPerRound;

        if(participantsAmount == 4) pointsPerRound = 6;
        else if (participantsAmount == 8) pointsPerRound = 9;
        else pointsPerRound = 16;

        while(currentSize >= 1){
            List<Match> roundMatches = new ArrayList<>();
            for(int i = 0; i < currentSize; i++){
                Match m = new Match();
                m.setTournamentId(tournamentId);
                m.setRound(roundIndex);
                m.setPoints(pointsPerRound);
                uow.registerNew(m);
                roundMatches.add(m);
                allMatches.add(m);
            }
            roundsMap.put(roundIndex,roundMatches);
            roundIndex++;
            pointsPerRound *= 2;
            currentSize /= 2;
        }

        uow.flush();

        for(int r = 1; r < roundIndex - 1; r++){
            List<Match> currentRound = roundsMap.get(r);
            List<Match> nextRound = roundsMap.get(r+1);

            for(int i = 0; i < currentRound.size(); i++){
                Match parentMatch = nextRound.get(i/2);
                currentRound.get(i).setNextMatchId(parentMatch.getId());
                uow.registerDirty(currentRound.get(i));
            }
        }

        return allMatches;
    }

    public static void seedPlayers(List<Player> players, List<Match> matches, UnitOfWork uow){
        Collections.shuffle(players);

        List<Match> firstRoundMatches = matches.stream().filter(m -> m.getRound() == 1).toList();

        int playerIndex = 0;

        for(Match match : firstRoundMatches){
            if(playerIndex < players.size()){
                match.setPlayer1Id(players.get(playerIndex++).getId());
            }

            if (playerIndex < players.size()) {
                match.setPlayer2Id(players.get(playerIndex++).getId());
            }

            uow.registerDirty(match);
        }
    }

}
