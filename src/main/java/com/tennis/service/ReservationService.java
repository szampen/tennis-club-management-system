package com.tennis.service;

import com.tennis.database.DatabaseConnection;
import com.tennis.database.UnitOfWork;
import com.tennis.database.UnitOfWorkFactory;
import com.tennis.domain.*;
import com.tennis.dto.*;
import com.tennis.repository.*;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {
    private final MatchRepository matchRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final CourtRepository courtRepository;
    private final PaymentRepository paymentRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public ReservationService() {
        this.matchRepository = new MatchRepository();
        this.reservationRepository = new ReservationRepository();
        this.userRepository = new UserRepository();
        this.courtRepository = new CourtRepository();
        this.paymentRepository = new PaymentRepository();
    }

    public ApiResponse createReservation(CreateReservationRequest request){
        UnitOfWork uow = null;
        try{
            uow = UnitOfWorkFactory.create();
            Connection conn = uow.getConnection();

            User user = userRepository.findById(request.getUserId(),conn);
            if(user == null) return new ApiResponse(false, "User not found.");

            Court court = courtRepository.findById(request.getCourtId(), conn);
            if(court == null) return new ApiResponse(false, "Court not found.");

            if(!court.isAvailableForReservations()) return new ApiResponse(false, "Court is not available for reservations.");

            LocalDateTime startTime = LocalDateTime.parse(request.getStartTime(), DATE_TIME_FORMATTER);
            LocalDateTime endTime = LocalDateTime.parse(request.getEndTime(),DATE_TIME_FORMATTER);

            if(startTime.isBefore(LocalDateTime.now())) return new ApiResponse(false, "You cant make a reservation in the past.");

            if(endTime.isBefore(startTime)) return new ApiResponse(false, "End time must be after start time.");

            List<Reservation> existing = reservationRepository.findByCourtIdAndDateRange(
                    court.getId(), startTime, endTime, conn
            );
            boolean isOccupied = existing.stream()
                    .anyMatch(r -> r.getStatus() == ReservationStatus.ACTIVE || r.getStatus() == ReservationStatus.HOLD);

            if (isOccupied) {
                return new ApiResponse(false, "This time slot has just been taken by someone else.");
            }

            Reservation reservation = new Reservation();
            reservation.setUserId(request.getUserId());
            reservation.setCourtId(request.getCourtId());
            reservation.setStartTime(startTime);
            reservation.setEndTime(endTime);

            if(request.isTournament()){
                reservation.setStatus(ReservationStatus.HOLD);
                reservation.setExpiresAt(LocalDateTime.now().plusMinutes(15));
            } else
            {
                reservation.setStatus(ReservationStatus.ACTIVE);
            }

            uow.registerNew(reservation);
            uow.flush();

            Payment payment = new Payment();

            if(!request.isTournament()){
                Double price = reservation.calculatePrice(court.getPricePerHour());

                payment.setAmount(price);
                payment.setPaymentStatus(PaymentStatus.PENDING);
                payment.setReservationId(reservation.getId());

                //Simulation of payment
                payment.processPayment();

                reservation.setPayment(payment);

                uow.registerNew(payment);
            }

            if(request.isTournament() && request.getMatchId() != null){
                Match match = matchRepository.findById(request.getMatchId(), conn);
                if(match != null){
                    match.setCourtId(court.getId());
                    match.setScheduledTime(startTime);
                    uow.registerDirty(match);
                }
            }

            uow.commit();

            if(!request.isTournament()){
                ReservationDetailsDTO dto = DTOMapper.toReservationDetailsDTO(reservation,payment, court,user.getId());
                return new ApiResponse(true, "Reservation created.", dto);
            }
            else {
                ReservationDetailsDTO dto = DTOMapper.toReservationDetailsDTO(reservation,null,court,user.getId());
                return new ApiResponse(true, "Reservation created.", dto);
            }

        } catch (Exception e){
            if (uow != null) uow.rollback();
            return new ApiResponse(false, "Error creating reservation: " + e.getMessage());
        } finally {
            if (uow != null) uow.finish();
        }
    }

    public ApiResponse getReservation(Long reservationId, Long userId){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            Reservation reservation = reservationRepository.findById(reservationId, conn);

            Payment payment = paymentRepository.findByReservationId(reservationId,conn);

            Court court = courtRepository.findById(reservation.getCourtId(),conn);

            ReservationDetailsDTO dto = DTOMapper.toReservationDetailsDTO(reservation, payment, court,userId);

            return new ApiResponse(true, "OK", dto);
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse getUserReservations(Long userId){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            List<Reservation> reservations = reservationRepository.findByUserId(userId,conn);
            if (reservations == null) {
                return new ApiResponse(true, "No reservations found", new ArrayList<>());
            }
            checkReservations(reservations,conn);
            List<ReservationsListDTO> dto = reservations.stream().map(DTOMapper::toReservationsListDTO).toList();

            return new ApiResponse(true, "OK", dto);
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
            List<ReservationsListDTO> dto = reservations.stream().map(DTOMapper::toReservationsListDTO).toList();

            return new ApiResponse(true, "OK", dto);
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse cancelReservation(Long reservationId, Long userId){
        UnitOfWork uow = null;
        try{
            uow = UnitOfWorkFactory.create();
            Connection conn = uow.getConnection();

            Reservation reservation = reservationRepository.findById(reservationId,conn);
            if(reservation == null) return new ApiResponse(false,"Reservation not found.");

            User requester = userRepository.findById(userId,conn);
            if(requester == null) return new ApiResponse(false, "User not found.");

            boolean isOwner = reservation.getUserId().equals(userId);
            boolean isAdmin = "ADMIN".equals(requester.getUserType().name());

            if(!isOwner && !isAdmin){
                return new ApiResponse(false, "No permission to cancel.");
            }

            if(!reservation.isPaymentLoaded()){
                reservation.setPayment(paymentRepository.findByReservationId(reservationId,conn));
            }

            reservation.cancel();
            uow.registerDirty(reservation);
            uow.registerDirty(reservation.getPayment());

            uow.commit();
            return new ApiResponse(true, "Reservation cancelled");
        } catch (Exception e){
            if (uow != null) uow.rollback();
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            if (uow != null) uow.finish();
        }
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
