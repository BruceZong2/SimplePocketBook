package com.zq.personal.accountbook.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 收支记录
 */
public class IncomeOutcomeRecord implements Parcelable {
    /**
     * 记录ID
     */
    private int id;

    /**
     * 记录类型，收入/支出/余额变更
     */
    private int recordType;

    /**
     * 消费/收入类型
     */
    private String category;

    /**
     * 日期
     */
    private String date;

    /**
     * 金额
     */
    private double amount;

    /**
     * 付款/收款账号
     */
    private String accountName;

    /**
     * 付款/收款账号ID
     */
    private int accountId;

    /**
     * 备注信息
     */
    private String remark;

    public IncomeOutcomeRecord() {
    }

    protected IncomeOutcomeRecord(Parcel in) {
        id = in.readInt();
        recordType = in.readInt();
        category = in.readString();
        date = in.readString();
        amount = in.readDouble();
        accountName = in.readString();
        remark = in.readString();
        accountId = in.readInt();
    }

    public static final Creator<IncomeOutcomeRecord> CREATOR = new Creator<IncomeOutcomeRecord>() {
        @Override
        public IncomeOutcomeRecord createFromParcel(Parcel in) {
            return new IncomeOutcomeRecord(in);
        }

        @Override
        public IncomeOutcomeRecord[] newArray(int size) {
            return new IncomeOutcomeRecord[size];
        }
    };

    public int getRecordType() {
        return recordType;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(recordType);
        dest.writeString(category);
        dest.writeString(date);
        dest.writeDouble(amount);
        dest.writeString(accountName);
        dest.writeString(remark);
        dest.writeInt(accountId);
    }

    public IncomeOutcomeRecord clone(){
        IncomeOutcomeRecord record = new IncomeOutcomeRecord();
        record.setId(id);
        record.setRecordType(recordType);
        record.setCategory(category);
        record.setDate(date);
        record.setAmount(amount);
        record.setAccountName(accountName);
        record.setAccountId(accountId);
        record.setRemark(remark);
        return record;
    }
}
