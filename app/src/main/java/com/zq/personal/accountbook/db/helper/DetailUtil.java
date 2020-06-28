package com.zq.personal.accountbook.db.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zq.personal.accountbook.db.table.AccountDetailTable;
import com.zq.personal.accountbook.model.IncomeOutcomeRecord;
import com.zq.personal.accountbook.util.CommonConstants;
import com.zq.personal.accountbook.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作相关接口
 *
 * @since 2020-05-30
 */
public final class DetailUtil {
    private static final String TAG = CommonConstants.LOG_TAG + DetailUtil.class.getSimpleName();

    /**
     * 防止被外部实例化
     */
    private DetailUtil() {
    }

    /**
     * 按月为单位查询收支详情
     *
     * @param yearMonth 年月，格式"2018-06"
     * @return 月度的整体收支记录
     */
    public static List<IncomeOutcomeRecord> queryDetailDataByYearAndMonth(String yearMonth) {
        List<IncomeOutcomeRecord> returnList = new ArrayList<>();
        String selection = AccountDetailTable.COLUMN_DATE + " like ? and " + AccountDetailTable.COLUMN_RECORD_TYPE + "<>?";
        String[] selectionArgs = new String[]{ yearMonth + "%", String.valueOf(Util.RecordType.RECORD_TYPE_SAVINGS.getIndex()) };
        String orderBy = AccountDetailTable.COLUMN_DATE + " desc," + AccountDetailTable.COLUMN_ID + " desc";
        try (SQLiteDatabase db = AccountBookDBHelper.getInstance().getReadableDatabase();
             Cursor cursor = db.query(AccountDetailTable.TABLE_NAME, null, selection,
                     selectionArgs, null, null, orderBy)) {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    returnList.add(convertToRecord(cursor));
                }
            } else {
                Log.i(TAG, "queryDetailDataByYearAndMonth, no record.");
            }
        } catch (SQLException e) {
            Log.w(TAG, "queryDetailDataByYearAndMonth error.");
        }
        return returnList;
    }

    /**
     * 查询某个账户的所有收支记录，按时间倒序排序
     *
     * @param accountId 账户ID
     * @return 收支记录列表
     */
    public static List<IncomeOutcomeRecord> queryRecordsByAccountId(int accountId) {
        List<IncomeOutcomeRecord> records = new ArrayList<>();
        String selection = AccountDetailTable.COLUMN_ACCOUNT_ID + "=?";
        String[] selectionArgs = new String[]{ String.valueOf(accountId) };
        try (SQLiteDatabase db = AccountBookDBHelper.getInstance().getReadableDatabase();
             Cursor cursor = db.query(AccountDetailTable.TABLE_NAME, null, selection,
                     selectionArgs, null, null, AccountDetailTable.COLUMN_DATE)) {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    records.add(convertToRecord(cursor));
                }
            } else {
                Log.i(TAG, "queryRecordsByAccountId, no record.");
            }
        } catch (SQLException e) {
            Log.w(TAG, "queryRecordsByAccountId error.");
        }
        return records;
    }

    private static IncomeOutcomeRecord convertToRecord(Cursor cursor) {
        IncomeOutcomeRecord record = new IncomeOutcomeRecord();
        record.setAmount(cursor.getDouble(cursor.getColumnIndex(AccountDetailTable.COLUMN_AMOUNT)));
        record.setId(cursor.getInt(cursor.getColumnIndex(AccountDetailTable.COLUMN_ID)));
        record.setRecordType(cursor.getInt(cursor.getColumnIndex(AccountDetailTable.COLUMN_RECORD_TYPE)));
        record.setCategory(cursor.getString(cursor.getColumnIndex(AccountDetailTable.COLUMN_CATEGORY)));
        record.setDate(cursor.getString(cursor.getColumnIndex(AccountDetailTable.COLUMN_DATE)));
        record.setAccountId(cursor.getInt(cursor.getColumnIndex(AccountDetailTable.COLUMN_ACCOUNT_ID)));
        record.setAccountName(cursor.getString(cursor.getColumnIndex(AccountDetailTable.COLUMN_ACCOUNT_NAME)));
        record.setRemark(cursor.getString(cursor.getColumnIndex(AccountDetailTable.COLUMN_REMARK)));
        return record;
    }

    /**
     * 增加一条记录
     *
     * @param record 新增记录信息
     * @param oldMoney 账户余额
     * @return 处理结果
     */
    public static boolean addAccountDetailItem(IncomeOutcomeRecord record, String oldMoney) {
        try (SQLiteDatabase db = AccountBookDBHelper.getInstance().getReadableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(AccountDetailTable.COLUMN_RECORD_TYPE, record.getRecordType());
            values.put(AccountDetailTable.COLUMN_ACCOUNT_ID, record.getAccountId());
            values.put(AccountDetailTable.COLUMN_AMOUNT, record.getAmount());
            HelperUtil.addNonNullValue(values, AccountDetailTable.COLUMN_CATEGORY, record.getCategory());
            HelperUtil.addNonNullValue(values, AccountDetailTable.COLUMN_REMARK, record.getRemark());
            HelperUtil.addNonNullValue(values, AccountDetailTable.COLUMN_DATE, record.getDate());
            HelperUtil.addNonNullValue(values, AccountDetailTable.COLUMN_ACCOUNT_NAME, record.getAccountName());
            HelperUtil.addNonNullValue(values, AccountDetailTable.COLUMN_OLD_AMOUNT, oldMoney);
            db.insert(AccountDetailTable.TABLE_NAME, null, values);
            return true;
        } catch (SQLException e) {
            Log.w(TAG, "addAccountDetailItem error.");
        }
        return false;
    }

    /**
     * 更新收支记录
     *
     * @param record 记录数据
     */
    public static void updateAccountDetailItem(IncomeOutcomeRecord record) {
        try (SQLiteDatabase db = AccountBookDBHelper.getInstance().getReadableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(AccountDetailTable.COLUMN_RECORD_TYPE, record.getRecordType());
            values.put(AccountDetailTable.COLUMN_ACCOUNT_ID, record.getAccountId());
            values.put(AccountDetailTable.COLUMN_AMOUNT, record.getAmount());
            HelperUtil.addNonNullValue(values, AccountDetailTable.COLUMN_CATEGORY, record.getCategory());
            HelperUtil.addNonNullValue(values, AccountDetailTable.COLUMN_REMARK, record.getRemark());
            HelperUtil.addNonNullValue(values, AccountDetailTable.COLUMN_DATE, record.getDate());
            HelperUtil.addNonNullValue(values, AccountDetailTable.COLUMN_ACCOUNT_NAME, record.getAccountName());
            String whereClause = AccountDetailTable.COLUMN_ID + "=?";
            String[] whereArgs = { String.valueOf(record.getId()) };
            db.update(AccountDetailTable.TABLE_NAME, values, whereClause, whereArgs);
        } catch (SQLException e) {
            Log.w(TAG, "updateAccountDetailItem error.");
        }
    }

    /**
     * 删除记录，删除的同时要更新相应账户的余额
     *
     * @param recordId 记录ID
     * @param accountId 账户ID
     * @param money 金额
     */
    public static void deleteAccountDetailItem(int recordId, int accountId, double money) {
        try (SQLiteDatabase db = AccountBookDBHelper.getInstance().getReadableDatabase()) {
            db.delete(AccountDetailTable.TABLE_NAME,
                    AccountDetailTable.COLUMN_ID + "=?", new String[]{String.valueOf(recordId)});
            double savings = AccountHelper.queryBalanceByAccountId(accountId);
            AccountHelper.updateAccountBalance(accountId, savings - money);
        } catch (SQLException e) {
            Log.w(TAG, "deleteAccountDetailItem error.");
        }
    }
}
