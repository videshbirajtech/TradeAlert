-- Script to manually assign SUPER_USER role to a user
-- Replace 'user@example.com' with the actual email of the user you want to promote

-- First, find the user ID
SELECT id, email, first_name, last_name FROM users WHERE email = 'user@example.com';

-- Insert SUPER_USER role for the user (replace USER_ID with the actual ID from above query)
-- Note: This assumes the user already has USER role from signup
INSERT INTO user_roles (user_id, role, assigned_by, created_at) 
VALUES (
    (SELECT id FROM users WHERE email = 'user@example.com'), 
    'SUPER_USER', 
    'MANUAL_DB_UPDATE', 
    NOW()
);

-- Verify the user now has both roles
SELECT u.email, u.first_name, u.last_name, ur.role 
FROM users u 
JOIN user_roles ur ON u.id = ur.user_id 
WHERE u.email = 'user@example.com'
ORDER BY ur.role;