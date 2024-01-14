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
     * The owner of the account
     */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

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
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletTransaction that = (WalletTransaction) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(currencyCode, that.currencyCode) &&
            Objects.equals(remarks, that.remarks) &&
            transactionType == that.transactionType
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, currencyCode, remarks, transactionType);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WalletTransaction{");
        sb.append("id=").append(id);
        sb.append(", amount=").append(amount);
        sb.append(", currencyCode='").append(currencyCode).append('\'');
        sb.append(", remarks='").append(remarks).append('\'');
        sb.append(", transactionType=").append(transactionType);
        sb.append('}');
        return sb.toString();
    }
}
