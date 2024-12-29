package com.gn128.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "payment")
@Configuration
public class PaymentProperties {

    private Boolean enabled;
    private Map<String, ApplicationPackage> applicationPackage;

    @Getter
    @Setter
    public static class ApplicationPackage {
        private int week;
        private int month;
        private int year;
    }
}
