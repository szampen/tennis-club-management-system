package com.tennis.domain;

public class Court {
    private Long id; //TODO
    private String name;
    private int courtNumber;
    private SurfaceType surfaceType;
    private boolean hasRoof;
    private String location; //TODO
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
}
