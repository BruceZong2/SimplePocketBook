package com.zq.personal.accountbook.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;
import android.widget.TextView;

import com.zq.personal.accountbook.R;
import com.zq.personal.accountbook.db.helper.AccountHelper;
import com.zq.personal.accountbook.db.helper.DetailUtil;
import com.zq.personal.accountbook.db.helper.MoneyTransferHelper;
import com.zq.personal.accountbook.model.AccountInfo;
import com.zq.personal.accountbook.model.IncomeOutcomeRecord;
import com.zq.personal.accountbook.model.MoneyTransferRecord;
import com.zq.personal.accountbook.ui.provider.AccountHistoryDataAdapter;
import com.zq.personal.accountbook.util.CommonConstants;
import com.zq.personal.accountbook.util.Util;

import java.util.List;
import java.util.Map;

/**
 * 账号收支历史详情记录
 */
public class AccountHistoryActivity extends Activity {
    private AccountInfo accountInfo;
    private ListView lvRecordList;
    private TextView tvBalance;
    private TextView tvTotalIncome;
    private TextView tvTotalOutcome;
    private AccountHistoryDataAdapter adapter;
    private Map<Integer, AccountInfo> accountInfoMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_account_history);
        getIntentData();
        if (accountInfo == null) {
            Util.showToast(this, R.string.ab_warn_account_not_exist);
            finish();
            return;
        }
        this.setTitle(accountInfo.getAccountName());
        initView();
        initData();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        int accountId = intent.getIntExtra(CommonConstants.BUNDLE_KEY_ACCOUNT_ID, -1);
        if (accountId != -1) {
            accountInfo = AccountHelper.queryAccountById(accountId);
        }
    }

    private void initView() {
        this.lvRecordList = findViewById(R.id.lv_account_record_list);
        this.tvBalance = findViewById(R.id.tv_total_balance);
        this.tvTotalIncome = findViewById(R.id.tv_total_income);
        this.tvTotalOutcome = findViewById(R.id.tv_total_outcome);
    }

    private void initData() {
        this.accountInfoMap = AccountHelper.queryAllAccountAsMap(true);
        List<IncomeOutcomeRecord> recordList = getRecords();
        this.dealTotalAmount(recordList);
        this.adapter = new AccountHistoryDataAdapter(this);
        this.adapter.setRecordList(recordList);
        this.lvRecordList.setAdapter(adapter);
    }

    private List<IncomeOutcomeRecord> getRecords() {
        String operation;
        AccountInfo otherAccount;
        List<IncomeOutcomeRecord> recordList = DetailUtil.queryRecordsByAccountId(accountInfo.getAccountId());
        List<MoneyTransferRecord> transferRecords =
                MoneyTransferHelper.queryTransferRecordsByAccountId(accountInfo.getAccountId());
        for (MoneyTransferRecord record : transferRecords) {
            IncomeOutcomeRecord dstRecord = new IncomeOutcomeRecord();
            dstRecord.setDate(record.getDate());
            dstRecord.setRemark(record.getRemark());
            if (record.getTransferOutAccountId() == accountInfo.getAccountId()) {
                otherAccount = accountInfoMap.get(record.getTransferInAccountId());
                if (otherAccount == null) {
                    continue;
                }
                operation = getResources().getString(R.string.ab_text_transfer_to, otherAccount.getAccountName());
                dstRecord.setCategory(operation);
                dstRecord.setAmount(-record.getAmount());
            } else {
                otherAccount = accountInfoMap.get(record.getTransferOutAccountId());
                if (otherAccount == null) {
                    continue;
                }
                operation = getResources().getString(R.string.ab_text_transfer_from, this.accountInfo.getAccountName());
                dstRecord.setCategory(operation);
                dstRecord.setAmount(record.getAmount());
            }
            recordList.add(dstRecord);
        }
        return recordList;
    }

    private void dealTotalAmount(List<IncomeOutcomeRecord> recordList) {
        double balance = 0.00;
        double income = 0.00;
        double outcome = 0.00;
        for (IncomeOutcomeRecord record : recordList) {
            double amount = record.getAmount();
            if (amount > 1E-7) {
                income += amount;
            } else {
                outcome += amount;
            }
            balance += amount;
        }
        this.tvBalance.setText(String.valueOf(balance));
        this.tvTotalIncome.setText(String.valueOf(income));
        this.tvTotalOutcome.setText(String.valueOf(outcome));
    }
}
