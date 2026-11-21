package com.boxdrawer.diagram.repository;

import com.boxdrawer.diagram.entity.ShareToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShareTokenRepository extends JpaRepository<ShareToken, String> {

    // Atomic: only one caller can flip used_at, so a token works once
    @Modifying
    @Query("update ShareToken t set t.usedAt = CURRENT_TIMESTAMP where t.token = :token and t.usedAt is null")
    int markUsed(@Param("token") String token);
}
