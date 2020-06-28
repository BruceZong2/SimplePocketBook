package com.zq.personal.accountbook.ui.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zq.personal.accountbook.R;
import com.zq.personal.accountbook.db.helper.AccountHelper;
import com.zq.personal.accountbook.db.helper.DetailUtil;
import com.zq.personal.accountbook.db.table.AccountTable;
import com.zq.personal.accountbook.db.util.AccountIdGenerator;
import com.zq.personal.accountbook.manager.BankManager;
import com.zq.personal.accountbook.model.AccountInfo;
import com.zq.personal.accountbook.model.IncomeOutcomeRecord;
import com.zq.personal.accountbook.util.Util;

/**
 * 账户新增/编辑页面
 *
 * @since 2020-06-10
 */
public class AccountEditActivity extends Activity {
    /**
     * 账户信息
     */
    private AccountInfo accountInfo;

    private TextView tvBankName;
    private EditText etAccountName;
    private EditText etAccountBalance;
    private TextView tvRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);
        this.setTitle(R.string.ab_menu_add_account);
        initView();
        initData();
    }

    private void initView() {
        this.tvBankName = this.findViewById(R.id.tv_bank_name);
        this.etAccountName = this.findViewById(R.id.et_account_name);
        this.etAccountBalance = this.findViewById(R.id.et_account_balance);
        this.tvRemark = this.findViewById(R.id.et_remark);
    }

    private void initData() {
        accountInfo = new AccountInfo();
        accountInfo.setTypeId(1); // 设置默认的账户类型
    }

    public void showBankList(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.ab_tip_select_issue_bank)
                .setItems(BankManager.getBankList().toArray(new String[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String issuerName = BankManager.getBankList().get(which);
                        accountInfo.setIssuerName(issuerName);
                        tvBankName.setText(issuerName);
                    }
                }).create().show();
    }

    /**
     * 保存按钮响应，新建卡片
     *
     * @param view 按钮
     */
    public void createNewCard(View view) {
        if (!checkAccountInfo()) {
            return;
        }
        accountInfo.setTypeId(AccountTable.TYPE_ID_DEBIT_CARD);
        accountInfo.setAccountId(AccountIdGenerator.instance().getNextAccountId());
        if (!AccountHelper.createAccount(accountInfo)) {
            showWarningTips(R.string.ab_tip_operation_fail);
        } else {
            if (accountInfo.getBalance() > 1E-7) {
                // 插入余额变更的数据
                IncomeOutcomeRecord balanceModifyRecord = new IncomeOutcomeRecord();
                balanceModifyRecord.setCategory(getResources().getString(R.string.ab_text_balance_modify));
                balanceModifyRecord.setDate(Util.getCurrentDate(Util.DATE_YMD_FORMAT));
                balanceModifyRecord.setAmount(accountInfo.getBalance());
                balanceModifyRecord.setAccountId(accountInfo.getAccountId());
                balanceModifyRecord.setAccountName(accountInfo.getAccountName());
                balanceModifyRecord.setRecordType(Util.RecordType.RECORD_TYPE_SAVINGS.getIndex());
                DetailUtil.addAccountDetailItem(balanceModifyRecord, "0.00");
            }
            Intent intent = new Intent(this, DebitCardManagerActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean checkAccountInfo() {
        if (TextUtils.isEmpty(this.accountInfo.getIssuerName())) {
            showWarningTips(R.string.ab_tip_select_issue_bank);
            return false;
        }
        if (TextUtils.isEmpty(this.etAccountName.getText())) {
            showWarningTips(R.string.ab_check_account_name_empty);
            return false;
        }
        accountInfo.setAccountName(this.etAccountName.getText().toString().trim());
        if (!this.etAccountBalance.getText().toString().trim().isEmpty()) {
            try {
                accountInfo.setBalance(Double.parseDouble(this.etAccountBalance.getText().toString().trim()));
            } catch (NumberFormatException e) {
                showWarningTips(R.string.ab_check_error_number_format);
                return false;
            }
        }
        accountInfo.setRemark(this.tvRemark.getText().toString().trim());
        accountInfo.setRemark(this.tvRemark.getText().toString().trim());
        return true;
    }

    private void showWarningTips(int resId) {
        Toast.makeText(this, getResources().getString(resId), Toast.LENGTH_SHORT).show();
    }
}