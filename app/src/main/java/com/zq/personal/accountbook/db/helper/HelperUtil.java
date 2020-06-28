package com.zq.personal.accountbook.db.helper;

import android.content.ContentValues;
import android.database.Cursor;

import com.zq.personal.accountbook.db.table.MoneyTransferTable;
import com.zq.personal.accountbook.model.MoneyTransferRecord;

/**
 * 各种数据转换类
 */
public class HelperUtil {

    /**
     * 非空时加入列表
     *
     * @param values 键值对
     * @param name   列名称
     * @param value  值
     */
    public static void addNonNullValue(ContentValues values, String name, String value) {
        if (values != null && name != null && value != null) {
            values.put(name, value);
        }
    }

    /**
     * 转换转账记录
     *
     * @param cursor 数据库结果
     * @return 转换结果
     */
    public static MoneyTransferRecord convertToMoneyTransferRecord(Cursor cursor) {
        MoneyTransferRecord record = new MoneyTransferRecord();
        record.setId(cursor.getInt(cursor.getColumnIndex(MoneyTransferTable.COLUMN_ID)));
        record.setTransferInAccountId(cursor.getInt(cursor.getColumnIndex(MoneyTransferTable.COLUMN_TRANSFER_IN_ID)));
        record.setTransferOutAccountId(cursor.getInt(cursor.getColumnIndex(MoneyTransferTable.COLUMN_TRANSFER_OUT_ID)));
        record.setAmount(cursor.getDouble(cursor.getColumnIndex(MoneyTransferTable.COLUMN_AMOUNT)));
        record.setDate(cursor.getString(cursor.getColumnIndex(MoneyTransferTable.COLUMN_DATE)));
        record.setRemark(cursor.getString(cursor.getColumnIndex(MoneyTransferTable.COLUMN_REMARK)));
        return record;
    }
}
