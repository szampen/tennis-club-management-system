package com.tennis.service;

import com.tennis.database.DatabaseConnection;
import com.tennis.database.UnitOfWork;
import com.tennis.database.UnitOfWorkFactory;
import com.tennis.domain.*;
import com.tennis.dto.*;
import com.tennis.repository.*;
import com.tennis.util.BracketGenerator;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tennis.util.BracketGenerator.seedPlayers;

@Service
public class TournamentService{
    private final TournamentRepository tournamentRepository = new TournamentRepository();
    private final MatchRepository matchRepository = new MatchRepository();
    private final UserRepository userRepository = new UserRepository();
    private final ReservationRepository reservationRepository;
    private final CourtRepository courtRepository = new CourtRepository();

    private LocalDateTime lastCheck = LocalDateTime.MIN;

    public TournamentService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public ApiResponse createTournament(CreateTournamentRequest request){
        ApiResponse validateResponse = request.validate();
        if(validateResponse != null) return validateResponse;

        UnitOfWork uow = null;
        try{
            uow = UnitOfWorkFactory.create();

            Tournament tournament = new Tournament(TournamentRank.valueOf(request.getTournamentRank()));
            tournament.setName(request.getName());
            tournament.setEntryFee(request.getEntryFee() != null ? request.getEntryFee() : 0.0);
            tournament.setRankingRequirement(request.getRankingRequirement() != null ? request.getRankingRequirement() : 0);

            uow.registerNew(tournament);
            uow.flush();

            List<Match> matchDrafts = BracketGenerator.generateEmptyBracket(tournament.getParticipants(), tournament.getId(),uow);

            uow.commit();

            TournamentDraftDTO draftDTO = DTOMapper.toTournamentDraftDTO(tournament,matchDrafts);

            return new ApiResponse(true, "Tournament created.", draftDTO);
        } catch (Exception e) {
            if (uow != null) uow.rollback();
            return new ApiResponse(false, "Error creating tournament: " + e.getMessage());
        } finally {
            if (uow != null) uow.finish();
        }
    }

    public ApiResponse finalizeTournament(Long tournamentId){
        UnitOfWork uow = null;
        try{
            uow = UnitOfWorkFactory.create();

            Tournament tournament = tournamentRepository.findById(tournamentId,uow.getConnection());
            if(tournament == null) return new ApiResponse(false, "Tournament not found.");
            if(tournament.getStatus() != TournamentStatus.DRAFT){
                return new ApiResponse(false, "Only draft tournaments can be finalized.");
            }

            List<Match> matches = matchRepository.findByTournament(tournamentId,uow.getConnection());

            if (matches.isEmpty()) {
                return new ApiResponse(false, "No matches generated for this tournament.");
            }

            for(Match m : matches){
                if(m.getCourtId() == null || m.getScheduledTime() == null){
                    return new ApiResponse(false, "Not all matches have scheduled reservations.");
                }
            }

            for(Match m : matches){
                reservationRepository.confirmTournamentReservation(m.getId(),uow.getConnection());
            }

            tournament.setStatus(TournamentStatus.REGISTRATION_CLOSED);

            LocalDateTime firstMatch = matches.stream()
                    .map(Match::getScheduledTime)
                    .min(LocalDateTime::compareTo)
                    .orElseThrow(() -> new IllegalStateException("Matches exist but no start time found"));

            LocalDateTime lastMatch = matches.stream()
                    .map(Match::getScheduledTime)
                    .max(LocalDateTime::compareTo)
                    .orElseThrow(() -> new IllegalStateException("Matches exist but no end time found"));

            tournament.setStartDate(firstMatch.toLocalDate());
            tournament.setEndDate(lastMatch.toLocalDate());

            uow.registerDirty(tournament);

            uow.commit();

            return new ApiResponse(true, "Tournament is now live.");
        } catch (Exception e) {
            if (uow != null) uow.rollback();
            return new ApiResponse(false, "Finalization failed: " + e.getMessage());
        } finally {
            if (uow != null) uow.finish();
        }
    }

    public ApiResponse closeRegistrationForTournament(Tournament tournament){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            tournament.closeRegistration();

            if(tournament.getStatus() != TournamentStatus.REGISTRATION_CLOSED){
                return new ApiResponse(false, "Registration must be open for you to close it.");
            }

            return new ApiResponse(true, "Registration is closed for " + tournament.getName());

        } catch (Exception e) {
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse openRegistrationForTournament(Tournament tournament){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            tournament.openRegistration();

            if(tournament.getStatus() != TournamentStatus.REGISTRATION_OPEN){
                return new ApiResponse(false, "Registration must be closed for you to open it.");
            }

            return new ApiResponse(true, "Registration is open for " + tournament.getName());

        } catch (Exception e) {
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse cancelTournament(Tournament tournament){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            tournament.cancel();

            return new ApiResponse(true, "Registration is open for " + tournament.getName());

        } catch (Exception e) {
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse registerForTournament(Long userId, Long tournamentId) {
        UnitOfWork uow = null;
        try {
            uow = UnitOfWorkFactory.create();
            Connection conn = uow.getConnection();

            //Pessimistic Offline Lock
            Tournament tournament = tournamentRepository.findByIdForUpdate(tournamentId, conn);
            Player player = (Player) userRepository.findById(userId, conn);

            if (tournament == null) return new ApiResponse(false, "Tournament not found.");
            if (player == null) return new ApiResponse(false, "Player not found.");

            int currentCount = tournamentRepository.numberOfParticipants(tournamentId, conn);
            if (!tournament.canPlayerRegister(player, currentCount)) {
                return new ApiResponse(false, "Registration impossible: lack of places, wrong status or ranking points.");
            }

            if (tournamentRepository.isUserInTournament(userId, tournamentId, conn)) {
                return new ApiResponse(false, "You are already registered.");
            }

            tournamentRepository.insertParticipant(userId, tournamentId, conn);

            uow.commit();
            return new ApiResponse(true, "Successfully registered for " + tournament.getName());

        } catch (Exception e) {
            if (uow != null) uow.rollback();
            return new ApiResponse(false, "Error during registration: " + e.getMessage());
        } finally {
            if (uow != null) uow.finish();
        }
    }

    public ApiResponse withdrawFromTournament(Long userId, Long tournamentId) {
        UnitOfWork uow = null;
        try {
            uow = UnitOfWorkFactory.create();
            Connection conn = uow.getConnection();

            Tournament tournament = tournamentRepository.findByIdForUpdate(tournamentId, conn);
            if (tournament.getStatus() != TournamentStatus.REGISTRATION_OPEN) {
                return new ApiResponse(false, "Cannot withdraw after registration is closed.");
            }

            if (!tournamentRepository.isUserInTournament(userId, tournamentId, conn)) {
                return new ApiResponse(false, "You are not a participant of this tournament.");
            }

            tournamentRepository.deleteParticipant(userId, tournamentId, conn);

            uow.commit();
            return new ApiResponse(true, "Successfully withdrawn and refund processed.");

        } catch (Exception e) {
            if (uow != null) uow.rollback();
            return new ApiResponse(false, "Error during withdrawal: " + e.getMessage());
        } finally {
            if (uow != null) uow.finish();
        }
    }

    public ApiResponse getAllTournaments(){
        this.closeTournamentAndSeedPlayers();
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();
            reservationRepository.cleanupExpiredHolds(conn);

            List<Tournament> tournaments = tournamentRepository.findAll(conn);

            List<TournamentListDTO> dtos = tournaments.stream().map(DTOMapper::toTournamentListDTO).toList();

            return new ApiResponse(true, "OK", dtos);
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse getTournamentByStatus(TournamentStatus status){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            List<Tournament> tournaments = tournamentRepository.findByStatus(status,conn);

            List<TournamentListDTO> dtos = tournaments.stream().map(DTOMapper::toTournamentListDTO).toList();

            return new ApiResponse(true, "OK", dtos);
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse getTournament(Long tournamentId, Long currentUserId){
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

            Tournament tournament = tournamentRepository.findById(tournamentId,conn);
            if(tournament == null) return new ApiResponse(false, "Tournament not found.");

            int registeredParticipants = tournamentRepository.numberOfParticipants(tournamentId,conn);

            List<Player> players = userRepository.findByTournament(tournamentId,conn);

            List<Match> matches = matchRepository.findByTournament(tournamentId, conn);

            Map<Long, Player> playerMap = players.stream().collect(Collectors.toMap(Player::getId, p -> p));

            boolean isRegistered = false;
            if (currentUserId != null) {
                isRegistered = tournamentRepository.isUserInTournament(currentUserId, tournamentId, conn);
            }


            TournamentDetailsDTO dto = new TournamentDetailsDTO();
            dto.setId(tournament.getId());
            dto.setName(tournament.getName());
            dto.setStatus(tournament.getStatus().name());
            dto.setEntryFee(tournament.getEntryFee());
            dto.setMaxParticipants(tournament.getParticipants());
            dto.setCurrentUserRegistered(isRegistered);
            dto.setCurrentParticipants(registeredParticipants);

            dto.setParticipants(players.stream().map(DTOMapper::toPlayerDTO).toList());

            List<MatchDTO> matchDTOs = matches.stream()
                    .map(match -> {
                        Player p1 = playerMap.get(match.getPlayer1Id());
                        Player p2 = playerMap.get(match.getPlayer2Id());

                        return DTOMapper.toMatchDTO(match, p1, p2);
                    })
                    .toList();

            dto.setMatches(matchDTOs);

            if(tournament.getWinnerId() != null){
                Player winner = playerMap.get(tournament.getWinnerId());
                if(winner != null) dto.setWinner(DTOMapper.toPlayerDTO(winner));
            }

            return new ApiResponse(true, "OK", dto);
        } catch (Exception e) {
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse getMatch(Long matchId){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            Match match = matchRepository.findById(matchId, conn);
            if (match == null) return new ApiResponse(false, "Match does not exist.");

            Tournament tournament = tournamentRepository.findById(match.getTournamentId(), conn);

            Court court = null;
            if (match.getCourtId() != null) {
                court = courtRepository.findById(match.getCourtId(), conn);
            }

            User p1 = (match.getPlayer1Id() != null) ? userRepository.findById(match.getPlayer1Id(), conn) : null;
            User p2 = (match.getPlayer2Id() != null) ? userRepository.findById(match.getPlayer2Id(), conn) : null;

            MatchDetailsDTO dto = DTOMapper.toMatchDetailsDTO(match, tournament, court, p1, p2);

            return new ApiResponse(true, "Match details retrieved.", dto);
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse addScoreSetToMatch(Long matchId, int p1Games, int p2Games) {
        UnitOfWork uow = null;
        try {
            uow = UnitOfWorkFactory.create();
            Connection conn = uow.getConnection();

            Match match = matchRepository.findById(matchId, conn);
            if (match == null) return new ApiResponse(false, "Match does not exist.");
            if (match.getWinnerId() != null) return new ApiResponse(false, "Match is already completed.");

            match.addSet(p1Games, p2Games);

            uow.registerDirty(match);

            // Check if end of adding scores - BO3
            if (match.getP1SetsWon() == 2 || match.getP2SetsWon() == 2) {
                uow.commit();
                return completeMatch(matchId);
            }

            uow.commit();
            return new ApiResponse(true, "Set added successfully.");
        } catch (Exception e) {
            if (uow != null) uow.rollback();
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            if (uow != null) uow.finish();
        }
    }

    public ApiResponse completeMatch(Long matchId) {
        UnitOfWork uow = null;
        try {
            uow = UnitOfWorkFactory.create();
            Connection conn = uow.getConnection();

            Match match = matchRepository.findById(matchId, conn);
            Tournament tournament = tournamentRepository.findById(match.getTournamentId(), conn);

            match.getWinner();
            Long winnerId = match.getWinnerId();
            Long loserId = (winnerId.equals(match.getPlayer1Id())) ? match.getPlayer2Id() : match.getPlayer1Id();

            // points for loser
            Player loser = (Player) userRepository.findById(loserId, conn);
            if (match.getPoints() != null) {
                loser.addRankingPoints(match.getPoints());
                uow.registerDirty(loser);
            }

            // check if it was final
            if (match.getNextMatchId() == null) {
                Player tournamentWinner = (Player) userRepository.findById(winnerId, conn);
                tournamentWinner.addRankingPoints(tournament.getRank().getBasePoints());
                uow.registerDirty(tournamentWinner);

                // set winner in tournament
                tournament.setWinnerId(winnerId);
                tournament.setStatus(TournamentStatus.COMPLETED);
                uow.registerDirty(tournament);
            } else {
                // not final => to next match
                Match nextMatch = matchRepository.findById(match.getNextMatchId(), conn);

                if (nextMatch.getPlayer1Id() == null) {
                    nextMatch.setPlayer1Id(winnerId);
                } else {
                    nextMatch.setPlayer2Id(winnerId);
                }
                uow.registerDirty(nextMatch);
            }

            uow.registerDirty(match);
            uow.commit();

            return new ApiResponse(true, "Match finished. Winner moved forward.");
        } catch (Exception e) {
            if (uow != null) uow.rollback();
            return new ApiResponse(false, "Error closing match: " + e.getMessage());
        } finally {
            if (uow != null) uow.finish();
        }
    }

    private void closeTournamentAndSeedPlayers(){
        if (lastCheck.isAfter(LocalDateTime.now().minusMinutes(10))) {
            return;
        }

        UnitOfWork uow = null;
        try {
            uow = UnitOfWorkFactory.create();
            Connection conn = uow.getConnection();

            LocalDate targetDate = LocalDate.now().plusDays(2);
            List<Tournament> tournaments = tournamentRepository.findByStartDateAndStatus(
                    targetDate, TournamentStatus.REGISTRATION_OPEN, conn);

            for (Tournament tournament : tournaments) {
                List<Player> signedUpPlayers = userRepository.findByTournament(tournament.getId(), conn);

                if (signedUpPlayers.size() < tournament.getParticipants()) {
                    tournament.cancel();
                    uow.registerDirty(tournament);
                } else {
                    List<Match> matches = matchRepository.findByTournament(tournament.getId(), conn);

                    seedPlayers(signedUpPlayers, matches, uow);

                    tournament.setStatus(TournamentStatus.REGISTRATION_CLOSED);
                    uow.registerDirty(tournament);
                }
            }

            uow.commit();

            this.lastCheck = LocalDateTime.now();
        } catch (Exception e) {
            if (uow != null) uow.rollback();
            System.err.println("Critical error in automated tournament processing: " + e.getMessage());
        } finally {
            if (uow != null) uow.finish();
        }
    }

}