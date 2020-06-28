package com.zq.personal.accountbook.model;

/**
 * 账号信息
 */
public class AccountInfo {
    /**
     * 发行方名称，可以为空
     */
    private String issuerName;

    /**
     * 主标题：账户名称
     */
    private String accountName;

    /**
     * 账户余额
     */
    private double balance;

    /**
     * 账户类型：储蓄卡、信用卡、现金等
     */
    private int typeId;

    /**
     * 账户ID
     */
    private int accountId;

    /**
     * 备注信息
     */
    private String remark;

    public AccountInfo() {
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
}
