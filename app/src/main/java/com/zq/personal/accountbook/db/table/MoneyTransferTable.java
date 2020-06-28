package com.zq.personal.accountbook.db.table;

/**
 * 转账记录数据表
 */
public class MoneyTransferTable {
    /**
     * 数据表名称
     */
    public static final String TABLE_NAME = "money_transfer_record";

    /**
     * 记录ID
     */
    public static final String COLUMN_ID = "id";

    /**
     * 转出账号ID
     */
    public static final String COLUMN_TRANSFER_OUT_ID = "out_account_id";

    /**
     * 转入账号ID
     */
    public static final String COLUMN_TRANSFER_IN_ID = "in_account_id";

    /**
     * 转账金额
     */
    public static final String COLUMN_AMOUNT = "amount";

    /**
     * 日期
     */
    public static final String COLUMN_DATE = "date";

    /**
     * 备注
     */
    public static final String COLUMN_REMARK = "remark";

    /**
     * 创建数据表的语句
     */
    public static final String CREATE_MONEY_TRANSFER_TABLE= "create table " + TABLE_NAME + " ("
            + COLUMN_ID + " int primary key,"
            + COLUMN_TRANSFER_OUT_ID + " int,"
            + COLUMN_TRANSFER_IN_ID + " int,"
            + COLUMN_AMOUNT + " Number,"
            + COLUMN_DATE + " text,"
            + COLUMN_REMARK + " text)";
}
