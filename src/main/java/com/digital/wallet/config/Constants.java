package com.digital.wallet.config;

import java.math.BigDecimal;

/**
 * Application constants.
 */
public final class Constants {

    public static final String SYSTEM = "system";

    public static final BigDecimal MAXIMUM_DEPOSIT = BigDecimal.valueOf(10_000);

    public static final BigDecimal MAXIMUM_WITHDRAWAL = BigDecimal.valueOf(5_000);

    public static final BigDecimal MINIMUM_DEPOSIT = BigDecimal.valueOf(10);

    private Constants() {}
}
