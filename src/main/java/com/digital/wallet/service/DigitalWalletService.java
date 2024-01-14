package com.digital.wallet.service;

import com.digital.wallet.domain.WalletTransaction;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DigitalWalletService {
    BigDecimal MAXIMUM_DEPOSIT = BigDecimal.valueOf(10_000);
    BigDecimal MAXIMUM_WITHDRAWAL = BigDecimal.valueOf(5_000);
    BigDecimal MINIMUM_DEPOSIT = BigDecimal.valueOf(10);

    /**
     * Executes a deposit for a customer in the digital wallet.
     *
     * @param customerId the unique identifier of the customer
     * @param currency   the currency of the deposit. As per ISO 4217.
     *                   <a href="https://en.wikipedia.org/wiki/ISO_4217">https://en.wikipedia.org/wiki/ISO_4217</a>
     * @param amount     the amount to be deposited
     *                   // Throwing exception if > 10 000  or < 10
     */
    void executeDeposit(Long customerId, String currency, BigDecimal amount, String remark);

    /**
     * Max £5,000
     */
    void executeWithdrawal(Long customerId, String currency, BigDecimal amount, String remark);

    Page<WalletTransaction> getTransactions(Long customerId, Pageable pageable);
}
