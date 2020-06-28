package com.zq.personal.accountbook.ui.account;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zq.personal.accountbook.R;
import com.zq.personal.accountbook.db.helper.AccountHelper;
import com.zq.personal.accountbook.db.helper.MoneyTransferHelper;
import com.zq.personal.accountbook.model.AccountInfo;
import com.zq.personal.accountbook.model.MoneyTransferRecord;
import com.zq.personal.accountbook.ui.BaseActivity;
import com.zq.personal.accountbook.util.CommonConstants;
import com.zq.personal.accountbook.util.Util;

import java.util.List;
import java.util.Locale;

/**
 * 转账页面
 */
public class MoneyTransferActivity extends BaseActivity {
    private static final String TAG = CommonConstants.LOG_TAG + MoneyTransferActivity.class.getSimpleName();
    private MoneyTransferRecord transferRecord;
    private List<AccountInfo> accountInfoList;
    private AccountInfo outAccount;
    private AccountInfo inAccount;
    private String[] accountArr;
    private TextView tvOutAccount;
    private TextView tvInAccount;
    private TextView tvTransferDate;
    private EditText etTransferAmount;
    private EditText etRemark;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_transfer);
        initView();
        initData();
    }

    private void initView() {
        this.tvOutAccount = findViewById(R.id.tv_transfer_out_account);
        this.tvInAccount = findViewById(R.id.tv_transfer_in_account);
        this.tvTransferDate = findViewById(R.id.tv_transfer_date);
        this.etTransferAmount = findViewById(R.id.et_transfer_amount);
        this.etRemark = findViewById(R.id.et_remark);
    }

    private void initData() {
        this.tvTransferDate.setText(Util.getCurrentDate(Util.DATE_YMD_FORMAT));
        transferRecord = new MoneyTransferRecord();
        initAccountArray();
    }

    private void initAccountArray() {
        accountInfoList = AccountHelper.queryAllAccount(true);
        int num = accountInfoList.size();
        accountArr = new String[num];
        for (int i = 0; i < num; i++) {
            accountArr[i] = accountInfoList.get(i).getAccountName();
        }
        if (num > 0) {
            tvOutAccount.setText(accountArr[0]);
            tvInAccount.setText(accountArr[0]);
        }
    }

    /**
     * 选择转出账户
     *
     * @param view 转出账户文本控件
     */
    public void selectTransferOutAccount(View view) {
        new AlertDialog.Builder(this)
                .setItems(accountArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        outAccount = accountInfoList.get(which);
                        transferRecord.setTransferOutAccountId(outAccount.getAccountId());
                        tvOutAccount.setText(outAccount.getAccountName());
                    }
                }).create().show();
    }

    /**
     * 选择转入账户
     *
     * @param view 转入账户文本控件
     */
    public void selectTransferInAccount(View view) {
        new AlertDialog.Builder(this)
                .setItems(accountArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inAccount = accountInfoList.get(which);
                        transferRecord.setTransferInAccountId(inAccount.getAccountId());
                        tvInAccount.setText(inAccount.getAccountName());
                    }
                }).create().show();
    }

    /**
     * 日期选择
     *
     * @param view 日期文本控件
     */
    public void onDateChanged(View view) {
        String date = tvTransferDate.getText().toString();
        String[] dateArr = date.split("-");
        new DatePickerDialog(MoneyTransferActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int year, int monthOfYear, int dayOfMonth) {
                String selectedDate = String.format(Locale.ENGLISH, "%04d", year) + "-"
                        + String.format(Locale.ENGLISH, "%02d", monthOfYear + 1)
                        + "-" + String.format(Locale.ENGLISH, "%02d", dayOfMonth);
                tvTransferDate.setText(selectedDate);
                Toast.makeText(MoneyTransferActivity.this, selectedDate, Toast.LENGTH_SHORT).show();
            }
        }, Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]) - 1, Integer.parseInt(dateArr[2])).show();
    }

    private boolean checkData() {
        if (transferRecord.getTransferInAccountId() == transferRecord.getTransferOutAccountId()) {
            Toast.makeText(MoneyTransferActivity.this,
                    getResources().getString(R.string.ab_check_error_same_account), Toast.LENGTH_SHORT).show();
            return false;
        }
        String transferAmount = this.etTransferAmount.getText().toString().trim();
        if (transferAmount.isEmpty()) {
            Toast.makeText(MoneyTransferActivity.this,
                    getResources().getString(R.string.ab_check_error_amount_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        transferRecord.setAmount(Double.parseDouble(transferAmount));
        transferRecord.setDate(tvTransferDate.getText().toString());
        transferRecord.setRemark(etRemark.getText().toString());
        return true;
    }

    /**
     * 保存转账记录
     *
     * @param view 保存按钮
     */
    public void saveTransferInfo(View view) {
        if (!checkData()) {
            return;
        }
        boolean result = MoneyTransferHelper.createTransferRecord(transferRecord);
        if (result) {
            AccountHelper.updateAccountBalance(
                    outAccount.getAccountId(), outAccount.getBalance() - transferRecord.getAmount());
            AccountHelper.updateAccountBalance(
                    inAccount.getAccountId(), inAccount.getBalance() + transferRecord.getAmount());
            Toast.makeText(MoneyTransferActivity.this,
                    getResources().getString(R.string.ab_tip_transfer_money_success), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.i(TAG, "saveTransferInfo fail.");
            Toast.makeText(MoneyTransferActivity.this,
                    getResources().getString(R.string.ab_tip_operation_fail), Toast.LENGTH_SHORT).show();
        }
    }
}
