package com.vdmcreation.TradeAlert.dto;

public class CoinDTO {

    private Long id;
    private String coinName;
    private String symbol;

    public CoinDTO() {}

    public CoinDTO(Long id, String coinName, String symbol) {
        this.id = id;
        this.coinName = coinName;
        this.symbol = symbol;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCoinName() { return coinName; }
    public void setCoinName(String coinName) { this.coinName = coinName; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
}
