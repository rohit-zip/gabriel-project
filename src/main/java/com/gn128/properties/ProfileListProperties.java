package com.gn128.properties;

import com.gn128.payloads.ProfileListProvider;
import com.gn128.ymlparser.YmlFileMapParserFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.properties
 * Created_on - December 04 - 2024
 * Created_at - 20:42
 */

@Configuration
@ConfigurationProperties(prefix = "profile")
@PropertySource(value = "classpath:configuration/profile-data.yml", factory = YmlFileMapParserFactory.class)
@Getter
@Setter
public class ProfileListProperties {

    private Map<String, ProfileListProvider> data = new HashMap<>();
}
