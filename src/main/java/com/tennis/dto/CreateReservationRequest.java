package com.tennis.dto;

public class CreateReservationRequest {
    private Long userId;
    private Long courtId;
    private String startTime; // Format: "2024-12-20T10:00:00"
    private String endTime;

    public CreateReservationRequest() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourtId() {
        return courtId;
    }

    public void setCourtId(Long courtId) {
        this.courtId = courtId;
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
}
