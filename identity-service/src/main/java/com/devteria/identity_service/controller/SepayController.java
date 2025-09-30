package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.ApiResponse;
import com.devteria.identity_service.dto.SepayCallbackRequest;
import com.devteria.identity_service.dto.SepayPaymentRequest;
import com.devteria.identity_service.service.SepayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sepay")
public class SepayController {
    @Autowired
  private SepayService sepayService;
    @Value("${sepay.api.key}")
    private String sepayKey;
    @PostMapping("/qr")
    public ResponseEntity<Map<String, String>> getQrCode(
            @RequestBody SepayPaymentRequest request) {

        String qrUrl = sepayService.generateQrCodeUrl(request);

        Map<String, String> result = new HashMap<>();
        result.put("qrUrl", qrUrl);

        return ResponseEntity.ok(result);
    }
    @PostMapping("/callback")
    public ApiResponse<String> callback( @RequestBody SepayCallbackRequest request) {
        System.out.println("Sepay callback raw: " + request);
        System.out.println(request);
        // Xử lý logic, ví dụ cập nhật đơn hàng
        if (request.getTransferAmount() != null && request.getTransferAmount() > 0) {
            System.out.println("Thanh toán thành công, content: " + request.getContent()
                    + " | amount: " + request.getTransferAmount());
        }
        return ApiResponse.<String>builder()
                .code(1000)
                .message("success")
                .build();
    }
}
