package com.tennis.domain;

import java.time.LocalDateTime;

public class Reservation {
    private Long id;
    private Long userId;
    private Long courtId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;
    private Long version; //TODO - Optimistic Offline Lock

    //TODO - objects for future Lazy Load implementation
    private User user;
    private Court court;
    private Payment payment;

    private boolean userLoaded = false;
    private boolean courtLoaded = false;
    private boolean paymentLoaded = false;

    public Reservation(){
        this.status = ReservationStatus.ACTIVE;
        //TODO - version
    }

    public void cancel(){
        if(this.status == ReservationStatus.ACTIVE){
            this.status = ReservationStatus.CANCELLED;
            if(this.payment != null && paymentLoaded){
                this.payment.refund();
            }
        }
    }

    public void complete(){
        if (LocalDateTime.now().isAfter(this.endTime)){
            this.status = ReservationStatus.COMPLETED;
        }
    }

    public Double calculatePrice(){
        long hours = java.time.Duration.between(startTime,endTime).toHours();
        return hours * this.court.getPricePerHour();
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

    //TODO - version setter/getter


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userLoaded = true;
    }

    public Court getCourt(){
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
        this.courtLoaded = true;
    }

    public Payment getPayment(){
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        this.paymentLoaded = true;
    }

    public boolean isUserLoaded() {
        return userLoaded;
    }

    public boolean isCourtLoaded() {
        return courtLoaded;
    }

    public boolean isPaymentLoaded() {
        return paymentLoaded;
    }
}
