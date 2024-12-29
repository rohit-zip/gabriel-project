package com.gn128.service.implementation;

import com.gn128.authentication.UserPrincipal;
import com.gn128.constants.EnvironmentConstants;
import com.gn128.constants.ExceptionMessages;
import com.gn128.dao.repository.PackageInfoRepository;
import com.gn128.dao.repository.PaymentRepository;
import com.gn128.dao.repository.RoleRepository;
import com.gn128.dao.repository.UserAuthRepository;
import com.gn128.entity.PackageInfo;
import com.gn128.entity.Payment;
import com.gn128.entity.Role;
import com.gn128.entity.UserAuth;
import com.gn128.entity.embeddable.PaymentUser;
import com.gn128.enums.PackageStatus;
import com.gn128.enums.PackageValidity;
import com.gn128.enums.SchedulerStatus;
import com.gn128.exception.payloads.BadRequestException;
import com.gn128.exception.payloads.FlutterWaveFeignException;
import com.gn128.feign.FlutterWaveRestIntegration;
import com.gn128.payloads.FlutterWaveTransactionResponsePayload;
import com.gn128.payloads.request.PaymentRequest;
import com.gn128.payloads.response.ModuleResponse;
import com.gn128.properties.PaymentProperties;
import com.gn128.service.PaymentService;
import com.gn128.utils.IpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImplementation implements PaymentService {

    private final FlutterWaveRestIntegration flutterWaveRestIntegration;
    private final Environment environment;
    private final PaymentProperties paymentProperties;
    private final PaymentRepository paymentRepository;
    private final PackageInfoRepository packageInfoRepository;
    private final UserAuthRepository userAuthRepository;
    private final RoleRepository roleRepository;

    @Override
    public ModuleResponse initiatePayment(String transactionId, PaymentRequest paymentRequest, UserPrincipal userPrincipal, HttpServletRequest httpServletRequest) {
        long startTime = System.currentTimeMillis();
        ResponseEntity<FlutterWaveTransactionResponsePayload> transactionResponse = flutterWaveRestIntegration.checkTransaction(transactionId, environment.getProperty(EnvironmentConstants.FLUTTER_WAVE_TOKEN));
        FlutterWaveTransactionResponsePayload response = transactionResponse.getBody();
        if (!transactionResponse.getStatusCode().is2xxSuccessful()) {
            assert response != null;
            String message = StringUtils.hasText(response.getMessage()) ? response.getMessage() : ExceptionMessages.FEIGN_FLUTTER_WAVE_TRANSACTION_ERROR;
            throw new FlutterWaveFeignException(message, HttpStatus.NOT_ACCEPTABLE);
        }
        int clientAmount = paymentRequest.getAmount();
        assert Objects.nonNull(response);
        FlutterWaveTransactionResponsePayload.PaymentData data = response.getData();
        if (clientAmount != data.getAmount()) {
            throw new BadRequestException("Payment not matched with Flutter wave", HttpStatus.BAD_REQUEST);
        }
        int duration = paymentRequest.getDuration();
        PackageValidity packageValidity;
        if (duration == 7) {
            packageValidity = PackageValidity.WEEK;
        } else if (duration == 30) {
            packageValidity = PackageValidity.MONTH;
        } else if (duration == 365) {
            packageValidity = PackageValidity.YEAR;
        } else {
            throw new BadRequestException("Duration must be one of 7, 30 or 365", HttpStatus.BAD_REQUEST);
        }
        int settlementAmount;
        Map<String, PaymentProperties.ApplicationPackage> applicationPackage = paymentProperties.getApplicationPackage();
        for (String packageKey : applicationPackage.keySet()) {
            PaymentProperties.ApplicationPackage appPackage = applicationPackage.get(packageKey);
            if (packageKey.equalsIgnoreCase(paymentRequest.getPackageType().name())) {
                if (packageValidity == PackageValidity.WEEK) {
                    settlementAmount = appPackage.getWeek();
                } else if (packageValidity == PackageValidity.MONTH) {
                    settlementAmount = appPackage.getMonth();
                } else {
                    settlementAmount = appPackage.getYear();
                }
                if (settlementAmount != clientAmount) {
                    throw new BadRequestException("The Package you opted costs " + settlementAmount, HttpStatus.BAD_REQUEST);
                }
            }
        }
        FlutterWaveTransactionResponsePayload.Customer customer = data.getCustomer();
        PaymentUser paymentUser = PaymentUser
                .builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone_number(customer.getPhone_number())
                .email(customer.getEmail())
                .build();
        Payment payment = Payment
                .builder()
                .transactionId(transactionId)
                .userId(userPrincipal.getUserId())
                .status(response.getStatus())
                .packageType(paymentRequest.getPackageType())
                .duration(duration)
                .dateCreated(new Date())
                .currency(data.getCurrency())
                .flutterWaveIp(data.getIp())
                .remoteAddress(IpUtils.getRemoteAddress(httpServletRequest))
                .amountSettled(data.getAmount_settled())
                .message(response.getMessage())
                .paymentUser(paymentUser)
                .build();
        paymentRepository.save(payment);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, duration);
        Date expiryDate = calendar.getTime();
        PackageInfo packageInfo = PackageInfo
                .builder()
                .packageType(paymentRequest.getPackageType())
                .userId(userPrincipal.getUserId())
                .packageStatus(PackageStatus.ACTIVE)
                .dateCreated(new Date())
                .expiryDate(expiryDate)
                .schedulerStatus(SchedulerStatus.SCHEDULED)
                .build();
        packageInfoRepository.save(packageInfo);
        UserAuth userAuth = userAuthRepository.findById(userPrincipal.getUserId())
                .orElseThrow(() -> new BadRequestException(ExceptionMessages.USER_NOT_FOUND_ID, HttpStatus.INTERNAL_SERVER_ERROR));
        List<Role> roles = userAuth.getRoles();
        Role role = roleRepository.findById(paymentRequest.getPackageType().name().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Role not found", HttpStatus.INTERNAL_SERVER_ERROR));
        roles.remove(role);
        roles.add(role);
        userAuth.setRoles(roles);
        userAuthRepository.save(userAuth);
        log.info("Execution Time (Initiate Payment) : {}ms", System.currentTimeMillis() - startTime);
        return ModuleResponse.builder().message("Payment Initiated").id(transactionId).build();
    }
}
