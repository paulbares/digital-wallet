package com.digital.wallet.service;

import com.digital.wallet.domain.WalletTransaction;
import com.google.common.util.concurrent.Striped;
import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * This class is a safe implementation of the DigitalWalletService interface. <br>
 * It ensures thread safety by using striped locks for executing deposit and withdrawal operations and prevent from
 * executing concurrent requests simultaneously for the same customer.
 */
@Service
@Qualifier("SafeDigitalWalletService")
public class SafeDigitalWalletServiceImpl implements DigitalWalletService {

    private final Striped<Lock> sync = Striped.lazyWeakLock(32); // This value is subject to be modified it any contention is observed.

    private final DigitalWalletServiceImpl underlying;

    public SafeDigitalWalletServiceImpl(DigitalWalletServiceImpl underlying) {
        this.underlying = underlying;
    }

    @Override
    public void executeDeposit(Long customerId, String currency, BigDecimal amount, String remark) {
        safelyExecute(customerId, () -> this.underlying.executeDeposit(customerId, currency, amount, remark));
    }

    @Override
    public void executeWithdrawal(Long customerId, String currency, BigDecimal amount, String remark) {
        safelyExecute(customerId, () -> this.underlying.executeWithdrawal(customerId, currency, amount, remark));
    }

    private void safelyExecute(Long customerId, Runnable runnable) {
        Lock lock = this.sync.get(customerId);
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Page<WalletTransaction> getTransactions(Long customerId, Pageable pageable) {
        return this.underlying.getTransactions(customerId, pageable);
    }
}
