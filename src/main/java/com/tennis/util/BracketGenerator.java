package com.tennis.util;

import com.tennis.database.UnitOfWork;
import com.tennis.database.UnitOfWorkFactory;
import com.tennis.domain.Match;
import com.tennis.domain.Player;

import java.util.*;

public class BracketGenerator {
    public static List<Match> generateEmptyBracket(int participantsAmount, Long tournamentId) {
        UnitOfWork uow = null;
        try {
            uow = UnitOfWorkFactory.create();

            Map<Integer, List<Match>> roundsMap = new HashMap<>();
            List<Match> allMatches = new ArrayList<>();

            int currentSize = participantsAmount/2;
            int roundIndex = 1;

            while(currentSize >= 1){
                List<Match> roundMatches = new ArrayList<>();
                for(int i = 0; i < currentSize; i++){
                    Match m = new Match();
                    m.setTournamentId(tournamentId);
                    m.setRound(roundIndex);
                    uow.registerNew(m);
                    roundMatches.add(m);
                    allMatches.add(m);
                }
                roundsMap.put(roundIndex,roundMatches);
                roundIndex++;
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

            uow.commit();
            return allMatches;
        } catch (Exception e) {
            if (uow != null) uow.rollback();
            throw new RuntimeException("Error generating bracket", e);
        } finally {
            if (uow != null) uow.finish();
        }
    }

    public static void seedPlayers(List<Player> players, List<Match> matches){
        UnitOfWork uow = null;
        try{
            uow = UnitOfWorkFactory.create();

            Collections.shuffle(players);

            List<Match> firstRoundMatches = matches.stream().filter(m -> m.getRound() == 1).toList();

            int availableSlots = firstRoundMatches.size() * 2;
            if(players.size() > availableSlots){
                throw new IllegalArgumentException("Too many players for this bracket.");
            }

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

            uow.commit();
        } catch (Exception e) {
            if (uow != null) uow.rollback();
            throw new RuntimeException("Error seeding players into bracket", e);
        } finally {
            if (uow != null) uow.finish();
        }
    }

}
