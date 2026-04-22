package com.vdmcreation.TradeAlert.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "coins")
public class Coin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coin_name", nullable = false, unique = true)
    private String coinName;

    @Column(name = "symbol", nullable = false, unique = true)
    private String symbol;

    public Coin() {}

    public Coin(String coinName, String symbol) {
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
