package com.vdmcreation.TradeAlert.config;

import com.vdmcreation.TradeAlert.entity.Coin;
import com.vdmcreation.TradeAlert.repository.CoinRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initCoins(CoinRepository coinRepository) {
        return args -> {
            if (coinRepository.count() == 0) {
                coinRepository.saveAll(List.of(
                        new Coin("Bitcoin",  "BTCUSDT"),
                        new Coin("Ethereum", "ETHUSDT")
                ));
                System.out.println("Coins seeded successfully.");
            }
        };
    }
}
