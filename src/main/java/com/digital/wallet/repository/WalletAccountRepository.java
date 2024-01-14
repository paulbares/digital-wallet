package com.digital.wallet.repository;

import com.digital.wallet.domain.WalletAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link WalletAccount} entity.
 */
@Repository
public interface WalletAccountRepository extends JpaRepository<WalletAccount, Long> {}
