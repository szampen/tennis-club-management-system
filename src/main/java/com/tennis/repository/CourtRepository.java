package com.tennis.repository;

import com.tennis.domain.Court;
import com.tennis.domain.SurfaceType;
import com.tennis.mapper.CourtMapper;

import java.sql.Connection;
import java.util.List;

public class CourtRepository {
    private CourtMapper mapper = new CourtMapper();

    public Court findById(Long id, Connection connection){
        try{
            return mapper.findById(id, connection);
        } catch (Exception e){
            throw new RuntimeException("Error fetching court.", e);
        }
    }

    public List<Court> findAll(Connection connection){
        try{
            return mapper.findAllCourts(connection);
        } catch (Exception e){
            throw new RuntimeException("Error fetching courts list.", e);
        }
    }

    public List<Court> findAvailableForReservations(Connection connection){
        try{
            return mapper.findAvailableForReservations(connection);
        } catch (Exception e){
            throw new RuntimeException("Error fetching available courts.",e);
        }
    }

    public List<Court> findBySurface(SurfaceType surfaceType, Connection connection){
        try{
            return mapper.findBySurface(surfaceType,connection);
        } catch (Exception e){
            throw new RuntimeException("Error fetching courts list.", e);
        }
    }

    public List<Court> findByRoof(Boolean hasRoof, Connection connection){
        try{
            return mapper.findByRoof(hasRoof,connection);
        } catch (Exception e){
            throw new RuntimeException("Error fetching courts list.",e);
        }
    }

    public void save(Court court, Connection connection){
        try{
            if(court.getId() == null){
                mapper.insert(court, connection);
            } else {
                mapper.update(court,connection);
            }
        } catch (Exception e){
            throw new RuntimeException("Error saving court.", e);
        }
    }

    public void delete(Court court, Connection connection){
        try{
            if(court != null){
                mapper.delete(court,connection);
            }
        } catch (Exception e){
            throw new RuntimeException("Error deleting court.", e);
        }
    }
}
