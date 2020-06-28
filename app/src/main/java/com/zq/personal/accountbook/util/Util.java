package com.zq.personal.accountbook.util;

import android.content.Context;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 一些公共类和变量
 *
 * @since 2020-05-30
 */
public class Util {
	/**
	 * 年月日的格式
	 */
	public static final String DATE_YMD_FORMAT = "yyyy-MM-dd";

	/**
	 * 年月的格式，比如首页的月份
	 */
	public static final String DATE_YM_FORMAT = "yyyy-MM";

	/**
	 * 记录类型：支出、收入、余额变更，账号间转账
	 */
	public enum RecordType {
		RECORD_TYPE_OUTCOME("outcome", 0),
		RECORD_TYPE_INCOME("income", 1),
		// 余额变更
		RECORD_TYPE_SAVINGS("savings", 2);
		
		private String name;
		private int index;
		
		RecordType(String name, int index) {
			this.name = name;
			this.index = index;
		}
		
		public String getName() {
			return name;
		}
		
		public int getIndex() {
			return index;
		}
	}

	/**
	 * 根据提供的格式获取当前时间
	 *
	 * @param format 时间格式
	 * @return 格式化的当前时间
	 */
	public static String getCurrentDate(String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
		return dateFormat.format(new Date());
	}
	
	public static int getSpinnerPositionByText(Spinner spin, String text) {
		for (int i = 0; i < spin.getCount(); i ++) {
			if (spin.getItemAtPosition(i).toString().equals(text)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 格式化金额字符串，保留两位小数
	 *
	 * @param money 金额字符串
	 * @return 格式化后的金额字符串
	 */
	public static String formatMoney(String money) {
		if (money == null || money.length() == 0) {
			return "0.00";
		} else {
			Double moneyDouble = Double.parseDouble(money);
			return String.format(Locale.ENGLISH, "%.2f", moneyDouble);
		}
	}

	/**
	 * 格式化金额字符串，保留两位小数
	 *
	 * @param money 金额
	 * @return 格式化后的金额字符串
	 */
	public static String formatMoney(double money) {
		return String.format(Locale.ENGLISH, "%.2f", money);
	}

	/**
	 * Toast公共方法
	 *
	 * @param context 上下文
	 * @param resId 字符串资源ID
	 */
	public static void showToast(Context context, int resId) {
		Toast.makeText(context, context.getResources().getString(resId), Toast.LENGTH_SHORT).show();
	}
}
