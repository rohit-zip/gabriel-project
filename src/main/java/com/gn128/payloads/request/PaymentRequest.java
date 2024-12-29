package com.gn128.payloads.request;

import com.gn128.enums.PackageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    private String userId;
    private int amount;
    private int duration;
    private PackageType packageType;
}
