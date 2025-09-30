package com.devteria.identity_service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SepayPaymentRequest {
    String accountNumber;   // số tài khoản ngân hàng đã đăng ký với Sepay
    Long amount;
    String description;
}
