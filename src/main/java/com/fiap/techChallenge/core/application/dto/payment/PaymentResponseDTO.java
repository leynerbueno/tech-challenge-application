package com.fiap.techChallenge.core.application.dto.payment;

import java.util.UUID;

public class PaymentResponseDTO {
    private UUID orderId;
    private String status;
    private String  qrCodeImage;

    public PaymentResponseDTO() {
    }

    public PaymentResponseDTO(String status, UUID orderId, String qrCodeImage) {
     this.status = status;
     this.orderId = orderId;
     this.qrCodeImage = qrCodeImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getQrCodeImage() {
        return qrCodeImage;
    }

    public void setQrCodeImage(String qrCodeImage) {
        this.qrCodeImage = qrCodeImage;
    }
}
