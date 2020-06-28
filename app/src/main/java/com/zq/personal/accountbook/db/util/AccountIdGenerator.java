package com.zq.personal.accountbook.db.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 账户ID生成器
 */
public class AccountIdGenerator {
    private static AccountIdGenerator instance;
    private AtomicInteger accountId;

    private AccountIdGenerator() {
        // TODO：查询数据库，获取最大的ID值
        accountId = new AtomicInteger(1000);
    }

    public static AccountIdGenerator instance() {
        if (instance == null) {
            synchronized (AccountIdGenerator.class) {
                if (instance == null) {
                    instance = new AccountIdGenerator();
                }
            }
        }
        return instance;
    }

    /**
     * 生成下一个账户ID的值
     *
     * @return ID值
     */
    public int getNextAccountId() {
        return accountId.incrementAndGet();
    }
}
