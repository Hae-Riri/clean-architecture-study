package com.example.cleanarchitecturestudy;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@EqualsAndHashCode
@ConfigurationProperties(prefix = "study")
public class CleanArchitectureConfigurationProperties {
    private long transferThreshold = Long.MAX_VALUE;
}
