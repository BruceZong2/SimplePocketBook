package com.zq.personal.accountbook.db.table;

/**
 * 账户信息
 */
public class AccountTable {
    /**
     * 储蓄卡类型
     */
    public static final int TYPE_ID_DEBIT_CARD = 100;

    /**
     * 储蓄卡类型
     */
    public static final int TYPE_ID_DEBIT_CARD_MAIN = 4;

    /**
     * 数据库名称
     */
    public static final String TABLE_NAME = "account_savings";

    /**
     * 发行方名称
     */
    public static final String COLUMN_ISSUER_NAME = "issuer_name";

    /**
     * 账户类型ID, 比如：储蓄卡、信用卡、微信、支付宝
     */
    public static final String COLUMN_TYPE_ID = "type_id";

    /**
     * 账户ID，账户具体的ID，唯一标志
     */
    public static final String COLUMN_ID = "id";

    /**
     * 账户名称，后续可能需要考虑删除，以便支持国际化
     */
    public static final String COLUMN_NAME = "name";

    /**
     * 账户余额
     */
    public static final String COLUMN_BALANCE = "balance";

    /**
     * 备注信息
     */
    public static final String COLUMN_REMARK = "remark";

    /**
     * 创建账户余额表
     */
    public static final String CREATE_SAVINGS_TABLE = "create table " + AccountTable.TABLE_NAME + " ("
            + COLUMN_ID + " int primary key, "
            + COLUMN_TYPE_ID + " int,"
            + COLUMN_NAME + " text, "
            + COLUMN_ISSUER_NAME + " text,"
            + COLUMN_BALANCE + " Number, "
            + COLUMN_REMARK + " text)";

    /**
     * 预制账户类型及余额, 100:储蓄卡
     */
    public static final String INIT_SAVINGS_DATA = "insert into " + AccountTable.TABLE_NAME
            + " (" + COLUMN_TYPE_ID + "," + COLUMN_ID + "," + COLUMN_NAME + "," + COLUMN_BALANCE + ") values "
            + "(0, 0, '微信', 0.00),"
            + "(1, 1, '支付宝', 0.00),"
            + "(2, 2, '现金', 0.00),"
            + "(3, 3, '信用卡', 0.00),"
            + "(4, 4, '储蓄卡', 0.00),"
            + "(5, 5, '黄金钱包', 0.00),"
            + "(6, 6, '蚂蚁财富', 0.00),"
            + "(7, 7, '腾讯理财通', 0.00)";
}
