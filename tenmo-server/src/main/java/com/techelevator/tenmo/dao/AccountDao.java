package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface AccountDao {
    /**
     * User should be able to get account balance.
     */

    BigDecimal getAccountBalance(int userId);

}
