package com.tennis.domain;

import java.util.Objects;

public class Court {
    private Long id;
    private String name;
    private int courtNumber;
    private SurfaceType surfaceType;
    private boolean hasRoof;
    private String location;
    private String imageUrl; //TODO
    private boolean availableForReservations;
    private Double pricePerHour;

    public Court() {}

    public boolean isAvailableForReservations(){
        return availableForReservations;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getCourtNumber(){
        return courtNumber;
    }

    public void setCourtNumber(int number){
        this.courtNumber = number;
    }

    public SurfaceType getSurfaceType(){
        return surfaceType;
    }

    public void setSurfaceType(SurfaceType type){
        this.surfaceType = type;
    }

    public boolean hasRoof(){
        return hasRoof;
    }

    public void setHasRoof(boolean hasRoof){
        this.hasRoof = hasRoof;
    }

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public void setAvailableForReservations(boolean available){
        this.availableForReservations = available;
    }

    public Double getPricePerHour(){
        return pricePerHour;
    }

    public void setPricePerHour(Double price){
        this.pricePerHour = price;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Court court = (Court) object;
        if (id == null || court.id == null) return false;
        return Objects.equals(id, court.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
