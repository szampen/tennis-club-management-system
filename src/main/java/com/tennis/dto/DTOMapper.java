package com.tennis.dto;

import com.tennis.domain.*;
import com.tennis.util.TimeSlot;

import java.time.format.DateTimeFormatter;

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
        dto.setStartTime(slot.getStartTime().toString());
        dto.setEndTime(slot.getEndTime().toString());
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
}
