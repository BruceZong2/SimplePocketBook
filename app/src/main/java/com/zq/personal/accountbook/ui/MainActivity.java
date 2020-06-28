package com.zq.personal.accountbook.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.zq.personal.accountbook.R;
import com.zq.personal.accountbook.app.AccountApplication;
import com.zq.personal.accountbook.db.helper.DetailUtil;
import com.zq.personal.accountbook.model.IncomeOutcomeRecord;
import com.zq.personal.accountbook.ui.account.AccountManagerActivity;
import com.zq.personal.accountbook.ui.provider.MainListAdapter;
import com.zq.personal.accountbook.util.CommonConstants;
import com.zq.personal.accountbook.util.Util;
import com.zq.personal.accountbook.util.Util.RecordType;

import java.util.List;
import java.util.Locale;

/**
 * 主Activity
 *
 * @since 2020-05-30
 */
public class MainActivity extends Activity {
    private static final String TAG = CommonConstants.LOG_TAG + ":MainActivity";
    private TextView tvDateSelector;
    private TextView tvIncomeAmount;
    private TextView tvOutcomeAmount;
    private TextView tvSavingAmount;
    private ListView mainListView;
    private String currentYearAndMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        updateAccountDetailList();
    }

    private void init() {
        initView();
        initDateSelector();
        initMainList();
        updateAccountDetailList();
    }

    private void initView() {
        this.tvIncomeAmount = this.findViewById(R.id.textIncomeMoney);
        this.tvOutcomeAmount = this.findViewById(R.id.textOutcomeMoney);
        this.tvSavingAmount = this.findViewById(R.id.textSavingMoney);
        this.tvDateSelector = this.findViewById(R.id.dateSelector);
    }

    /**
     * 初始化月度选择器
     */
    private void initDateSelector() {
        this.currentYearAndMonth = Util.getCurrentDate(Util.DATE_YM_FORMAT);
        updateCurrentYearAndMonth();
    }

    /**
     * 动态添加主数据列表（可以通过xml使用静态的，此处为了练习）
     */
    private void initMainList() {
        this.mainListView = new ListView(this);
        LayoutParams layoutParam = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParam.addRule(RelativeLayout.BELOW, R.id.main_top);
        layoutParam.addRule(RelativeLayout.ABOVE, R.id.mainBtnLayout);
        this.mainListView.setLayoutParams(layoutParam);
        RelativeLayout layout = this.findViewById(R.id.mainLayout);
        layout.addView(this.mainListView);
        this.mainListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                IncomeOutcomeRecord record = (IncomeOutcomeRecord) parent.getAdapter().getItem(pos);
                if (record.getRecordType() == RecordType.RECORD_TYPE_SAVINGS.getIndex()) {
                    // 余额变更不支持编辑
                    String tipStr = AccountApplication.getContext().getString(R.string.ab_tip_cannot_modify_savings);
                    Toast.makeText(MainActivity.this, tipStr, Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putParcelable(CommonConstants.BUNDLE_KEY_INCOME_OUTCOME_RECORD, record);
                bundle.putInt(AddRecordActivity.BUNDLE_KEY_MODE_STR, AddRecordActivity.EDIT_RECORD);
                Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    /**
     * 更新顶部月度收支详情
     */
    private void updateAccountDetailList() {
        List<IncomeOutcomeRecord> recordList = DetailUtil.queryDetailDataByYearAndMonth(currentYearAndMonth);
        updateMonthTotal(recordList);
        MainListAdapter adapter = new MainListAdapter(this);
        adapter.setRecordList(recordList);
        this.mainListView.setAdapter(adapter);
    }

    private void updateMonthTotal(List<IncomeOutcomeRecord> recordList) {
        double income = 0.00;
        double outcome = 0.00;
        for (IncomeOutcomeRecord record : recordList) {
            if (record.getAmount() > 1E-7) {
                income += record.getAmount();
            } else {
                outcome += record.getAmount();
            }
        }
        this.tvIncomeAmount.setText(Util.formatMoney(income));
        this.tvOutcomeAmount.setText(Util.formatMoney(outcome));
        this.tvSavingAmount.setText(Util.formatMoney(income + outcome));
    }

    /**
     * 新增收支按键事件
     *
     * @param view 按钮
     */
    public void onAddNewRecord(View view) {
        Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
        intent.putExtra(AddRecordActivity.BUNDLE_KEY_MODE_STR, AddRecordActivity.ADD_NEW_RECORD);
        startActivity(intent);
    }

    /**
     * 跳转账户详情事件
     *
     * @param view 按钮
     */
    public void onAccountDetail(View view) {
        Intent intent = new Intent(MainActivity.this, AccountManagerActivity.class);
        startActivity(intent);
    }

    /**
     * 图形化报告详情
     *
     * @param view 按钮
     */
    public void onChartReport(View view) {
        String tipStr = AccountApplication.getContext().getString(R.string.ab_tip_module_developing_wait);
        new AlertDialog.Builder(MainActivity.this).setMessage(tipStr).show();
    }

    /**
     * 选择详情日期事件
     *
     * @param view 按钮
     */
    public void onDateSelector(View view) {
        String[] dateArr = currentYearAndMonth.split("-");
        new DatePickerDialog(MainActivity.this, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int year, int monthOfYear, int dayOfMonth) {
                currentYearAndMonth = generateYearMonth(year, monthOfYear);
                updateCurrentYearAndMonth();
                updateAccountDetailList();
                Toast.makeText(MainActivity.this, currentYearAndMonth, Toast.LENGTH_SHORT).show();
            }

            private String generateYearMonth(int year, int monthOfYear) {
                return String.format(Locale.ENGLISH, "%04d", year)
                        + "-" + String.format(Locale.ENGLISH, "%02d", monthOfYear + 1);
            }
        }, Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]) - 1, 1).show();
    }

    private void updateCurrentYearAndMonth() {
        String curYearMonth = this.currentYearAndMonth + CommonConstants.ARROW_DOWN_STR;
        this.tvDateSelector.setText(curYearMonth);
    }
}
