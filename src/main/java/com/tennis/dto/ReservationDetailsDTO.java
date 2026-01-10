package com.tennis.dto;

public class ReservationDetailsDTO {
    private Long id;
    private String startTime;
    private String endTime;
    private String status;

    private String courtName;
    private int courtNumber;
    private String courtSurface;
    private String courtLocation;
    private boolean courtHasRoof;

    private PaymentDTO payment;

    public ReservationDetailsDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getCourtSurface() {
        return courtSurface;
    }

    public void setCourtSurface(String courtSurface) {
        this.courtSurface = courtSurface;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCourtNumber() {
        return courtNumber;
    }

    public void setCourtNumber(int courtNumber) {
        this.courtNumber = courtNumber;
    }

    public String getCourtLocation() {
        return courtLocation;
    }

    public void setCourtLocation(String courtLocation) {
        this.courtLocation = courtLocation;
    }

    public boolean isCourtHasRoof() {
        return courtHasRoof;
    }

    public void setCourtHasRoof(boolean courtHasRoof) {
        this.courtHasRoof = courtHasRoof;
    }

    public PaymentDTO getPayment() {
        return payment;
    }

    public void setPayment(PaymentDTO payment) {
        this.payment = payment;
    }
}
