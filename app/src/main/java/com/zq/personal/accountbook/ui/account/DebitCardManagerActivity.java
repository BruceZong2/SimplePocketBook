package com.zq.personal.accountbook.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zq.personal.accountbook.R;
import com.zq.personal.accountbook.db.helper.AccountHelper;
import com.zq.personal.accountbook.db.table.AccountTable;
import com.zq.personal.accountbook.model.AccountInfo;
import com.zq.personal.accountbook.ui.BaseActivity;
import com.zq.personal.accountbook.ui.provider.DebitCardsDataAdapter;
import com.zq.personal.accountbook.util.CommonConstants;

import java.util.List;

/**
 * 储蓄卡管理页面
 */
public class DebitCardManagerActivity extends BaseActivity {
    private static final String TAG = DebitCardManagerActivity.class.getSimpleName();
    private ListView lvDebitCard;
    private DebitCardsDataAdapter debitCardsDataAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_debit_manager);
        initView();
        initData();
    }

    private void initView() {
        this.lvDebitCard = this.findViewById(R.id.lv_debit_card);
    }

    private void initData() {
        List<AccountInfo> debitCardList = AccountHelper.queryAccountByType(AccountTable.TYPE_ID_DEBIT_CARD);
        this.debitCardsDataAdapter = new DebitCardsDataAdapter(this);
        this.debitCardsDataAdapter.setAccountInfoList(debitCardList);
        this.lvDebitCard.setAdapter(debitCardsDataAdapter);
        this.lvDebitCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AccountInfo accountInfo = (AccountInfo) parent.getItemAtPosition(position);
                if (accountInfo == null) {
                    Log.i(TAG, "item click, account info is null");
                    return;
                }
                Intent intent = new Intent(DebitCardManagerActivity.this, AccountHistoryActivity.class);
                intent.putExtra(CommonConstants.BUNDLE_KEY_ACCOUNT_ID, accountInfo.getAccountId());
                startActivity(intent);
            }
        });
    }
}
