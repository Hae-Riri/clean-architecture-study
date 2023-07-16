package com.example.cleanarchitecturestudy;

import com.example.cleanarchitecturestudy.account.application.service.MoneyTransferProperties;
import com.example.cleanarchitecturestudy.account.domain.Money;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CleanArchitectureConfigurationProperties.class)
public class CleanArchitectureConfiguration {

    /**
     * Adds a use-case-specific {@link MoneyTransferProperties} object to the application context.
     * The properties are read from the Spring-Boot-specific {@link CleanArchitectureConfigurationProperties} object.
     */
    @Bean
    public MoneyTransferProperties moneyTransferProperties(CleanArchitectureConfigurationProperties properties) {
        return new MoneyTransferProperties(Money.of(properties.getTransferThreshold()));
    }
}
