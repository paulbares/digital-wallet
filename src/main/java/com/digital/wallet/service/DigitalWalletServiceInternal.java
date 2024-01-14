package com.digital.wallet.service;

import static com.digital.wallet.config.Constants.*;

import com.digital.wallet.domain.TransactionType;
import com.digital.wallet.domain.WalletTransaction;
import com.digital.wallet.repository.WalletAccountRepository;
import com.digital.wallet.repository.WalletTransactionRepository;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * An internal implementation of {@link DigitalWalletService} that is not meant to be used directly because it does not
 * prevent from concurrent operations on the same account.
 */
@Service
public class DigitalWalletServiceInternal implements DigitalWalletService {

    private final WalletAccountRepository accountRepository;
    private final WalletTransactionRepository transactionRepository;

    public DigitalWalletServiceInternal(WalletAccountRepository accountRepository, WalletTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public void executeDeposit(Long customerId, String currency, BigDecimal amount, String remark) {
        checkDepositAmount(amount);
        this.accountRepository.findById(customerId)
            .ifPresent(account -> {
                if (!currency.equals(account.getCurrencyCode())) {
                    throw new IllegalArgumentException("The user has no account associated to the currency: " + currency);
                }
                account.setAmount(account.getAmount().add(amount));
                this.accountRepository.save(account);
            });
        saveTrx(customerId, currency, amount, remark, TransactionType.CREDIT);
    }

    private void checkDepositAmount(BigDecimal amount) {
        if (amount.compareTo(MAXIMUM_DEPOSIT) > 0) {
            throw new IllegalArgumentException("Maximum deposit allowed is " + MAXIMUM_DEPOSIT);
        } else if (amount.compareTo(MINIMUM_DEPOSIT) < 0) {
            throw new IllegalArgumentException("Minimum deposit allowed is " + MINIMUM_DEPOSIT);
        }
    }

    @Override
    @Transactional
    public void executeWithdrawal(Long customerId, String currency, BigDecimal amount, String remark) {
        this.accountRepository.findById(customerId)
            .ifPresent(account -> {
                if (!currency.equals(account.getCurrencyCode())) {
                    throw new IllegalArgumentException("The user has no account associated to the currency: " + currency);
                }
                checkWithdrawalAmount(amount, account.getAmount());
                account.setAmount(account.getAmount().subtract(amount));
                this.accountRepository.save(account);
            });
        saveTrx(customerId, currency, amount, remark, TransactionType.DEBIT);
    }

    private void checkWithdrawalAmount(BigDecimal amount, BigDecimal accountBalance) {
        if (amount.compareTo(MAXIMUM_WITHDRAWAL) > 0) {
            throw new IllegalArgumentException("Maximum withdrawal allowed is " + MAXIMUM_WITHDRAWAL);
        } else if (amount.compareTo(new BigDecimal(0)) < 0) {
            throw new IllegalArgumentException("Withdrawal amount cannot be negative");
        } else if (amount.compareTo(accountBalance) > 0) {
            throw new IllegalArgumentException(
                "Insufficient balance. You are trying to withdraw " + amount + " but the balance is " + accountBalance
            );
        }
    }

    private void saveTrx(Long customerId, String currency, BigDecimal amount, String remark, TransactionType transactionType) {
        WalletTransaction trx = new WalletTransaction();
        trx.setCustomerId(customerId);
        trx.setAmount(amount);
        trx.setCurrencyCode(currency);
        trx.setRemarks(remark);
        trx.setTransactionType(transactionType);
        this.transactionRepository.save(trx);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WalletTransaction> getTransactions(Long customerId, Pageable pageable) {
        return this.transactionRepository.findAllByCustomerId(customerId, pageable);
    }
}
