package com.vdmcreation.TradeAlert.service.impl;

import com.vdmcreation.TradeAlert.dto.CoinDTO;
import com.vdmcreation.TradeAlert.entity.Coin;
import com.vdmcreation.TradeAlert.repository.CoinRepository;
import com.vdmcreation.TradeAlert.service.CoinService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoinServiceImpl implements CoinService {

    private final CoinRepository coinRepository;

    public CoinServiceImpl(CoinRepository coinRepository) {
        this.coinRepository = coinRepository;
    }

    @Override
    public List<CoinDTO> getAllCoins() {
        return coinRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private CoinDTO toDTO(Coin coin) {
        return new CoinDTO(coin.getId(), coin.getCoinName(), coin.getSymbol());
    }
}
