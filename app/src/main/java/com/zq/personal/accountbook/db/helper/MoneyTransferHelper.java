package com.zq.personal.accountbook.db.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zq.personal.accountbook.db.table.MoneyTransferTable;
import com.zq.personal.accountbook.model.MoneyTransferRecord;
import com.zq.personal.accountbook.util.CommonConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 转账相关的数据库操作
 */
public class MoneyTransferHelper {
    private static final String TAG = CommonConstants.LOG_TAG + MoneyTransferHelper.class.getSimpleName();

    /**
     * 防止单例被外部实例化
     */
    private MoneyTransferHelper() {
    }

    /**
     * 新建一条转账记录
     *
     * @param record 转账信息
     * @return 处理结果
     */
    public static boolean createTransferRecord(MoneyTransferRecord record) {
        try (SQLiteDatabase db = AccountBookDBHelper.getInstance().getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(MoneyTransferTable.COLUMN_TRANSFER_OUT_ID, record.getTransferOutAccountId());
            values.put(MoneyTransferTable.COLUMN_TRANSFER_IN_ID, record.getTransferInAccountId());
            values.put(MoneyTransferTable.COLUMN_AMOUNT, record.getAmount());
            HelperUtil.addNonNullValue(values, MoneyTransferTable.COLUMN_DATE, record.getDate());
            HelperUtil.addNonNullValue(values, MoneyTransferTable.COLUMN_REMARK, record.getRemark());
            if (db.insert(MoneyTransferTable.TABLE_NAME, null, values) == -1) {
                Log.i(TAG, "createAccount fail.");
                return false;
            }
        } catch (SQLException e) {
            Log.i(TAG, "createAccount error.");
            return false;
        }
        return true;
    }

    /**
     * 通过账号ID查找转账记录
     *
     * @param accountId 账号ID
     * @return 转账记录列表
     */
    public static List<MoneyTransferRecord> queryTransferRecordsByAccountId(int accountId) {
        List<MoneyTransferRecord> recordList = new ArrayList<>();
        String selection =
                MoneyTransferTable.COLUMN_TRANSFER_IN_ID + "=? or "+ MoneyTransferTable.COLUMN_TRANSFER_OUT_ID + "=?";
        String[] selectionArgs = {String.valueOf(accountId), String.valueOf(accountId)};
        try (SQLiteDatabase db = AccountBookDBHelper.getInstance().getReadableDatabase();
             Cursor cursor = db.query(MoneyTransferTable.TABLE_NAME, null,
                     selection, selectionArgs, null, null, MoneyTransferTable.COLUMN_DATE + " desc")) {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    recordList.add(HelperUtil.convertToMoneyTransferRecord(cursor));
                }
            } else {
                Log.i(TAG, "queryTransferRecordsByAccountId, result empty.");
            }
        } catch (SQLException e) {
            Log.w(TAG, "querySavingsByName error.");
        }
        return recordList;
    }
}
