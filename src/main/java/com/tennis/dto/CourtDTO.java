package com.tennis.dto;

import com.tennis.domain.Court;

//TODO: might be useless, to implement later if needed
public class CourtDTO {
    private Long id;
    private String name;
    private Integer courtNumber;
    private String surfaceType;
    private boolean hasRoof;
    private String location;
    private String imageUrl;
    private boolean availableForReservations;
    private Double pricePerHour;

    public CourtDTO(Court court){
        this.id = court.getId();
        this.name = court.getName();
        this.courtNumber = court.getCourtNumber();
        this.surfaceType = court.getSurfaceType().name();
        this.hasRoof = court.hasRoof();
        this.location = court.getLocation();
        this.imageUrl = court.getImageUrl();
        this.availableForReservations = court.isAvailableForReservations();
        this.pricePerHour = court.getPricePerHour();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public Integer getCourtNumber() {
        return courtNumber;
    }

    public boolean isAvailableForReservations() {
        return availableForReservations;
    }

    public boolean isHasRoof() {
        return hasRoof;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(String surfaceType) {
        this.surfaceType = surfaceType;
    }

    public void setAvailableForReservations(boolean availableForReservations) {
        this.availableForReservations = availableForReservations;
    }

    public void setHasRoof(boolean hasRoof) {
        this.hasRoof = hasRoof;
    }

    public void setCourtNumber(Integer courtNumber) {
        this.courtNumber = courtNumber;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }
}
