package com.zq.personal.accountbook.manager;

import com.zq.personal.accountbook.R;
import com.zq.personal.accountbook.app.AccountApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * 银行相关的数据管理
 */
public class BankManager {
    private static List<String> bankList;

    static {
        bankList = new ArrayList<>();
        bankList.add(AccountApplication.getContext().getString(R.string.ab_bank_boc));
        bankList.add(AccountApplication.getContext().getString(R.string.ab_bank_abc));
        bankList.add(AccountApplication.getContext().getString(R.string.ab_bank_icbc));
        bankList.add(AccountApplication.getContext().getString(R.string.ab_bank_ccb));
        bankList.add(AccountApplication.getContext().getString(R.string.ab_bank_bcm));
        bankList.add(AccountApplication.getContext().getString(R.string.ab_bank_cmb));
    }

    /**
     * 获取银行列表
     *
     * @return 银行列表
     */
    public static List<String > getBankList() {
        return bankList;
    }
}
