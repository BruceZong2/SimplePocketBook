package com.zq.personal.accountbook.ui.provider;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zq.personal.accountbook.R;
import com.zq.personal.accountbook.model.AccountInfo;
import com.zq.personal.accountbook.util.CommonConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 账号列表数据源
 *
 * @since 2020-06-07
 */
public class AccountDataAdapter extends BaseAdapter {
    private static final String TAG = CommonConstants.LOG_TAG + AccountDataAdapter.class.getSimpleName();
    private List<AccountInfo> accountInfoList = new ArrayList<>();
    private Context context;

    public AccountDataAdapter(Context context) {
        this.context = context;
    }

    /**
     * 设置列表数据
     *
     * @param accountInfoList 账户信息列表
     */
    public void setAccountInfoList(List<AccountInfo> accountInfoList) {
        this.accountInfoList.clear();
        if (accountInfoList == null || accountInfoList.isEmpty()) {
            return;
        }
        this.accountInfoList.addAll(accountInfoList);
    }

    @Override
    public int getCount() {
        return accountInfoList == null ? 0 : accountInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < 0 || position > accountInfoList.size() - 1) {
            Log.i(TAG, "getItem, position is out of index, position=" + position);
            return null;
        }
        return accountInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position < 0 || position > accountInfoList.size() - 1) {
            Log.i(TAG, "getView, position is out of index, position=" + position);
            return null;
        }
        AccountInfo accountInfo = accountInfoList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.account_list_item, null);
            viewHolder = new ViewHolder(view);
            viewHolder.refreshData(accountInfo);
            view.setTag(viewHolder);
            return view;
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.refreshData(accountInfo);
            return convertView;
        }
    }

    /**
     * item载体，循环利用view
     */
    private static class ViewHolder {
        TextView tvTitle;
        TextView tvDesc;
        TextView tvBalance;
        ImageView ivIcon;

        private ViewHolder(View view) {
            this.tvTitle = view.findViewById(R.id.tv_item_title);
            this.tvDesc = view.findViewById(R.id.tv_item_desc);
            this.tvBalance = view.findViewById(R.id.tv_item_balance);
            this.ivIcon = view.findViewById(R.id.iv_item_icon);
        }

        private void refreshData(AccountInfo accountInfo) {
            if (accountInfo == null) {
                return;
            }
            this.tvTitle.setText(accountInfo.getAccountName());
            this.tvBalance.setText(String.valueOf(accountInfo.getBalance()));
            if (TextUtils.isEmpty(accountInfo.getRemark())) {
                this.tvDesc.setVisibility(View.GONE);
            } else {
                this.tvDesc.setVisibility(View.VISIBLE);
                this.tvDesc.setText(accountInfo.getRemark());
            }
        }
    }
}
