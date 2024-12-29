package com.gn128.payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlutterWaveTransactionResponsePayload {

    private String status;
    private String message;
    private PaymentData data;

    @Getter
    @Setter
    public static class PaymentData {
        private String id;
        private Integer amount;
        private String currency;
        private String ip;
        private String payment_type;
        private String app_fee;
        private Double amount_settled;
        private Customer customer;
    }

    @Getter
    @Setter
    public static class Customer {
        private String id;
        private String name;
        private String phone_number;
        private String email;
    }
}
