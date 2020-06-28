package com.zq.personal.accountbook.model;

/**
 * 转账记录
 */
public class MoneyTransferRecord {
    private int transferOutAccountId;
    private int transferInAccountId;
    private int id;
    private double amount;
    private String date;
    private String remark;

    public int getTransferOutAccountId() {
        return transferOutAccountId;
    }

    public void setTransferOutAccountId(int transferOutAccountId) {
        this.transferOutAccountId = transferOutAccountId;
    }

    public int getTransferInAccountId() {
        return transferInAccountId;
    }

    public void setTransferInAccountId(int transferInAccountId) {
        this.transferInAccountId = transferInAccountId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
