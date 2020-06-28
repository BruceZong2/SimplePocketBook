package com.zq.personal.accountbook.db.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zq.personal.accountbook.db.table.AccountTable;
import com.zq.personal.accountbook.model.AccountInfo;
import com.zq.personal.accountbook.util.CommonConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账户相关的数据库操作
 */
public class AccountHelper {
    private static final String TAG = CommonConstants.LOG_TAG + AccountHelper.class.getSimpleName();

    /**
     * 防止单例被外部实例化
     */
    private AccountHelper() {
    }

    /**
     * 查询账号信息，并按账号ID为Key，以map的方式返回
     *
     * @param includeSubAccount 是否包含子账户
     * @return 账户信息
     */
    public static Map<Integer, AccountInfo> queryAllAccountAsMap(boolean includeSubAccount) {
        Map<Integer, AccountInfo> accountInfoMap = new HashMap<>();
        List<AccountInfo> accountInfoList = queryAllAccount(includeSubAccount);
        for (AccountInfo info : accountInfoList) {
            accountInfoMap.put(info.getAccountId(), info);
        }
        return  accountInfoMap;
    }

    /**
     * 查询所有账户信息
     *
     * @param includeSubAccount 是否同时包含子账户
     * @return 账户信息列表
     */
    public static List<AccountInfo> queryAllAccount(boolean includeSubAccount) {
        List<AccountInfo> accountInfoList = new ArrayList<>();
        List<AccountInfo> allAccounts = queryAccounts();
        AccountInfo debitAccount = null;
        double debitBalance = 0.00;
        for (AccountInfo accountInfo : allAccounts) {
            if (accountInfo.getTypeId() == AccountTable.TYPE_ID_DEBIT_CARD_MAIN) {
                debitAccount = accountInfo;
                accountInfoList.add(accountInfo);
            } else if (accountInfo.getTypeId() == AccountTable.TYPE_ID_DEBIT_CARD) {
                debitBalance += accountInfo.getBalance();
                if (includeSubAccount) {
                    accountInfoList.add(accountInfo);
                }
            } else {
                accountInfoList.add(accountInfo);
            }
        }
        if (debitAccount != null) {
            debitAccount.setBalance(debitBalance);
        }
        return accountInfoList;
    }

    /**
     * 查询所有账户
     *
     * @return 账户列表
     */
    private static List<AccountInfo> queryAccounts() {
        List<AccountInfo> accountInfoList = new ArrayList<>();
        try (SQLiteDatabase db = AccountBookDBHelper.getInstance().getReadableDatabase();
            Cursor cursor = db.query(AccountTable.TABLE_NAME, null, null, null, null, null, null)) {
            if (cursor != null && cursor.getCount() > 0) {
                Log.i(TAG, "querySavings, size=" + cursor.getCount());
                while (cursor.moveToNext()) {
                    accountInfoList.add(buildAccountInfo(cursor));
                }
            } else {
                Log.i(TAG, "querySavings, no record.");
            }
        } catch (SQLException e) {
            Log.w(TAG, "querySavings error.");
        }
        return accountInfoList;
    }

    /**
     * 通过类型查询账户信息
     *
     * @param typeId 账户类型ID信息
     * @return 账户列表
     */
    public static List<AccountInfo> queryAccountByType(int typeId) {
        List<AccountInfo> accountInfoList = new ArrayList<>();
        try (SQLiteDatabase db = AccountBookDBHelper.getInstance().getReadableDatabase();
             Cursor cursor = db.query(AccountTable.TABLE_NAME, null,
                     AccountTable.COLUMN_TYPE_ID + "=?", new String[] { String.valueOf(typeId) }, null, null, null)) {
            if (cursor != null && cursor.getCount() > 0) {
                Log.i(TAG, "querySavings, size=" + cursor.getCount());
                while (cursor.moveToNext()) {
                    accountInfoList.add(buildAccountInfo(cursor));
                }
            } else {
                Log.i(TAG, "querySavings, no record.");
            }
        } catch (SQLException e) {
            Log.w(TAG, "querySavings error.");
        }
        return accountInfoList;
    }

    /**
     * 通过类型查询账户信息
     *
     * @param id 账户ID
     * @return 账户信息
     */
    public static AccountInfo queryAccountById(int id) {
        try (SQLiteDatabase db = AccountBookDBHelper.getInstance().getReadableDatabase();
             Cursor cursor = db.query(AccountTable.TABLE_NAME, null,
                     AccountTable.COLUMN_ID + "=?", new String[] { String.valueOf(id) }, null, null, null)) {
            if (cursor != null && cursor.getCount() > 0) {
                Log.i(TAG, "querySavings, size=" + cursor.getCount());
                cursor.moveToNext();
                return buildAccountInfo(cursor);
            } else {
                Log.i(TAG, "querySavings, no record.");
            }
        } catch (SQLException e) {
            Log.w(TAG, "querySavings error.");
        }
        return null;
    }

    private static AccountInfo buildAccountInfo(Cursor cursor) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setIssuerName(cursor.getString(cursor.getColumnIndex(AccountTable.COLUMN_ISSUER_NAME)));
        accountInfo.setAccountName(cursor.getString(cursor.getColumnIndex(AccountTable.COLUMN_NAME)));
        accountInfo.setBalance(cursor.getDouble(cursor.getColumnIndex(AccountTable.COLUMN_BALANCE)));
        accountInfo.setTypeId(cursor.getInt(cursor.getColumnIndex(AccountTable.COLUMN_TYPE_ID)));
        accountInfo.setAccountId(cursor.getInt(cursor.getColumnIndex(AccountTable.COLUMN_ID)));
        accountInfo.setRemark(cursor.getString(cursor.getColumnIndex(AccountTable.COLUMN_REMARK)));
        return accountInfo;
    }

    /**
     * 查询某一账户的余额
     *
     * @param accountId 账户ID
     * @return 余额
     */
    public static double queryBalanceByAccountId(int accountId) {
        try (SQLiteDatabase db = AccountBookDBHelper.getInstance().getReadableDatabase();
             Cursor cursor = db.query(AccountTable.TABLE_NAME, new String[] { AccountTable.COLUMN_BALANCE },
                     AccountTable.COLUMN_ID + "=?", new String[]{ String.valueOf(accountId) }, null, null, null)) {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToNext();
                return cursor.getDouble(0);
            }
        } catch (SQLException e) {
            Log.w(TAG, "querySavingsByName error.");
        }
        return 0.00;
    }

    /**
     * 更新账户余额
     *
     * @param accountId   账户名称
     * @param money  金额
     * @return 处理结果
     */
    public static boolean updateAccountBalance(int accountId, double money) {
        try (SQLiteDatabase db = AccountBookDBHelper.getInstance().getReadableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(AccountTable.COLUMN_BALANCE, money);
            db.update(AccountTable.TABLE_NAME, values, AccountTable.COLUMN_ID + "=?", new String[] { String.valueOf(accountId) });
            return true;
        } catch (SQLException e) {
            Log.w(TAG, "updateSavings error.");
        }
        return false;
    }

    /**
     * 创建一个账户
     *
     * @param debitCardInfo 账户信息
     * @return 创建结果
     */
    public static boolean createAccount(AccountInfo debitCardInfo) {
        try (SQLiteDatabase db = AccountBookDBHelper.getInstance().getWritableDatabase()) {
            ContentValues values = new ContentValues();
            HelperUtil.addNonNullValue(values, AccountTable.COLUMN_ISSUER_NAME, debitCardInfo.getIssuerName());
            HelperUtil.addNonNullValue(values, AccountTable.COLUMN_NAME, debitCardInfo.getAccountName());
            HelperUtil.addNonNullValue(values, AccountTable.COLUMN_REMARK, debitCardInfo.getRemark());
            values.put(AccountTable.COLUMN_TYPE_ID, debitCardInfo.getTypeId());
            values.put(AccountTable.COLUMN_ID, debitCardInfo.getAccountId());
            values.put(AccountTable.COLUMN_BALANCE, debitCardInfo.getBalance());
            if (db.insert(AccountTable.TABLE_NAME, null, values) == -1) {
                Log.i(TAG, "createAccount fail.");
                return false;
            }
        } catch (SQLException e) {
            Log.i(TAG, "createAccount error.");
            return false;
        }
        return true;
    }
}
