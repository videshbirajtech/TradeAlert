package com.vdmcreation.TradeAlert.service.impl;

import com.vdmcreation.TradeAlert.dto.PriceAlertDTO;
import com.vdmcreation.TradeAlert.dto.PriceAlertRequestDTO;
import com.vdmcreation.TradeAlert.entity.Coin;
import com.vdmcreation.TradeAlert.entity.PriceAlert;
import com.vdmcreation.TradeAlert.entity.User;
import com.vdmcreation.TradeAlert.repository.CoinRepository;
import com.vdmcreation.TradeAlert.repository.PriceAlertRepository;
import com.vdmcreation.TradeAlert.repository.UserRepository;
import com.vdmcreation.TradeAlert.service.PriceAlertService;
import com.vdmcreation.TradeAlert.service.UserAlertStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PriceAlertServiceImpl implements PriceAlertService {

    private final PriceAlertRepository alertRepository;
    private final UserRepository userRepository;
    private final CoinRepository coinRepository;
    
    @Autowired
    private UserAlertStatsService userAlertStatsService;

    public PriceAlertServiceImpl(PriceAlertRepository alertRepository,
                                  UserRepository userRepository,
                                  CoinRepository coinRepository) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.coinRepository = coinRepository;
    }

    @Override
    public PriceAlertDTO createAlert(PriceAlertRequestDTO request) {
        User user = getUser(request.getUserEmail());
        
        // Check if user can create more alerts
        if (!userAlertStatsService.canCreateAlert(user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Alert limit reached. Please upgrade your subscription to create more alerts.");
        }
        
        Coin coin = getCoin(request.getCoinId());

        PriceAlert alert = new PriceAlert();
        alert.setUser(user);
        alert.setCoin(coin);
        alert.setAlertPrice(request.getAlertPrice());

        PriceAlert savedAlert = alertRepository.save(alert);
        
        // Update alert count
        userAlertStatsService.incrementAlertCount(user.getId());

        return toDTO(savedAlert);
    }

    @Override
    public List<PriceAlertDTO> getAlertsByEmail(String email) {
        User user = getUser(email);
        return alertRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PriceAlertDTO updateAlert(Long id, PriceAlertRequestDTO request) {
        PriceAlert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Alert not found with id: " + id));

        Coin coin = getCoin(request.getCoinId());
        alert.setCoin(coin);
        alert.setAlertPrice(request.getAlertPrice());

        return toDTO(alertRepository.save(alert));
    }

    @Override
    public void deleteAlert(Long id) {
        PriceAlert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Alert not found with id: " + id));
        
        Long userId = alert.getUser().getId();
        alertRepository.deleteById(id);
        
        // Update alert count
        userAlertStatsService.decrementAlertCount(userId);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: " + email));
    }

    private Coin getCoin(Long coinId) {
        return coinRepository.findById(coinId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Coin not found with id: " + coinId));
    }

    private PriceAlertDTO toDTO(PriceAlert alert) {
        PriceAlertDTO dto = new PriceAlertDTO();
        dto.setId(alert.getId());
        dto.setCoinId(alert.getCoin().getId());
        dto.setCoinName(alert.getCoin().getCoinName());
        dto.setSymbol(alert.getCoin().getSymbol());
        dto.setAlertPrice(alert.getAlertPrice());
        dto.setCreatedAt(alert.getCreatedAt());
        dto.setUpdatedAt(alert.getUpdatedAt());
        return dto;
    }
}