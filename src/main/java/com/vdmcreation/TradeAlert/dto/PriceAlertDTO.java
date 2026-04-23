package com.vdmcreation.TradeAlert.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceAlertDTO {

    private Long id;
    private Long coinId;
    private String coinName;
    private String symbol;
    private BigDecimal alertPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PriceAlertDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCoinId() { return coinId; }
    public void setCoinId(Long coinId) { this.coinId = coinId; }

    public String getCoinName() { return coinName; }
    public void setCoinName(String coinName) { this.coinName = coinName; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public BigDecimal getAlertPrice() { return alertPrice; }
    public void setAlertPrice(BigDecimal alertPrice) { this.alertPrice = alertPrice; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
