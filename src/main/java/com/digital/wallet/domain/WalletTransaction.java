package com.digital.wallet.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A user.
 */
@Entity
@Table(name = "wallet_transaction")
public class WalletTransaction extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * Transaction Amount in Base Currency.
     */
    @NotNull
    @Column(name = "base_currency_amount", precision = 21, scale = 2)
    private BigDecimal baseCurrencyAmount;

    /**
     * Deposit Amount.
     */
    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    /**
     * Currency for Deposit Amount.
     */
    @NotNull
    @Column(name = "currency", nullable = false)
    private String currency;

    /**
     * Remarks.
     */
    @Column(name = "remarks")
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBaseCurrencyAmount() {
        return baseCurrencyAmount;
    }

    public void setBaseCurrencyAmount(BigDecimal baseCurrencyAmount) {
        this.baseCurrencyAmount = baseCurrencyAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletTransaction that = (WalletTransaction) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(baseCurrencyAmount, that.baseCurrencyAmount) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(currency, that.currency) &&
            Objects.equals(remarks, that.remarks)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseCurrencyAmount, amount, currency, remarks);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WalletTransaction{");
        sb.append("id=").append(id);
        sb.append(", baseCurrencyAmount=").append(baseCurrencyAmount);
        sb.append(", amount=").append(amount);
        sb.append(", currency='").append(currency).append('\'');
        sb.append(", remarks='").append(remarks).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
