package tams.model;

import java.util.Date;

/**
 * Represents a payment for a booking.
 */
public class Payment {
    
    private String paymentId;
    private double amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private Date paymentDate;
    
    /**
     * Constructor for creating a new payment.
     * 
     * @param paymentId the unique identifier for this payment
     * @param amount the payment amount
     * @param method the payment method
     */
    public Payment(String paymentId, double amount, PaymentMethod method) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
        this.paymentDate = new Date();
    }
    
    /**
     * Process this payment.
     * In a real system, this would integrate with a payment gateway.
     * 
     * @return true if the payment was processed successfully, false otherwise
     */
    public boolean processPayment() {
        // Simulate payment processing
        // In a real system, this would communicate with a payment gateway
        
        // For demonstration, we'll simulate success based on certain conditions
        if (amount > 0 && method != null) {
            status = PaymentStatus.COMPLETED;
            return true;
        }
        
        status = PaymentStatus.FAILED;
        return false;
    }
    
    // Getters and setters
    
    public String getPaymentId() {
        return paymentId;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public PaymentMethod getMethod() {
        return method;
    }
    
    public void setMethod(PaymentMethod method) {
        this.method = method;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    
    public Date getPaymentDate() {
        return new Date(paymentDate.getTime());
    }
    
    @Override
    public String toString() {
        return String.format("Payment #%s: $%.2f via %s - Status: %s", 
            paymentId, amount, method, status);
    }
} 