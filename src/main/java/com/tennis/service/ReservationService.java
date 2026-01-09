package com.tennis.service;

import com.tennis.database.DatabaseConnection;
import com.tennis.database.UnitOfWork;
import com.tennis.database.UnitOfWorkFactory;
import com.tennis.domain.*;
import com.tennis.dto.*;
import com.tennis.repository.CourtRepository;
import com.tennis.repository.PaymentRepository;
import com.tennis.repository.ReservationRepository;
import com.tennis.repository.UserRepository;
import com.tennis.util.TimeSlot;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private static final int OPENING_HOUR = 10;
    private static final int CLOSING_HOUR = 22;
    private static final int SLOT_DURATION = 1;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private ReservationRepository reservationRepository = new ReservationRepository();
    private UserRepository userRepository = new UserRepository();
    private CourtRepository courtRepository = new CourtRepository();
    private PaymentRepository paymentRepository = new PaymentRepository();

    public ApiResponse createReservation(CreateReservationRequest request){
        UnitOfWork uow = null;
        try{
            uow = UnitOfWorkFactory.create();

            User user = userRepository.findById(request.getUserId(),uow.getConnection());
            if(user == null) return new ApiResponse(false, "User not found.");

            Court court = courtRepository.findById(request.getCourtId(), uow.getConnection());
            if(court == null) return new ApiResponse(false, "Court not found.");

            if(!court.isAvailableForReservations()) return new ApiResponse(false, "Court is not available for reservations.");

            LocalDateTime startTime = LocalDateTime.parse(request.getStartTime(), DATE_TIME_FORMATTER);
            LocalDateTime endTime = LocalDateTime.parse(request.getEndTime(),DATE_TIME_FORMATTER);

            if(startTime.isBefore(LocalDateTime.now())) return new ApiResponse(false, "You cant make a reservation in the past.");

            if(endTime.isBefore(startTime)) return new ApiResponse(false, "End time must be after start time.");

            Reservation reservation = new Reservation();
            reservation.setUserId(request.getUserId());
            reservation.setCourtId(request.getCourtId());
            reservation.setStartTime(startTime);
            reservation.setEndTime(endTime);
            reservation.setStatus(ReservationStatus.ACTIVE);

            Double price = reservation.calculatePrice(court.getPricePerHour());

            uow.registerNew(reservation);
            uow.flush();

            Payment payment = new Payment();
            payment.setAmount(price);
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setReservationId(reservation.getId());

            //Simulation of payment
            payment.processPayment();

            reservation.setPayment(payment);

            uow.registerNew(payment);
            uow.commit();

            ReservationDTO dto = DTOMapper.toReservationDTO(reservation);
            dto.setCourtName(court.getName());
            dto.setCourtSurface(court.getSurfaceType().name());

            return new ApiResponse(true, "Reservation created.", dto);
        } catch (Exception e){
            if (uow != null) uow.rollback();
            return new ApiResponse(false, "Error creating reservation: " + e.getMessage());
        } finally {
            if (uow != null) uow.finish();
        }
    }

    public ApiResponse getUserReservations(Long userId){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            List<Reservation> reservations = reservationRepository.findByUserId(userId,conn);
            checkReservations(reservations,conn);
            List<ReservationDTO> dtos = reservations.stream().map(DTOMapper::toReservationDTO).toList();

            return new ApiResponse(true, "OK", dtos);
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse getCourtReservations(Long courtId){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            List<Reservation> reservations = reservationRepository.findByCourtId(courtId,conn);
            checkReservations(reservations, conn);
            List<ReservationDTO> dtos = reservations.stream().map(DTOMapper::toReservationDTO).toList();

            return new ApiResponse(true, "OK", dtos);
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse cancelReservation(Long reservationId, Long userId){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            Reservation reservation = reservationRepository.findById(reservationId,conn);
            if(reservation == null) return new ApiResponse(false,"Reservation not found.");

            if(!reservation.getUserId().equals(userId)) return new ApiResponse(false, "No permission to cancel.");

            if(!reservation.isPaymentLoaded()){
                reservation.setPayment(paymentRepository.findByReservationId(reservationId,conn));
            }

            reservation.cancel();

            return new ApiResponse(true, "Reservation cancelled");
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse getCourtAvailability(Long courtId, LocalDate date){
        try{
            Court court = courtRepository.findById(courtId, DatabaseConnection.getConnection());
            if (court == null) return new ApiResponse(false, "Court not found.");

            List<TimeSlot> slots = generateTimeSlots(date);

            LocalDateTime dayStart = date.atTime(0,0);
            LocalDateTime dayEnd = date.atTime(23,59);

            List<Reservation> reservations = reservationRepository.findByCourtIdAndDateRange(courtId,dayStart,dayEnd,DatabaseConnection.getConnection());

            for(Reservation reservation : reservations){
                if(reservation.getStatus() == ReservationStatus.ACTIVE){
                    markSlotAsOccupied(slots,reservation);
                }
            }

            List<TimeSlotDTO> dtos = slots.stream().map(DTOMapper::toTimeSlotDTO).toList();

            return new ApiResponse(true, "OK", dtos);

        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        }
    }

    private List<TimeSlot> generateTimeSlots(LocalDate date){
        List<TimeSlot> slots = new ArrayList<>();

        for(int hour = OPENING_HOUR; hour < CLOSING_HOUR; hour+=SLOT_DURATION){
            LocalDateTime start = date.atTime(hour, 0);
            LocalDateTime end = start.plusHours(SLOT_DURATION);

            slots.add(new TimeSlot(start,end));
        }

        return slots;
    }

    private void markSlotAsOccupied(List<TimeSlot> slots, Reservation reservation){
        for(TimeSlot slot : slots){
            if(reservationOverlapsSlot(reservation, slot)){
                slot.setAvailable(false);
                slot.setReservationId(reservation.getId());
            }
        }
    }

    private boolean reservationOverlapsSlot(Reservation reservation, TimeSlot slot){
        LocalDateTime resStart = reservation.getStartTime();
        LocalDateTime resEnd = reservation.getEndTime();
        LocalDateTime slotStart = slot.getStartTime();
        LocalDateTime slotEnd = slot.getEndTime();

        return resStart.isBefore(slotEnd) && resEnd.isAfter(slotStart);
    }

    private void checkReservations(List<Reservation> reservations, Connection connection){
        for(Reservation r : reservations){
            if(r.getStatus() == ReservationStatus.ACTIVE && LocalDateTime.now().isAfter(r.getEndTime())){
                r.setStatus(ReservationStatus.COMPLETED);
                reservationRepository.save(r,connection);
            }
        }
    }


}
