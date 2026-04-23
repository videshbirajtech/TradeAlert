package com.vdmcreation.TradeAlert.service;

import com.vdmcreation.TradeAlert.entity.User;
import com.vdmcreation.TradeAlert.entity.UserAlertStats;
import com.vdmcreation.TradeAlert.repository.UserAlertStatsRepository;
import com.vdmcreation.TradeAlert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAlertStatsRepository userAlertStatsRepository;

    @Autowired
    private UserAlertStatsService userAlertStatsService;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public void run(String... args) throws Exception {
        initializeUserAlertStats();
        initializeUserRoles();
    }

    private void initializeUserAlertStats() {
        try {
            List<User> allUsers = userRepository.findAll();
            System.out.println("Initializing alert stats for " + allUsers.size() + " users...");

            for (User user : allUsers) {
                if (!userAlertStatsRepository.existsByUserId(user.getId())) {
                    System.out.println("Creating alert stats for user: " + user.getEmail());
                    userAlertStatsService.createOrUpdateUserAlertStats(user.getId());
                }
            }

            System.out.println("User alert stats initialization completed.");
        } catch (Exception e) {
            System.err.println("Error during user alert stats initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeUserRoles() {
        try {
            System.out.println("Initializing default user roles...");
            userRoleService.initializeDefaultRoles();
            System.out.println("User roles initialization completed.");
        } catch (Exception e) {
            System.err.println("Error during user roles initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
}