package com.digital.wallet.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "wallet_account")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WalletAccount extends AbstractAuditingEntity<Long> implements Serializable {

    @Version // for enabling optimistic locking
    private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * The amount of money available.
     */
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    /**
     * As per ISO 4217. <a href="https://en.wikipedia.org/wiki/ISO_4217">https://en.wikipedia.org/wiki/ISO_4217</a>
     */
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletAccount that = (WalletAccount) o;
        return (
            Objects.equals(version, that.version) &&
            Objects.equals(id, that.id) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(currencyCode, that.currencyCode)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, id, amount, currencyCode);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WalletAccount{");
        sb.append("version=").append(version);
        sb.append(", id=").append(id);
        sb.append(", amount=").append(amount);
        sb.append(", currencyCode='").append(currencyCode).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
