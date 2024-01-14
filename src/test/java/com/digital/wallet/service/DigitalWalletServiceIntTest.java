package com.digital.wallet.service;

import static com.digital.wallet.config.Constants.*;

import com.digital.wallet.IntegrationTest;
import com.digital.wallet.domain.TransactionType;
import com.digital.wallet.domain.WalletAccount;
import com.digital.wallet.domain.WalletTransaction;
import com.digital.wallet.repository.WalletAccountRepository;
import com.digital.wallet.repository.WalletTransactionRepository;
import java.math.BigDecimal;
import java.util.Iterator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link DigitalWalletService}.
 */
@IntegrationTest
@Transactional
class DigitalWalletServiceIntTest {

    private static final String currency = "GBP";

    @Autowired
    private WalletAccountRepository accountRepository;

    @Autowired
    private WalletTransactionRepository transactionRepository;

    @Autowired
    @Qualifier("SafeDigitalWalletService")
    private DigitalWalletService digitalWalletService;

    // Two customer ids...
    long paulId;
    long peterId;

    @BeforeEach
    void setupRepositories() {
        // Create the accounts with a balance of 0
        WalletAccount paulAccount = new WalletAccount();
        paulAccount.setCurrencyCode(currency);
        paulAccount.setAmount(new BigDecimal(0));
        paulId = accountRepository.save(paulAccount).getId();

        WalletAccount peterAccount = new WalletAccount();
        peterAccount.setCurrencyCode(currency);
        peterAccount.setAmount(new BigDecimal(0));
        peterId = accountRepository.save(peterAccount).getId();
    }

    @Test
    void testDepositWithdrawal() {
        BigDecimal amount = new BigDecimal(10);
        this.digitalWalletService.executeDeposit(paulId, currency, amount, "my first deposit");

        checkWalletAccount(paulId, amount);
        // should not affect peter's account
        checkWalletAccount(peterId, new BigDecimal(0));

        Page<WalletTransaction> transactions = this.digitalWalletService.getTransactions(paulId, Pageable.unpaged());
        Assertions.assertThat(transactions.getSize()).isEqualTo(1);
        Iterator<WalletTransaction> iterator = transactions.iterator();
        checkWalletTransaction(iterator.next(), paulId, amount, TransactionType.CREDIT);

        BigDecimal withdrawalAmount = new BigDecimal(5);
        this.digitalWalletService.executeWithdrawal(paulId, currency, withdrawalAmount, "my first deposit");
        BigDecimal newAmount = amount.subtract(withdrawalAmount);
        checkWalletAccount(paulId, newAmount);

        transactions = this.digitalWalletService.getTransactions(paulId, Pageable.unpaged());
        Assertions.assertThat(transactions.getSize()).isEqualTo(2);
        checkWalletTransaction((iterator = transactions.iterator()).next(), paulId, amount, TransactionType.CREDIT);
        checkWalletTransaction(iterator.next(), paulId, withdrawalAmount, TransactionType.DEBIT);

        // Change customer
        BigDecimal fortyTwo = new BigDecimal(42);
        this.digitalWalletService.executeDeposit(peterId, currency, fortyTwo, "hello");
        checkWalletAccount(paulId, newAmount);
        checkWalletAccount(peterId, fortyTwo);
        transactions = this.digitalWalletService.getTransactions(peterId, Pageable.unpaged());
        Assertions.assertThat(transactions.getSize()).isEqualTo(1);
        checkWalletTransaction(transactions.iterator().next(), peterId, fortyTwo, TransactionType.CREDIT);
    }

    @Test
    void testDepositBounds() {
        Assertions
            .assertThatThrownBy(() ->
                this.digitalWalletService.executeDeposit(paulId, currency, MINIMUM_DEPOSIT.subtract(new BigDecimal(1)), "my first deposit")
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Minimum deposit allowed is 10");
        checkWalletAccount(paulId, new BigDecimal(0));
        Page<WalletTransaction> transactions = this.digitalWalletService.getTransactions(paulId, Pageable.unpaged());
        Assertions.assertThat(transactions.getSize()).isEqualTo(0);

        Assertions
            .assertThatThrownBy(() ->
                this.digitalWalletService.executeDeposit(paulId, currency, MAXIMUM_DEPOSIT.add(new BigDecimal(1)), "my first deposit")
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Maximum deposit allowed is 10000");
        checkWalletAccount(paulId, new BigDecimal(0));
        transactions = this.digitalWalletService.getTransactions(paulId, Pageable.unpaged());
        Assertions.assertThat(transactions.getSize()).isEqualTo(0);
    }

    @Test
    void testWithdrawalBounds() {
        // Topup account first
        BigDecimal amount = new BigDecimal(10_000);
        this.digitalWalletService.executeDeposit(paulId, currency, amount, "my first deposit");

        Assertions
            .assertThatThrownBy(() ->
                this.digitalWalletService.executeWithdrawal(paulId, currency, MAXIMUM_WITHDRAWAL.add(new BigDecimal(1)), "")
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Maximum withdrawal allowed is 5000");
        checkWalletAccount(paulId, new BigDecimal(10_000));
        Page<WalletTransaction> transactions = this.digitalWalletService.getTransactions(paulId, Pageable.unpaged());
        Assertions.assertThat(transactions.getSize()).isEqualTo(1);

        Assertions
            .assertThatThrownBy(() -> this.digitalWalletService.executeWithdrawal(paulId, currency, new BigDecimal(-1), ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Withdrawal amount cannot be negative");
        checkWalletAccount(paulId, new BigDecimal(10_000));
        transactions = this.digitalWalletService.getTransactions(paulId, Pageable.unpaged());
        Assertions.assertThat(transactions.getSize()).isEqualTo(1);
    }

    @Test
    void testWithdrawalWithInsufficientBalance() {
        BigDecimal amount = new BigDecimal(100);
        this.digitalWalletService.executeDeposit(paulId, currency, amount, "");

        checkWalletAccount(paulId, amount);
        Page<WalletTransaction> transactions = this.digitalWalletService.getTransactions(paulId, Pageable.unpaged());
        Assertions.assertThat(transactions.getSize()).isEqualTo(1);

        BigDecimal withdrawalAmount = amount.add(new BigDecimal(1));
        Assertions
            .assertThatThrownBy(() -> this.digitalWalletService.executeWithdrawal(paulId, currency, withdrawalAmount, ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Insufficient balance. You are trying to withdraw 101 but the balance is 100");
        // Account value should be the same
        checkWalletAccount(paulId, amount);
        Assertions.assertThat(transactions.getSize()).isEqualTo(1);
    }

    @Test
    void testTransactionPagination() {
        // Create several trxs and try to fetch them
        int nbOfTrx = 100;
        for (int i = 0; i < nbOfTrx; i++) {
            BigDecimal amount = MINIMUM_DEPOSIT.add(new BigDecimal(i));
            this.digitalWalletService.executeDeposit(paulId, currency, amount, "");
        }
        // Fetch all trxs
        Page<WalletTransaction> unpagedTrxs = this.digitalWalletService.getTransactions(paulId, Pageable.unpaged());
        Assertions.assertThat(unpagedTrxs.getSize()).isEqualTo(nbOfTrx);

        int pageSize = 10;
        int i = 0;
        for (int page = 0; page < pageSize; page++) {
            Page<WalletTransaction> paginatedTrxs = this.digitalWalletService.getTransactions(paulId, PageRequest.of(page, pageSize));
            Assertions.assertThat(paginatedTrxs.getSize()).isEqualTo(pageSize);
            for (WalletTransaction paginatedTrx : paginatedTrxs) {
                checkWalletTransaction(paginatedTrx, paulId, MINIMUM_DEPOSIT.add(new BigDecimal(i)), TransactionType.CREDIT);
                i++;
            }
        }
    }

    private void checkWalletAccount(Long customerId, BigDecimal amount) {
        WalletAccount walletAccount = this.accountRepository.findById(customerId).orElseThrow();
        Assertions.assertThat(walletAccount.getId()).isEqualTo(customerId);
        Assertions.assertThat(walletAccount.getAmount()).isEqualTo(amount);
        Assertions.assertThat(walletAccount.getCurrencyCode()).isEqualTo(currency);
    }

    private void checkWalletTransaction(WalletTransaction trx, Long customerId, BigDecimal amount, TransactionType transactionType) {
        Assertions.assertThat(trx.getCustomerId()).isEqualTo(customerId);
        Assertions.assertThat(trx.getAmount()).isEqualTo(amount);
        Assertions.assertThat(trx.getCurrencyCode()).isEqualTo(currency);
        Assertions.assertThat(trx.getTransactionType()).isEqualTo(transactionType);
    }
}
