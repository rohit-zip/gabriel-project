package com.gn128.feign;

import com.gn128.payloads.FlutterWaveTransactionResponsePayload;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "flutter-wave",
        url = "https://api.flutterwave.com/v3/transactions"
)
@Headers(
        "Authorization: {token}"
)
public interface FlutterWaveRestIntegration {

    @GetMapping("/{transactionId}/verify")
    ResponseEntity<FlutterWaveTransactionResponsePayload> checkTransaction(@PathVariable String transactionId, @RequestHeader("Authorization") String token);
}
