package com.tennis.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reservation {
    private Long id;
    private Long userId;
    private Long courtId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;
    private LocalDateTime expiresAt;

    private Payment payment;

    private boolean paymentLoaded = false;

    public Reservation(){}

    public void cancel(){
        if(this.status == ReservationStatus.ACTIVE){
            this.status = ReservationStatus.CANCELLED;
            if(this.payment != null && paymentLoaded){
                this.payment.refund();
            }
        }
    }

    //TODO: check
    public void complete(){
        if (LocalDateTime.now().isAfter(this.endTime)){
            this.status = ReservationStatus.COMPLETED;
        }
    }

    public Double calculatePrice(Double price){
        long hours = java.time.Duration.between(startTime,endTime).toHours();
        return hours * price;
    }

    public void setId(Long id){
        this.id = id;
    }
    public Long getId(){
        return id;
    }

    public Long getUserId(){
        return userId;
    }

    public void setUserId(Long id){
        this.userId = id;
    }

    public Long getCourtId(){
        return courtId;
    }

    public void setCourtId(Long id){
        this.courtId = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    //TODO: check
    public Payment getPayment(){
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        this.paymentLoaded = true;
    }

    public boolean isPaymentLoaded() {
        return paymentLoaded;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Reservation that = (Reservation) object;
        if(id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
