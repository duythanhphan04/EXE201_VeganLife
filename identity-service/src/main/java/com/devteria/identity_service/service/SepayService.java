package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.SepayPaymentRequest;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SepayService {
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
    public  Map<String, String> parseUserAndCoach(String content) {
        Map<String, String> result = new HashMap<>();
        if (content == null || content.isEmpty()) {
            return result;
        }

        // Regex bắt UserID và CoachID với 32 ký tự hex
        Pattern pattern = Pattern.compile("UserID([a-fA-F0-9]{32})CoachID([a-fA-F0-9]{32})");
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            String rawUserId = matcher.group(1);
            String rawCoachId = matcher.group(2);

            result.put("userId", formatUUID(rawUserId));
            result.put("coachId", formatUUID(rawCoachId));
        }
        return result;
    }

    private  String formatUUID(String raw) {
        return raw.substring(0, 8) + "-" +
                raw.substring(8, 12) + "-" +
                raw.substring(12, 16) + "-" +
                raw.substring(16, 20) + "-" +
                raw.substring(20);
    }
}
