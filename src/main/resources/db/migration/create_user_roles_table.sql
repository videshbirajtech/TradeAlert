-- Create user_roles table
CREATE TABLE user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(255),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_roles_user_id (user_id),
    INDEX idx_user_roles_role (role),
    UNIQUE KEY unique_user_role (user_id, role)
);

-- Initialize default USER role for all existing users
INSERT INTO user_roles (user_id, role, assigned_by)
SELECT id, 'USER', 'SYSTEM'
FROM users
WHERE id NOT IN (SELECT DISTINCT user_id FROM user_roles);

-- Create a super admin user (optional - you can create this manually)
-- UPDATE: You should manually assign SUPER_USER role to specific users
-- Example: INSERT INTO user_roles (user_id, role, assigned_by) VALUES (1, 'SUPER_USER', 'SYSTEM');