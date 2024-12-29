package com.gn128.service;

import com.gn128.authentication.UserPrincipal;
import com.gn128.payloads.request.PaymentRequest;
import com.gn128.payloads.response.ModuleResponse;

import javax.servlet.http.HttpServletRequest;

public interface PaymentService {

    ModuleResponse initiatePayment(
            String transactionId,
            PaymentRequest paymentRequest,
            UserPrincipal userPrincipal,
            HttpServletRequest httpServletRequest
    );
}
