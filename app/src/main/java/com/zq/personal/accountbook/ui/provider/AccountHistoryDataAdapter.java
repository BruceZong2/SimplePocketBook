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
import com.zq.personal.accountbook.model.IncomeOutcomeRecord;
import com.zq.personal.accountbook.util.CommonConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 账号历史记录数据
 */
public class AccountHistoryDataAdapter extends BaseAdapter {
    private static final String TAG = CommonConstants.LOG_TAG + AccountHistoryDataAdapter.class.getSimpleName();
    private List<IncomeOutcomeRecord> recordList = new ArrayList<>();
    private Context context;

    public AccountHistoryDataAdapter(Context context) {
        this.context = context;
    }

    public void setRecordList(List<IncomeOutcomeRecord> records) {
        this.recordList.clear();
        if (records == null || records.isEmpty()) {
            return;
        }
        this.recordList.addAll(records);
    }

    @Override
    public int getCount() {
        return recordList == null ? 0 : recordList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < 0 || position > recordList.size() - 1) {
            Log.i(TAG, "getItem, position is out of index, position=" + position);
            return null;
        }
        return recordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position < 0 || position > recordList.size() - 1) {
            Log.i(TAG, "getView, position is out of index, position=" + position);
            return null;
        }
        IncomeOutcomeRecord accountInfo = recordList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.account_history_item, null);
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
        TextView tvAmount;
        TextView tvDate;
        ImageView ivIcon;

        private ViewHolder(View view) {
            this.tvTitle = view.findViewById(R.id.tv_item_title);
            this.tvDesc = view.findViewById(R.id.tv_item_desc);
            this.tvAmount = view.findViewById(R.id.tv_item_balance);
            this.ivIcon = view.findViewById(R.id.iv_item_icon);
            this.tvDate = view.findViewById(R.id.tv_item_date);
        }

        private void refreshData(IncomeOutcomeRecord record) {
            if (record == null) {
                return;
            }
            this.tvTitle.setText(record.getCategory());
            this.tvAmount.setText(String.valueOf(record.getAmount()));
            this.tvDate.setText(record.getDate());
            if (TextUtils.isEmpty(record.getRemark())) {
                this.tvDesc.setVisibility(View.GONE);
            } else {
                this.tvDesc.setVisibility(View.VISIBLE);
                this.tvDesc.setText(record.getRemark());
            }
        }
    }
}
