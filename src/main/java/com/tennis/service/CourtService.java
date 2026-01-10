package com.tennis.service;

import com.tennis.database.DatabaseConnection;
import com.tennis.database.UnitOfWork;
import com.tennis.database.UnitOfWorkFactory;
import com.tennis.domain.Court;
import com.tennis.domain.Reservation;
import com.tennis.domain.SurfaceType;
import com.tennis.domain.User;
import com.tennis.dto.ApiResponse;
import com.tennis.dto.CourtDTO;
import com.tennis.dto.DTOMapper;
import com.tennis.dto.TimeSlotDTO;
import com.tennis.repository.CourtRepository;
import com.tennis.repository.PaymentRepository;
import com.tennis.repository.ReservationRepository;
import com.tennis.repository.UserRepository;
import com.tennis.util.CourtFilter;
import com.tennis.util.CourtSort;
import com.tennis.util.SortDirection;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class CourtService {
    private final CourtRepository courtRepository = new CourtRepository();
    private final UserRepository userRepository = new UserRepository();
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public CourtService(ReservationService reservationService, ReservationRepository reservationRepository, PaymentRepository paymentRepository) {
        this.reservationService = reservationService;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }


    public ApiResponse getAllCourts(){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            List<Court> courts = courtRepository.findAll(conn);
            List<CourtDTO> courtDTOS = courts.stream().map(DTOMapper::toCourtDTO).toList();

            courtDTOS.parallelStream().forEach(this::setFirstAvailableDate);

            return new ApiResponse(true, "OK", courtDTOS);
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse getCourtsFiltered(CourtFilter filter){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            List<Court> courts = courtRepository.findByFilter(filter,conn);
            List<CourtDTO> courtDTOS = courts.stream().map(DTOMapper::toCourtDTO).toList();

            courtDTOS.forEach(this::setFirstAvailableDate);

            if(filter.getCourtSort() == CourtSort.FIRSTDATE){
                if(filter.getDirection() == SortDirection.ASC){
                    List<CourtDTO> sortedCourts = courtDTOS.stream()
                            .sorted(Comparator.comparing(CourtDTO::getFirstAvailableDate,
                                    Comparator.nullsLast(Comparator.naturalOrder())))
                            .toList();
                    return new ApiResponse(true, "OK", sortedCourts);
                } else{
                    List<CourtDTO> sortedCourts = courtDTOS.stream()
                            .sorted(Comparator.comparing(CourtDTO::getFirstAvailableDate,
                                    Comparator.nullsLast(Comparator.reverseOrder())))
                            .toList();
                    return new ApiResponse(true, "OK", sortedCourts);
                }
            }

            return new ApiResponse(true, "OK", courtDTOS);
        } catch (Exception e) {
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse getCourt(Long id){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            Court court = courtRepository.findById(id, conn);

            if(court == null) return new ApiResponse(false, "Court not found.");

            CourtDTO dto = DTOMapper.toCourtDTO(court);

            return new ApiResponse(true, "OK", dto);
        } catch (Exception e) {
            return new ApiResponse(false, "Error: " + e.getMessage());
        }  finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse createCourt(CourtDTO courtDTO, Long adminId){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            if (courtDTO.getName() == null || courtDTO.getName().isBlank()) {
                return new ApiResponse(false, "Court name is required");
            }
            if (courtDTO.getCourtNumber() <= 0) {
                return new ApiResponse(false, "Invalid court number");
            }
            if (courtDTO.getPricePerHour() <= 0) {
                return new ApiResponse(false, "Price must be greater than 0");
            }
            if (courtDTO.getSurfaceType() == null) {
                return new ApiResponse(false, "Surface type is required");
            }

            User admin = userRepository.findById(adminId,conn);
            if(admin == null || !admin.canManageCourts()){
                return new ApiResponse(false, "No permission.");
            }

            Court court = new Court();
            court.setName(courtDTO.getName());
            court.setCourtNumber(courtDTO.getCourtNumber());
            court.setSurfaceType(SurfaceType.valueOf(courtDTO.getSurfaceType().toUpperCase()));
            court.setHasRoof(courtDTO.isHasRoof());
            court.setLocation(courtDTO.getLocation());
            court.setAvailableForReservations(courtDTO.isAvailableForReservations());
            court.setImageUrl(courtDTO.getImageUrl());
            court.setPricePerHour(courtDTO.getPricePerHour());

            courtRepository.save(court, conn);

            CourtDTO dto = DTOMapper.toCourtDTO(court);
            return new ApiResponse(true, "Court created.", dto);
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse changeName(Long courtId, Long adminId, String name){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            User admin = userRepository.findById(adminId, conn);
            if(admin == null || !admin.canManageCourts()){
                return new ApiResponse(false, "No permission.");
            }

            Court court = courtRepository.findById(courtId, conn);
            if(court == null) return new ApiResponse(false, "Court not found.");

            if(name == null || name.isBlank()){
                return new ApiResponse(false, "Please fill in the information.");
            }
            court.setName(name);
            courtRepository.save(court,conn);

            return new ApiResponse(true, "Court name changed");
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse changeCourtNumber(Long courtId, Long adminId, Integer number){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            User admin = userRepository.findById(adminId, conn);
            if(admin == null || !admin.canManageCourts()){
                return new ApiResponse(false, "No permission.");
            }

            Court court = courtRepository.findById(courtId, conn);
            if(court == null) return new ApiResponse(false, "Court not found.");

            if(number == null || number <= 0){
                return new ApiResponse(false, "Incorrect number");
            }
            court.setCourtNumber(number);
            courtRepository.save(court,conn);

            return new ApiResponse(true, "Court number changed");
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse changePricePerHour(Long courtId, Long adminId, Double price){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            User admin = userRepository.findById(adminId, conn);
            if(admin == null || !admin.canManageCourts()){
                return new ApiResponse(false, "No permission.");
            }

            Court court = courtRepository.findById(courtId, conn);
            if(court == null) return new ApiResponse(false, "Court not found.");

            if(price == null || price <= 0){
                return new ApiResponse(false, "Incorrect price");
            }
            court.setPricePerHour(price);
            courtRepository.save(court,conn);

            return new ApiResponse(true, "Court's price per hour changed");
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse changeSurfaceType(Long courtId, Long adminId, String surface){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            User admin = userRepository.findById(adminId, conn);
            if(admin == null || !admin.canManageCourts()){
                return new ApiResponse(false, "No permission.");
            }

            Court court = courtRepository.findById(courtId, conn);
            if(court == null) return new ApiResponse(false, "Court not found.");

            if(surface == null ){
                return new ApiResponse(false, "Incorrect surface type");
            }
            court.setSurfaceType(SurfaceType.valueOf(surface.toUpperCase()));
            courtRepository.save(court,conn);

            return new ApiResponse(true, "Court's surface type changed");
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse changeHasRoof(Long courtId, Long adminId, boolean hasRoof){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            User admin = userRepository.findById(adminId, conn);
            if(admin == null || !admin.canManageCourts()){
                return new ApiResponse(false, "No permission.");
            }

            Court court = courtRepository.findById(courtId, conn);
            if(court == null) return new ApiResponse(false, "Court not found.");

            court.setHasRoof(hasRoof);
            courtRepository.save(court,conn);

            return new ApiResponse(true, "Court's roof option changed");
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse changeAvailableForReservations(Long courtId, Long adminId, boolean available){
        UnitOfWork uow = null;
        try{
            uow = UnitOfWorkFactory.create();
            Connection conn = uow.getConnection();

            User admin = userRepository.findById(adminId, conn);
            if(admin == null || !admin.canManageCourts()){
                return new ApiResponse(false, "No permission.");
            }

            Court court = courtRepository.findById(courtId, conn);
            if(court == null) return new ApiResponse(false, "Court not found.");

            court.setAvailableForReservations(available);
            uow.registerDirty(court);

            if(!available){
                List<Reservation> reservations = reservationRepository.findByCourtId(courtId,conn);
                for(Reservation r : reservations){
                    if(!r.isPaymentLoaded()){
                        r.setPayment(paymentRepository.findByReservationId(r.getId(),conn));
                    }
                    r.cancel();
                    uow.registerDirty(r);
                    uow.registerDirty(r.getPayment());
                }
            }

            uow.commit();

            return new ApiResponse(true, "Court's availability option changed");
        } catch (Exception e){
            if(uow != null) uow.rollback();
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            if (uow != null) uow.finish();
        }
    }

    public ApiResponse changeLocation(Long courtId, Long adminId, String location){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            User admin = userRepository.findById(adminId, conn);
            if(admin == null || !admin.canManageCourts()){
                return new ApiResponse(false, "No permission.");
            }

            Court court = courtRepository.findById(courtId, conn);
            if(court == null) return new ApiResponse(false, "Court not found.");

            court.setLocation(location);
            courtRepository.save(court,conn);

            return new ApiResponse(true, "Court's location changed");
        } catch (Exception e){
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }

    public ApiResponse deleteCourt(Long courtId, Long adminId){
        UnitOfWork uow = null;
        try{
            uow = UnitOfWorkFactory.create();
            Connection conn = uow.getConnection();

            User admin = userRepository.findById(adminId,conn);
            if(admin == null || !admin.canManageCourts()){
                return new ApiResponse(false, "No permission.");
            }

            Court court = courtRepository.findById(courtId, conn);
            if(court == null) return new ApiResponse(false, "No court to delete.");

            List<Reservation> reservations = reservationRepository.findByCourtId(courtId,conn);
            for(Reservation r : reservations){
                if(!r.isPaymentLoaded()){
                    r.setPayment(paymentRepository.findByReservationId(r.getId(),conn));
                }
                r.cancel();
                uow.registerDirty(r);
                uow.registerDirty(r.getPayment());
            }
            uow.registerDeleted(court);

            uow.commit();

            return new ApiResponse(true, "Court deleted.");
        } catch (Exception e) {
            if (uow != null) uow.rollback();
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            if (uow != null) uow.finish();
        }
    }

    @SuppressWarnings("unchecked")
    private void setFirstAvailableDate(CourtDTO dto) {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalDate endLimit = date.plusYears(1);

        while (date.isBefore(endLimit)) {
            ApiResponse response = reservationService.getCourtAvailability(dto.getId(), date);

            if (response.isSuccess() && response.getData() instanceof List) {
                List<TimeSlotDTO> slots = (List<TimeSlotDTO>) response.getData();

                boolean hasAvailableSlot = slots.stream().anyMatch(TimeSlotDTO::isAvailable);

                if (hasAvailableSlot) {
                    dto.setFirstAvailableDate(date);
                    return;
                }
            }
            date = date.plusDays(1);
        }
        dto.setFirstAvailableDate(null);
    }

}
