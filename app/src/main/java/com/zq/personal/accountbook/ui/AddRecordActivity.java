package com.zq.personal.accountbook.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.zq.personal.accountbook.R;
import com.zq.personal.accountbook.db.helper.AccountHelper;
import com.zq.personal.accountbook.db.helper.DetailUtil;
import com.zq.personal.accountbook.db.table.AccountTable;
import com.zq.personal.accountbook.model.AccountInfo;
import com.zq.personal.accountbook.model.IncomeOutcomeRecord;
import com.zq.personal.accountbook.ui.account.AccountHistoryActivity;
import com.zq.personal.accountbook.util.CommonConstants;
import com.zq.personal.accountbook.util.Util;
import com.zq.personal.accountbook.util.Util.RecordType;

import java.util.List;
import java.util.Locale;

/**
 * 添加/编辑一笔支出/收入
 */
public class AddRecordActivity extends Activity {
    /**
     * 新增记录
     */
    public static final int ADD_NEW_RECORD = 0;

    /**
     * 修改记录
     */
    public static final int EDIT_RECORD = 1;

    /**
     * 模式key值
     */
    public static final String BUNDLE_KEY_MODE_STR = "mode";
    private static final String TAG = CommonConstants.LOG_TAG + AddRecordActivity.class.getSimpleName();
    /**
     * 模式： 0，新增； 1，编辑、删除
     */
    private int mode = ADD_NEW_RECORD;
    private RadioGroup recordTypeGroup;
    private Spinner categorySpin;
    private EditText dateEdit;
    private Spinner accountSpin;
    private EditText moneyEdit;
    private EditText remarkEdit;
    private Button deleteBtn;

    private IncomeOutcomeRecord curRecord;
    private IncomeOutcomeRecord originRecord;
    private List<AccountInfo> accountInfoList;
    private AccountInfo selectedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_record_activity);
        initView();
        setViewEvents();
        initData();

        if (mode == ADD_NEW_RECORD) {
            dateEdit.setText(Util.getCurrentDate(Util.DATE_YMD_FORMAT));
            deleteBtn.setVisibility(View.GONE);
        } else {
            updateViews();
        }
    }

    private void updateViews() {
        // 编辑状态不支持类型转换，只能编辑日期、金额、账户和备注
        for (int i = 0; i < recordTypeGroup.getChildCount(); i++) {
            recordTypeGroup.getChildAt(i).setEnabled(false);
        }
        refreshCategorySpin(curRecord.getRecordType());
        if (curRecord.getRecordType() == RecordType.RECORD_TYPE_OUTCOME.getIndex()) {
            recordTypeGroup.check(R.id.addOutcomeRecord);
        } else if (curRecord.getRecordType() == RecordType.RECORD_TYPE_INCOME.getIndex()) {
            recordTypeGroup.check(R.id.addIncomeRecord);
        }
        categorySpin.setSelection(Util.getSpinnerPositionByText(categorySpin, curRecord.getCategory()));
        dateEdit.setText(curRecord.getDate());
        moneyEdit.setText(String.valueOf(Math.abs(curRecord.getAmount())));
        accountSpin.setSelection(Util.getSpinnerPositionByText(accountSpin, curRecord.getAccountName()));
        selectedAccount = AccountHelper.queryAccountById(curRecord.getAccountId());
        remarkEdit.setText(curRecord.getRemark());
    }

    private void initView() {
        recordTypeGroup = (RadioGroup) this.findViewById(R.id.recordTypeGroup);
        categorySpin = (Spinner) findViewById(R.id.categorySpin);
        dateEdit = ((EditText) this.findViewById(R.id.dateEdit));
        moneyEdit = (EditText) findViewById(R.id.moneyEdit);
        accountSpin = (Spinner) findViewById(R.id.accountSpin);
        remarkEdit = (EditText) findViewById(R.id.remarkEdit);
        deleteBtn = (Button) this.findViewById(R.id.deleteBtn);
    }

    private void setViewEvents() {
        recordTypeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                typeChanged(checkedId);
            }
        });
    }

    private void initData() {
        initDateFromIntent();
        initAccountSelections();
        if (curRecord == null) {
            curRecord = new IncomeOutcomeRecord();
            // 默认为支出
            curRecord.setRecordType(RecordType.RECORD_TYPE_OUTCOME.getIndex());
        }
    }

    private void initDateFromIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        if (bundle.containsKey(CommonConstants.BUNDLE_KEY_INCOME_OUTCOME_RECORD)) {
            originRecord = bundle.getParcelable(CommonConstants.BUNDLE_KEY_INCOME_OUTCOME_RECORD);
            curRecord = originRecord.clone();
        }
        if (bundle.containsKey(AddRecordActivity.BUNDLE_KEY_MODE_STR)) {
            mode = bundle.getInt(AddRecordActivity.BUNDLE_KEY_MODE_STR, ADD_NEW_RECORD);
        }
    }

    private void initAccountSelections() {
        accountInfoList = AccountHelper.queryAllAccount(true);
        selectedAccount = accountInfoList.get(0);
        String[] accountArray = new String[accountInfoList.size()];
        for (int i = 0; i < accountInfoList.size(); i++) {
            AccountInfo accountInfo = accountInfoList.get(i);
            if (accountInfo.getTypeId() == AccountTable.TYPE_ID_DEBIT_CARD) {
                accountArray[i] = accountInfo.getAccountName() + "[" + accountInfo.getIssuerName() + "]";
            } else {
                accountArray[i] = accountInfo.getAccountName();
            }
        }
        ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(
                AddRecordActivity.this, android.R.layout.simple_spinner_dropdown_item, accountArray);
        accountSpin.setAdapter(accountAdapter);
        accountSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAccount = accountInfoList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void refreshCategorySpin(int recordType) {
        String[] arr;
        if (recordType == RecordType.RECORD_TYPE_INCOME.getIndex()) {
            arr = getResources().getStringArray(R.array.income_type);
        } else {
            arr = getResources().getStringArray(R.array.outcome_type);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                AddRecordActivity.this, android.R.layout.simple_spinner_dropdown_item, arr);
        ((Spinner) findViewById(R.id.categorySpin)).setAdapter(adapter);
    }

    private void hideCategoryAndDate(boolean bHide) {
        if (bHide) {
            findViewById(R.id.categoryLayout).setVisibility(View.GONE);
            findViewById(R.id.dateLayout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.categoryLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.dateLayout).setVisibility(View.VISIBLE);
        }
    }

    private boolean checkRecord() {
        String money = moneyEdit.getText().toString();
        if (money.isEmpty()) {
            Toast.makeText(this,
                    getResources().getString(R.string.ab_check_error_amount_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (curRecord.getRecordType() == RecordType.RECORD_TYPE_OUTCOME.getIndex()) {
            money = "-" + money;
        }
        String moneyStr = String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(money));
        curRecord.setAmount(Double.parseDouble(moneyStr));
        curRecord.setCategory(categorySpin.getSelectedItem().toString());
        curRecord.setDate(dateEdit.getText().toString());
        curRecord.setAccountName(selectedAccount.getAccountName());
        curRecord.setAccountId(selectedAccount.getAccountId());
        curRecord.setRemark(remarkEdit.getText().toString());
        return true;
    }

    /**
     * 保存记录
     *
     * @param view 按钮
     */
    public void saveRecord(View view) {
        if (!checkRecord()) {
            return;
        }
        // 余额变更
        if (curRecord.getRecordType() == RecordType.RECORD_TYPE_SAVINGS.getIndex()) {
            dealWithBalanceChange();
        } else if (mode == ADD_NEW_RECORD) {
            dealWithRecordNew();
        } else {
            dealWithRecordChange();
        }
        finish();
    }

    private void dealWithRecordNew() {
        boolean result = DetailUtil.addAccountDetailItem(curRecord, null);
        if (result) {
            double accountBalanceNew = selectedAccount.getBalance() + curRecord.getAmount();
            AccountHelper.updateAccountBalance(curRecord.getAccountId(), accountBalanceNew);
        } else {
            Log.i(TAG, "save record fail.");
        }
    }

    private void dealWithRecordChange() {
        if (curRecord.getAccountId() == originRecord.getAccountId()) {
            // 账户类型没变，金额变了
            double saving = selectedAccount.getBalance();
            double newSaving = saving + curRecord.getAmount() - originRecord.getAmount();
            AccountHelper.updateAccountBalance(curRecord.getAccountId(), newSaving);
        } else {
            // 账户变了
            double srcAccountSaving = AccountHelper.queryBalanceByAccountId(originRecord.getAccountId());
            double dstAccountSaving = AccountHelper.queryBalanceByAccountId(curRecord.getAccountId());
            double srcAccountNewSaving = srcAccountSaving - originRecord.getAmount();
            double dstAccountNewSaving = dstAccountSaving + curRecord.getAmount();
            AccountHelper.updateAccountBalance(originRecord.getAccountId(), srcAccountNewSaving);
            AccountHelper.updateAccountBalance(curRecord.getAccountId(), dstAccountNewSaving);
        }
        DetailUtil.updateAccountDetailItem(curRecord);
    }

    private void dealWithBalanceChange() {
        double beforeModifyBalance = selectedAccount.getBalance();
        // 更新账户的余额
        AccountHelper.updateAccountBalance(curRecord.getAccountId(), curRecord.getAmount());
        double moneyOff = curRecord.getAmount() - beforeModifyBalance;
        curRecord.setAmount(moneyOff);
        // 设置余额变更的收支类型
        curRecord.setCategory(getResources().getString(R.string.ab_text_balance_modify));
        // 插入账户余额变更的操作信息
        DetailUtil.addAccountDetailItem(curRecord, Util.formatMoney(beforeModifyBalance));
        // 跳转到账户详情
        Intent intent = new Intent(AddRecordActivity.this, AccountHistoryActivity.class);
        intent.putExtra(CommonConstants.BUNDLE_KEY_ACCOUNT_ID, curRecord.getAccountId());
        startActivity(intent);
    }

    /**
     * 删除按钮响应事件，删除记录
     *
     * @param view 按钮
     */
    public void deleteRecord(View view) {
        DetailUtil.deleteAccountDetailItem(originRecord.getId(), originRecord.getAccountId() , originRecord.getAmount());
        finish();
    }

    /**
     * 日期变更
     *
     * @param view 日期编辑框
     */
    public void onDateChanged(View view) {
        String date = dateEdit.getText().toString();
        String[] dateArr = date.split("-");
        new DatePickerDialog(AddRecordActivity.this, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int year, int monthOfYear, int dayOfMonth) {
                String selectedDate = String.format(Locale.ENGLISH, "%04d", year) + "-"
                        + String.format(Locale.ENGLISH, "%02d", monthOfYear + 1)
                        + "-" + String.format(Locale.ENGLISH, "%02d", dayOfMonth);
                dateEdit.setText(selectedDate);
                Toast.makeText(AddRecordActivity.this, selectedDate, Toast.LENGTH_SHORT).show();
            }
        }, Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]) - 1, Integer.parseInt(dateArr[2])).show();
    }

    /**
     * 支出/收入/余额变更 类型变化
     *
     * @param checkedId 选择的类型
     */
    private void typeChanged(int checkedId) {
        if (checkedId == R.id.addIncomeRecord) {
            curRecord.setRecordType(RecordType.RECORD_TYPE_INCOME.getIndex());
            refreshCategorySpin(RecordType.RECORD_TYPE_INCOME.getIndex());
            hideCategoryAndDate(false);
            moneyEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            moneyEdit.setText("");
        } else if (checkedId == R.id.savingRecord) {
            curRecord.setRecordType(RecordType.RECORD_TYPE_SAVINGS.getIndex());
            hideCategoryAndDate(true);
            moneyEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            moneyEdit.setText("");
        } else {
            curRecord.setRecordType(RecordType.RECORD_TYPE_OUTCOME.getIndex());
            refreshCategorySpin(RecordType.RECORD_TYPE_OUTCOME.getIndex());
            hideCategoryAndDate(false);
            moneyEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            moneyEdit.setText("");
        }
    }
}
