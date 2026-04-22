package com.vdmcreation.TradeAlert.enums;

public enum CoinEnum {
    BTC("BTCUSDT"),
    ETH("ETHUSDT");

    private final String displayName;
    CoinEnum(String displayName)
    {
        this.displayName=displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
