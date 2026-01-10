package com.tennis.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Payment {
    private Long id;
    private Long reservationId;
    private Double amount;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String transactionId;

    public Payment(){
        this.status = PaymentStatus.PENDING;
    }

    public void processPayment(){
        this.status = PaymentStatus.COMPLETED;
        this.paymentDate = LocalDateTime.now();
        this.transactionId = "TXN-" + System.currentTimeMillis();
    }

    public void refund(){
        if(this.status == PaymentStatus.COMPLETED){
            this.status = PaymentStatus.REFUNDED;
            this.paymentDate = LocalDateTime.now();
            this.transactionId = "TXN-" + System.currentTimeMillis();
        }
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId(){
        return id;
    }

    public Long getReservationId(){
        return reservationId;
    }

    public void setReservationId(Long id){
        this.reservationId = id;
    }

    public Double getAmount(){
        return amount;
    }

    public void setAmount(Double amount){
        this.amount = amount;
    }

    public PaymentStatus getPaymentStatus(){
        return status;
    }

    public void setPaymentStatus(PaymentStatus status){
        this.status = status;
    }

    public LocalDateTime getPaymentDate(){
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime date){
        this.paymentDate = date;
    }

    public String getTransactionId(){
        return transactionId;
    }

    public void setTransactionId(String id){
        this.transactionId = id;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Payment payment = (Payment) object;
        if(id == null || payment.id == null) return false;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
