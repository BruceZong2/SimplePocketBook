package com.zq.personal.accountbook.db.table;

/**
 * 收支记录表，每条记录详细信息
 */
public class AccountDetailTable {
    /**
     * 数据表名称
     */
    public static final String TABLE_NAME = "account_detail";

    /**
     * 自增ID列
     */
    public static final String COLUMN_ID = "_id";

    /**
     * 收支类型：收入、支出、余额变更等
     */
    public static final String COLUMN_RECORD_TYPE = "recordType";

    /**
     * 收支分类：餐饮、交通、工资等
     */
    public static final String COLUMN_CATEGORY = "category";

    /**
     * 日期：消费时间
     */
    public static final String COLUMN_DATE = "date";

    /**
     * 金额
     */
    public static final String COLUMN_AMOUNT = "money";

    /**
     * 金额，用于记录余额变更之前的金额
     */
    public static final String COLUMN_OLD_AMOUNT = "oldMoney";

    /**
     * 账户名称
     */
    public static final String COLUMN_ACCOUNT_NAME = "accountType";

    /**
     * 账户ID
     */
    public static final String COLUMN_ACCOUNT_ID = "account_id";

    /**
     * 备注信息
     */
    public static final String COLUMN_REMARK = "remark";

    /**
     * 创建每条交易的详细记录数据表
     */
    public static final String CREATE_ACCOUNT_DETAIL_TABLE =  "create table " + TABLE_NAME +  " ("
            + COLUMN_ID + " integer primary key autoincrement,"
            + COLUMN_RECORD_TYPE + " int,"
            + COLUMN_CATEGORY + " text,"
            + COLUMN_DATE + " text,"
            + COLUMN_AMOUNT + " Number,"
            + COLUMN_OLD_AMOUNT + " Number,"
            + COLUMN_ACCOUNT_NAME + " text,"
            + COLUMN_ACCOUNT_ID + " int,"
            + COLUMN_REMARK + " text)";
}
