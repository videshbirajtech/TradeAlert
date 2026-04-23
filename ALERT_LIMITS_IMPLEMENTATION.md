# Alert Limits Implementation

This document describes the implementation of subscription-based alert limits for the Trade Alert application.

## Overview

The system now enforces alert limits based on user subscription plans:
- **Monthly Plan**: 10 alerts maximum
- **Quarterly Plan**: 30 alerts maximum  
- **Yearly Plan**: 100 alerts maximum

## Backend Implementation

### 1. Database Changes

#### New Table: `user_alert_stats`
```sql
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
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

#### Updated PlanType Enum
Added `maxAlerts` field to define alert limits per plan:
- MONTHLY: 10 alerts
- QUARTERLY: 30 alerts  
- YEARLY: 100 alerts

### 2. New Components

#### Entities
- `UserAlertStats.java` - Tracks user alert statistics and limits

#### Repositories
- `UserAlertStatsRepository.java` - Data access for alert statistics
- Updated `PriceAlertRepository.java` - Added `countByUserId()` method
- Updated `SubscriptionRepository.java` - Added `findByUserIdAndEndDateAfter()` method

#### Services
- `UserAlertStatsService.java` - Interface for alert statistics management
- `UserAlertStatsServiceImpl.java` - Implementation with validation logic

#### Controllers
- `UserAlertStatsController.java` - REST endpoints for alert statistics
- Updated `PriceAlertController.java` - Added dashboard endpoint with stats

#### DTOs
- `UserAlertStatsDTO.java` - Data transfer object for alert statistics
- `AlertDashboardDTO.java` - Combined alerts and statistics response

### 3. Key Features

#### Alert Creation Validation
- Checks if user has active subscription
- Validates remaining alert count before creation
- Returns appropriate error messages when limits are reached

#### Automatic Statistics Updates
- Increments count when alerts are created
- Decrements count when alerts are deleted
- Refreshes subscription status and limits

#### Dashboard Integration
- Combined endpoint `/api/alerts/dashboard` returns both alerts and statistics
- Real-time remaining alert count display

## API Endpoints

### Alert Management
```
POST   /api/alerts                    - Create new alert (with validation)
GET    /api/alerts?email={email}      - Get user alerts
GET    /api/alerts/dashboard?email={email} - Get alerts + statistics
PUT    /api/alerts/{id}               - Update alert
DELETE /api/alerts/{id}               - Delete alert (updates count)
```

### Alert Statistics
```
GET    /api/user-alert-stats/user/{userId}           - Get stats by user ID
GET    /api/user-alert-stats/email/{email}          - Get stats by email
POST   /api/user-alert-stats/refresh/{userId}       - Refresh user stats
GET    /api/user-alert-stats/can-create-alert/{userId} - Check creation permission
```

## Frontend Implementation

### Updated Services
- Enhanced `PriceService` with alert management methods
- Added TypeScript interfaces for type safety

### New Components
- `AlertDashboardComponent` - Complete dashboard with statistics display
- Responsive design with progress bars and status indicators

### Key Features
- Real-time alert limit display
- Visual progress bar showing usage
- Subscription status indicators
- Form validation based on remaining alerts
- Error handling for limit exceeded scenarios

## Usage Examples

### Backend - Check if user can create alert
```java
@Autowired
private UserAlertStatsService userAlertStatsService;

public boolean validateAlertCreation(Long userId) {
    return userAlertStatsService.canCreateAlert(userId);
}
```

### Frontend - Display alert statistics
```typescript
this.priceService.getAlertDashboard(userEmail).subscribe(response => {
  if (response.success) {
    this.dashboard = response.data;
    // dashboard.alertStats contains all statistics
  }
});
```

## Database Migration

Run the SQL script `src/main/resources/db/migration/create_user_alert_stats_table.sql` to:
1. Create the new `user_alert_stats` table
2. Initialize statistics for existing users
3. Set up proper foreign key relationships

## Error Handling

The system provides clear error messages:
- "Alert limit reached. Please upgrade your subscription to create more alerts."
- "You need an active subscription to create alerts."
- "You have reached your alert limit (X alerts)."

## Security Considerations

- All endpoints validate user ownership of alerts
- Subscription status is verified server-side
- Alert counts are maintained atomically to prevent race conditions

## Testing

Test scenarios to verify:
1. Alert creation blocked when limit reached
2. Statistics update correctly on create/delete
3. Subscription changes reflect in alert limits
4. Dashboard displays accurate information
5. Error messages are user-friendly

## Future Enhancements

Potential improvements:
1. Alert archiving instead of hard deletion
2. Temporary alert limit increases
3. Usage analytics and reporting
4. Email notifications for limit warnings
5. Bulk alert operations with validation