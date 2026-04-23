package com.vdmcreation.TradeAlert.repository;

import com.vdmcreation.TradeAlert.entity.User;
import com.vdmcreation.TradeAlert.entity.UserRoles;
import com.vdmcreation.TradeAlert.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {

    List<UserRoles> findByUser(User user);

    List<UserRoles> findByUserId(Long userId);

    @Query("SELECT ur FROM UserRoles ur WHERE ur.user.email = :email")
    List<UserRoles> findByUserEmail(@Param("email") String email);

    @Query("SELECT ur FROM UserRoles ur WHERE ur.user.id = :userId AND ur.role = :role")
    Optional<UserRoles> findByUserIdAndRole(@Param("userId") Long userId, @Param("role") UserRole role);

    @Query("SELECT ur FROM UserRoles ur WHERE ur.user.email = :email AND ur.role = :role")
    Optional<UserRoles> findByUserEmailAndRole(@Param("email") String email, @Param("role") UserRole role);

    boolean existsByUserIdAndRole(Long userId, UserRole role);

    boolean existsByUserEmailAndRole(String email, UserRole role);
}