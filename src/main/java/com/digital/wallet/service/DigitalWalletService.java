package com.digital.wallet.service;

import com.digital.wallet.domain.WalletTransaction;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DigitalWalletService {
    /**
     * Executes a deposit for a customer in the digital wallet. The amount should be between
     * {@link com.digital.wallet.config.Constants#MINIMUM_DEPOSIT} and
     * {@link com.digital.wallet.config.Constants#MAXIMUM_DEPOSIT}.
     *
     * @param customerId the unique identifier of the customer
     * @param currency   the currency of the deposit. As per ISO 4217.
     *                   <a href="https://en.wikipedia.org/wiki/ISO_4217">https://en.wikipedia.org/wiki/ISO_4217</a>
     * @param amount     the amount to be deposited
     * @param remark     a remark or description for the deposit
     */
    void executeDeposit(Long customerId, String currency, BigDecimal amount, String remark);

    /**
     * Executes a withdrawal for a customer in the digital wallet. The amount should be between 0
     * and {@link com.digital.wallet.config.Constants#MAXIMUM_WITHDRAWAL}.
     *
     * @param customerId the unique identifier of the customer
     * @param currency   the currency of the withdrawal. As per ISO 4217.
     *                   <a href="https://en.wikipedia.org/wiki/ISO_4217">https://en.wikipedia.org/wiki/ISO_4217</a>
     * @param amount     the amount to be withdrawn
     * @param remark     a remark or description for the withdrawal
     */
    void executeWithdrawal(Long customerId, String currency, BigDecimal amount, String remark);

    /**
     * Retrieves the transactions for a customer.
     *
     * @param customerId the unique identifier of the customer
     * @param pageable   the pagination information
     * @return a Page object containing the transactions
     */
    Page<WalletTransaction> getTransactions(Long customerId, Pageable pageable);
}
