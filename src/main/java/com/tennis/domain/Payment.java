package com.tennis.domain;

import java.time.LocalDateTime;

public class Payment {
    private Long id; //TODO
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
        }
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
}
