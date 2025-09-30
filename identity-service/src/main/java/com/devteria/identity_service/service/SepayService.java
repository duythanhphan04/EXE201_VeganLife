package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.SepayPaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class SepayService {
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String QR_BASE_URL = "https://qr.sepay.vn/img";
    public String generateQrCodeUrl(SepayPaymentRequest request ) {
        try {
            String encodedDes = URLEncoder.encode(request.getDescription(), StandardCharsets.UTF_8);
            return String.format("%s?acc=%s&bank=ACB&amount=%d&des=%s",
                    QR_BASE_URL, request.getAccountNumber(), request.getAmount(), encodedDes);
        } catch (Exception e) {
            throw new RuntimeException("Error while encoding QR description", e);
        }
    }
}
