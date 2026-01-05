package com.tennis.dto;

public class ReservationDTO {
    private Long id;
    private String courtName;
    private String courtSurface;
    private String startTime;
    private String endTime;
    private String status;

    public ReservationDTO() {}

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

}
