package com.zq.personal.accountbook.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.zq.personal.accountbook.R;
import com.zq.personal.accountbook.db.helper.AccountHelper;
import com.zq.personal.accountbook.db.table.AccountTable;
import com.zq.personal.accountbook.model.AccountInfo;
import com.zq.personal.accountbook.ui.BaseActivity;
import com.zq.personal.accountbook.ui.provider.AccountDataAdapter;
import com.zq.personal.accountbook.util.CommonConstants;
import com.zq.personal.accountbook.util.Util;

import java.util.List;

/**
 * 账户管理页面
 *
 * @since 2020-06-07
 */
public class AccountManagerActivity extends BaseActivity {
    private static final String TAG = CommonConstants.LOG_TAG + AccountManagerActivity.class.getSimpleName();
    private TextView tvAccountDesc;
    private TextView tvAccountBalanceTotal;
    private ListView lvAccount;
    private AccountDataAdapter accountDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        this.initView();
        this.initData();
    }

    private void initView() {
        this.lvAccount = this.findViewById(R.id.lv_account);
        this.tvAccountDesc = this.findViewById(R.id.tv_account_desc);
        this.tvAccountBalanceTotal = this.findViewById(R.id.tv_account_balance_total);
    }

    private void initData() {
        List<AccountInfo> accountInfoList = AccountHelper.queryAllAccount(false);
        this.refreshAccountTotal(accountInfoList);
        this.tvAccountDesc.setText(getResources().getString(R.string.ab_label_all_account, accountInfoList.size()));
        this.accountDataAdapter = new AccountDataAdapter(this);
        this.accountDataAdapter.setAccountInfoList(accountInfoList);
        this.lvAccount.setAdapter(this.accountDataAdapter);
        this.lvAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AccountInfo accountInfo = (AccountInfo) parent.getItemAtPosition(position);
                if (accountInfo == null) {
                    Log.i(TAG, "item click, account info is null");
                    return;
                }
                if (accountInfo.getAccountId() == AccountTable.TYPE_ID_DEBIT_CARD_MAIN) {
                    Intent intent = new Intent(AccountManagerActivity.this, DebitCardManagerActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(AccountManagerActivity.this, AccountHistoryActivity.class);
                    intent.putExtra(CommonConstants.BUNDLE_KEY_ACCOUNT_ID, accountInfo.getAccountId());
                    startActivity(intent);
                }
            }
        });
    }

    private void refreshAccountTotal(List<AccountInfo> accountInfoList) {
        double total = 0.00;
        for (AccountInfo accountInfo : accountInfoList) {
            total += accountInfo.getBalance();
        }
        String asset = getResources().getString(R.string.ab_label_total_asset)
                + Util.formatMoney(String.valueOf(total));
        this.tvAccountBalanceTotal.setText(asset);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pop_menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.pop_menu_settings, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_add_account) {
            Intent intent = new Intent(this, AccountEditActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.menu_item_transfer_money) {
            Intent intent = new Intent(this, MoneyTransferActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
