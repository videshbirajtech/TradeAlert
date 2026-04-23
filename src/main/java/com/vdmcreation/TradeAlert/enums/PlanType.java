package com.vdmcreation.TradeAlert.enums;

public enum PlanType {

    MONTHLY(10.0, 1, 10),
    QUARTERLY(27.0, 3, 30),
    YEARLY(100.0, 12, 100);

    private final double price;
    private final int months;
    private final int maxAlerts;

    PlanType(double price, int months, int maxAlerts) {
        this.price = price;
        this.months = months;
        this.maxAlerts = maxAlerts;
    }

    public double getPrice() { return price; }
    public int getMonths() { return months; }
    public int getMaxAlerts() { return maxAlerts; }
}
