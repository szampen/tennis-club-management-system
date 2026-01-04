package com.tennis.service;

import com.tennis.database.DatabaseConnection;
import com.tennis.database.UnitOfWork;
import com.tennis.database.UnitOfWorkFactory;
import com.tennis.domain.Court;
import com.tennis.domain.SurfaceType;
import com.tennis.domain.User;
import com.tennis.dto.ApiResponse;
import com.tennis.dto.CourtDTO;
import com.tennis.dto.DTOMapper;
import com.tennis.repository.CourtRepository;
import com.tennis.repository.UserRepository;
import com.tennis.util.CourtFilter;

import java.sql.Connection;
import java.util.List;

public class CourtService {
    private final CourtRepository courtRepository = new CourtRepository();
    private final UserRepository userRepository = new UserRepository();

    //TODO: think about DTO that has less info for list
    public ApiResponse getAllCourts(){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            List<Court> courts = courtRepository.findAll(conn);
            List<CourtDTO> courtDTOS = courts.stream().map(DTOMapper::toCourtDTO).toList();

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

    //TODO: changes when frontend design is known, create DTO for updating
    public ApiResponse updateCourt(Long courtId, CourtDTO dto, Long adminId){
        Connection conn = null;
    try {
        conn = DatabaseConnection.getConnection();

        User admin = userRepository.findById(adminId, conn);
        if(admin == null || !admin.canManageCourts()){
            return new ApiResponse(false, "No permission.");
        }

        Court court = courtRepository.findById(courtId, conn);
        if(court == null){
            return new ApiResponse(false, "Court not found.");
        }

        if(dto.getName() != null && !dto.getName().isBlank()){
            court.setName(dto.getName());
        }
        if(dto.getCourtNumber() > 0 && dto.getPricePerHour() != null){
            court.setCourtNumber(dto.getCourtNumber());
        }
        if(dto.getPricePerHour() > 0 && dto.getPricePerHour() != null){
            court.setPricePerHour(dto.getPricePerHour());
        }
        if(dto.getSurfaceType() != null){
            court.setSurfaceType(SurfaceType.valueOf(dto.getSurfaceType().toUpperCase()));
        }
        court.setHasRoof(dto.isHasRoof());
        court.setAvailableForReservations(dto.isAvailableForReservations());
        court.setLocation(dto.getLocation());
        court.setImageUrl(dto.getImageUrl());

        courtRepository.save(court, conn);

        return new ApiResponse(true, "Court updated.", DTOMapper.toCourtDTO(court));

    } catch (Exception e){
        return new ApiResponse(false, "Error: " + e.getMessage());
    } finally {
        DatabaseConnection.returnConnection(conn);
    }
}

    //TODO: deleting reservations - UnitOfWork
    public ApiResponse deleteCourt(Long courtId, Long adminId){
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();

            User admin = userRepository.findById(adminId,conn);
            if(admin == null || !admin.canManageCourts()){
                return new ApiResponse(false, "No permission.");
            }

            Court court = courtRepository.findById(courtId, conn);
            if(court == null) return new ApiResponse(false, "No court to delete.");

            courtRepository.delete(court, conn);

            return new ApiResponse(true, "Court deleted.");
        } catch (Exception e) {
            return new ApiResponse(false, "Error: " + e.getMessage());
        } finally {
            DatabaseConnection.returnConnection(conn);
        }
    }
}
