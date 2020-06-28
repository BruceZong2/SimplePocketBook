package com.zq.personal.accountbook.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zq.personal.accountbook.app.AccountApplication;
import com.zq.personal.accountbook.db.table.AccountDetailTable;
import com.zq.personal.accountbook.db.table.AccountTable;
import com.zq.personal.accountbook.db.table.MoneyTransferTable;
import com.zq.personal.accountbook.util.CommonConstants;

/**
 * 数据库管理，包括创建及升级
 *
 * @since 2020-05-30
 */
public final class AccountBookDBHelper extends SQLiteOpenHelper {
    /**
     * 当前数据库版本
     */
    public static final int DATABASE_VERSION = 1;

    /**
     * 数据库名称
     */
    public static final String DATABASE_NAME = "AccountBook.db";

    private static final String TAG = CommonConstants.LOG_TAG + AccountBookDBHelper.class.getSimpleName();
    private static volatile AccountBookDBHelper instance;

    public static AccountBookDBHelper getInstance() {
        if (instance == null) {
            synchronized (AccountBookDBHelper.class) {
                if (instance == null) {
                    instance = new AccountBookDBHelper(
                            AccountApplication.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
                }
            }
        }
        return instance;
    }

    private AccountBookDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(AccountDetailTable.CREATE_ACCOUNT_DETAIL_TABLE);
            db.execSQL(AccountTable.CREATE_SAVINGS_TABLE);
            db.execSQL(AccountTable.INIT_SAVINGS_DATA);
            db.execSQL(MoneyTransferTable.CREATE_MONEY_TRANSFER_TABLE);
        } catch (Exception e) {
            Log.e(TAG, "Create database error, e=" + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Database upgrade from " + oldVersion + " to " + newVersion);
    }
}
