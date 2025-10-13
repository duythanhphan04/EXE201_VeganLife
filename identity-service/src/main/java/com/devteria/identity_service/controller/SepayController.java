package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.ApiResponse;
import com.devteria.identity_service.dto.SepayCallbackRequest;
import com.devteria.identity_service.dto.SepayPaymentRequest;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.UserPlan;
import com.devteria.identity_service.service.SepayService;
import com.devteria.identity_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/sepay")
public class SepayController {
    @Autowired
    private SepayService sepayService;
    @Autowired
    private UserService userService;
    @PostMapping("/qr/userID/{userID}/coachID/{coachID}")
    public ResponseEntity<Map<String, String>> getQrCode(
            @RequestBody SepayPaymentRequest request , @PathVariable String userID, @PathVariable String coachID) {
        request.setDescription("UserID:"+userID+"--"+"CoachID:"+coachID);
        String qrUrl = sepayService.generateQrCodeUrl(request);
        Map<String, String> result = new HashMap<>();
        result.put("qrUrl", qrUrl);

        return ResponseEntity.ok(result);
    }
    @PostMapping("/callback")
    public ApiResponse<String> callback( @RequestBody SepayCallbackRequest request) {
        System.out.println("Sepay callback raw: " + request);
        System.out.println(request);
        if (request.getTransferAmount() != null && request.getTransferAmount() > 0) {
            System.out.println("Thanh toán thành công, content: " + request.getContent()
                    + " | amount: " + request.getTransferAmount());
        }
        String content = request.getContent();
        Map<String, String> parsed = sepayService.parseUserAndCoach(content);
        String userId = parsed.get("userId");
        String coachId = parsed.get("coachId");
        System.out.println("UserID = " + userId);
        System.out.println("CoachID = " + coachId);
        User user = userService.getUserEntityByID(userId);
        User coach = userService.getUserEntityByID(coachId);
        if(user!=null && coach!=null) {
            userService.updateUserPlan(user.getUserID(), UserPlan.PREMIUM);
            userService.updateUserCoach(user.getUserID(), coach.getUserID());
            return ApiResponse.<String>builder()
                    .code(1000)
                    .message("success")
                    .build();
        }else{
            return ApiResponse.<String>builder()
                    .code(500)
                    .message("Error")
                    .build();
        }
    }

    @GetMapping("/status/userID/{userID}/coachID/{coachID}")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPaymentStatus(
            @PathVariable String userID,
            @PathVariable String coachID) {

        try {
            User user = userService.getUserEntityByID(userID);

            if (user == null) {
                return ResponseEntity
                        .status(404)
                        .body(ApiResponse.<Map<String, String>>builder()
                                .message("User not found")
                                .data(Map.of("status", "FAILED"))
                                .build());
            }

            boolean hasCoach = user.getCoach() != null && coachID.equals(user.getCoach().getUserID());
            boolean isPremium = user.getPlan() == UserPlan.PREMIUM;

            String status;
            if (hasCoach && isPremium) {
                status = "SUCCESS";
            } else {
                status = "PENDING";
            }

            Map<String, String> response = Map.of("status", status);

            return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                    .message("Payment status checked")
                    .data(response)
                    .build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    ApiResponse.<Map<String, String>>builder()
                            .message("Error checking payment status")
                            .data(Map.of("status", "FAILED"))
                            .build()
            );
        }
    }
}
