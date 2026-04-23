-- Create user_alert_stats table
CREATE TABLE user_alert_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    subscription_id VARCHAR(255),
    is_subscription_active BOOLEAN NOT NULL DEFAULT FALSE,
    total_alert_count INT NOT NULL DEFAULT 0,
    remaining_alert_count INT NOT NULL DEFAULT 0,
    max_alerts_allowed INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_alert_stats_user_id (user_id),
    INDEX idx_user_alert_stats_subscription_id (subscription_id)
);

-- Initialize user_alert_stats for existing users
INSERT INTO user_alert_stats (user_id, subscription_id, is_subscription_active, total_alert_count, remaining_alert_count, max_alerts_allowed)
SELECT 
    u.id as user_id,
    u.subscription_id,
    CASE 
        WHEN s.id IS NOT NULL AND s.end_date > NOW() THEN TRUE 
        ELSE FALSE 
    END as is_subscription_active,
    COALESCE(alert_counts.alert_count, 0) as total_alert_count,
    CASE 
        WHEN s.id IS NOT NULL AND s.end_date > NOW() THEN 
            GREATEST(0, 
                CASE s.plan_type
                    WHEN 'MONTHLY' THEN 10
                    WHEN 'QUARTERLY' THEN 30
                    WHEN 'YEARLY' THEN 100
                    ELSE 0
                END - COALESCE(alert_counts.alert_count, 0)
            )
        ELSE 0 
    END as remaining_alert_count,
    CASE 
        WHEN s.id IS NOT NULL AND s.end_date > NOW() THEN 
            CASE s.plan_type
                WHEN 'MONTHLY' THEN 10
                WHEN 'QUARTERLY' THEN 30
                WHEN 'YEARLY' THEN 100
                ELSE 0
            END
        ELSE 0 
    END as max_alerts_allowed
FROM users u
LEFT JOIN subscriptions s ON u.subscription_id = s.subscription_id AND s.end_date > NOW()
LEFT JOIN (
    SELECT user_id, COUNT(*) as alert_count 
    FROM price_alerts 
    GROUP BY user_id
) alert_counts ON u.id = alert_counts.user_id;