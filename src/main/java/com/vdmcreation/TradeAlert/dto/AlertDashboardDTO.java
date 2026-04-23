package com.vdmcreation.TradeAlert.dto;

import java.util.List;

public class AlertDashboardDTO {
    
    private List<PriceAlertDTO> alerts;
    private UserAlertStatsDTO alertStats;

    public AlertDashboardDTO() {}

    public AlertDashboardDTO(List<PriceAlertDTO> alerts, UserAlertStatsDTO alertStats) {
        this.alerts = alerts;
        this.alertStats = alertStats;
    }

    public List<PriceAlertDTO> getAlerts() { return alerts; }
    public void setAlerts(List<PriceAlertDTO> alerts) { this.alerts = alerts; }

    public UserAlertStatsDTO getAlertStats() { return alertStats; }
    public void setAlertStats(UserAlertStatsDTO alertStats) { this.alertStats = alertStats; }
}