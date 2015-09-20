package com.example.perspikyliator.mypracticeproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.perspikyliator.mypracticeproject.R;
import com.example.perspikyliator.mypracticeproject.model.Cash;

import java.util.ArrayList;
import java.util.List;

public class ShareAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<Cash> mData;

    public ShareAdapter(Context _context, ArrayList<Cash> _data) {
        mContext = _context;
        mData = _data;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cash_share_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Cash cash = mData.get(position);
        viewHolder.tv1.setText(cash.cashName + "         ");
        viewHolder.tv2.setText(getRounded(cash.cashAsk) + "/" + getRounded(cash.cashBid));
        return convertView;
    }

    class ViewHolder {
        TextView tv1;
        TextView tv2;
        public ViewHolder(View _view) {
            tv1 = (TextView) _view.findViewById(R.id.tvCashName_CSI);
            tv2 = (TextView) _view.findViewById(R.id.tvCash_CSI);
        }
    }

    public String getRounded(String _number) {
        double newAsk = Double.parseDouble(_number);
        newAsk = newAsk * 100;
        int i = (int) Math.round(newAsk);
        newAsk = (double)i / 100;
        return new Double(newAsk).toString();
    }
}