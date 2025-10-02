package com.devteria.identity_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    String description;
}
