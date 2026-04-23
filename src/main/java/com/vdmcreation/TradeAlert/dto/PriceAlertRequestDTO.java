package com.vdmcreation.TradeAlert.dto;

import java.math.BigDecimal;

public class PriceAlertRequestDTO {

    private Long coinId;
    private BigDecimal alertPrice;
    private String userEmail;

    public PriceAlertRequestDTO() {}

    public Long getCoinId() { return coinId; }
    public void setCoinId(Long coinId) { this.coinId = coinId; }

    public BigDecimal getAlertPrice() { return alertPrice; }
    public void setAlertPrice(BigDecimal alertPrice) { this.alertPrice = alertPrice; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
