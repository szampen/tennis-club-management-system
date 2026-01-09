package com.tennis.dto;

import com.tennis.domain.*;
import com.tennis.util.SetScore;
import com.tennis.util.TimeSlot;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DTOMapper {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static UserDTO toUserDTO(User user){
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setUserType(user.getUserType().name());

        if(user instanceof Player){
            dto.setRankingPoints(((Player) user).getRankingPoints());
        }

        return dto;
    }

    public static CourtDTO toCourtDTO(Court court){
        return new CourtDTO(court);
    }

    public static TimeSlotDTO toTimeSlotDTO(TimeSlot slot) {
        TimeSlotDTO dto = new TimeSlotDTO();
        dto.setStartTime(slot.getStartTime().format(DATE_FORMATTER));
        dto.setEndTime(slot.getEndTime().format(DATE_FORMATTER));
        dto.setAvailable(slot.isAvailable());
        dto.setReservationId(slot.getReservationId());
        return dto;
    }

    public static ReservationDTO toReservationDTO(Reservation reservation) {
        if (reservation == null) return null;

        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setStartTime(reservation.getStartTime().format(DATE_FORMATTER));
        dto.setEndTime(reservation.getEndTime().format(DATE_FORMATTER));
        dto.setStatus(reservation.getStatus().name());

        return dto;
    }

    public static PaymentDTO toPaymentDTO(Payment payment) {
        if (payment == null) return null;

        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getPaymentStatus().name());

        if (payment.getPaymentDate() != null) {
            dto.setPaymentDate(payment.getPaymentDate().format(DATE_FORMATTER));
        }

        dto.setTransactionId(payment.getTransactionId());

        return dto;
    }

    public static PlayerDTO toPlayerDTO(Player player){
        PlayerDTO dto = new PlayerDTO();
        dto.setId(player.getId());
        dto.setFullName(player.getFirstName() + " " + player.getLastName());
        dto.setRankingPoints(player.getRankingPoints());
        return dto;
    }

    public static MatchDTO toMatchDTO(Match match) {
        return toMatchDTO(match, null, null);
    }

    public static MatchDTO toMatchDTO(Match match, User p1, User p2) {
        MatchDTO dto = new MatchDTO();
        dto.setId(match.getId());
        dto.setRound(match.getRound());
        dto.setNextMatchId(match.getNextMatchId());
        dto.setTournamentId(match.getTournamentId());
        dto.setReserved(match.getCourtId() != null);
        dto.setStartTime(match.getScheduledTime().format(DATE_FORMATTER));
        dto.setState(match.getWinnerId() != null ? "DONE" : "SCHEDULED");

        List<MatchParticipantDTO> participants = new ArrayList<>();

        participants.add(new MatchParticipantDTO(
                match.getPlayer1Id(),
                p1 != null ? p1.getFirstName() + " " + p1.getLastName() : "TBD",
                match.getWinnerId() != null && match.getWinnerId().equals(match.getPlayer1Id()),
                formatScoreForPlayer(match, true)
        ));

        participants.add(new MatchParticipantDTO(
                match.getPlayer2Id(),
                p2 != null ? p2.getFirstName() + " " + p2.getLastName() : "TBD",
                match.getWinnerId() != null && match.getWinnerId().equals(match.getPlayer2Id()),
                formatScoreForPlayer(match, false)
        ));

        dto.setParticipants(participants);
        return dto;
    }

    public static TournamentDraftDTO toTournamentDraftDTO(Tournament tournament, List<Match> matches) {
        TournamentDraftDTO dto = new TournamentDraftDTO();
        dto.setTournamentId(tournament.getId());
        dto.setTournamentName(tournament.getName());

        List<MatchDTO> matchDTOs = matches.stream()
                .map(DTOMapper::toMatchDTO)
                .toList();

        dto.setMatches(matchDTOs);
        return dto;
    }

    public static TournamentListDTO toTournamentListDTO(Tournament tournament){
        TournamentListDTO dto = new TournamentListDTO();
        dto.setTournamentName(tournament.getName());
        dto.setTournamentRank(tournament.getRank().name());
        dto.setStartDate(tournament.getStartDate().toString());
        dto.setEndDate(tournament.getEndDate().toString());
        dto.setStatus(tournament.getStatus().name());
        return dto;
    }

    public static MatchDetailsDTO toMatchDetailsDTO(Match match, Tournament tournament, Court court, User p1, User p2) {
        MatchDetailsDTO dto = new MatchDetailsDTO();

        dto.setTournamentName(tournament.getName());
        dto.setTournamentRank(tournament.getRank().name());
        dto.setTournamentStatus(tournament.getStatus().name());

        dto.setRoundName(getRoundName(match.getRound(),tournament.getParticipants()));

        dto.setPlayer1Id(match.getPlayer1Id());
        dto.setPlayer1FullName(p1 != null ? p1.getFirstName() + " " + p1.getLastName() : "TBD");
        dto.setPlayer2Id(match.getPlayer2Id());
        dto.setPlayer2FullName(p2 != null ? p2.getFirstName() + " " + p2.getLastName() : "TBD");
        dto.setWinnerId(match.getWinnerId());

        dto.setFinalScore(match.getP1SetsWon() + ":" + match.getP2SetsWon());
        dto.setSets(match.getSets().stream()
                .map(s -> s.getPlayer1Games() + ":" + s.getPlayer2Games())
                .toList());

        if (court != null) {
            dto.setCourtName(court.getName());
            dto.setCourtNumber(court.getCourtNumber());
            dto.setCourtSurfaceType(court.getSurfaceType().name());
            dto.setCourtLocation(court.getLocation());
        }

        if (match.getScheduledTime() != null) {
            dto.setScheduledTime(match.getScheduledTime().format(DATE_FORMATTER));
        }

        return dto;
    }

    private static String getRoundName(int currentRound, int totalParticipants) {
        int totalRounds = (int) (Math.log(totalParticipants) / Math.log(2));
        int roundsLeft = totalRounds - currentRound;

        return switch (roundsLeft) {
            case 0 -> "Final";
            case 1 -> "Semifinal";
            case 2 -> "Quarterfinal";
            case 3 -> "R16";
            case 4 -> "R32";
            default -> "Round " + currentRound;
        };
    }

    private static String formatScoreForPlayer(Match match, boolean isPlayer1) {
        if (match.getSets() == null || match.getSets().isEmpty()) {
            return "0"; // Match did not happen
        }

        StringBuilder sb = new StringBuilder();
        List<SetScore> sets = match.getSets();

        for (int i = 0; i < sets.size(); i++) {
            SetScore s = sets.get(i);
            if (isPlayer1) {
                sb.append(s.getPlayer1Games()).append(":").append(s.getPlayer2Games());
            } else {
                sb.append(s.getPlayer2Games()).append(":").append(s.getPlayer1Games());
            }

            if (i < sets.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
