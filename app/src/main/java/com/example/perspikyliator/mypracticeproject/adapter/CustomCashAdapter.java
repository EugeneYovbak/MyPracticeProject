package com.example.perspikyliator.mypracticeproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.perspikyliator.mypracticeproject.R;
import com.example.perspikyliator.mypracticeproject.model.Cash;

import java.util.ArrayList;

public class CustomCashAdapter extends RecyclerView.Adapter<CustomCashAdapter.CustomViewHolder> {

    private final Context mContext;
    private final ArrayList<Cash> mData;

    public CustomCashAdapter(Context _context, ArrayList<Cash> _data) {
        mContext = _context;
        mData = _data;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup _viewGroup, int _viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.cash_item, _viewGroup, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder _customViewHolder, int i) {
        _customViewHolder.onBind();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        ImageView iv1;
        ImageView iv2;

        public CustomViewHolder(View itemView) {
            super(itemView);
            tv1 = (TextView) itemView.findViewById(R.id.tvCash_CI);
            tv2 = (TextView) itemView.findViewById(R.id.tvAsk_CI);
            tv3 = (TextView) itemView.findViewById(R.id.tvBid_CI);

            iv1 = (ImageView) itemView.findViewById(R.id.ivAsk_CI);
            iv2 = (ImageView) itemView.findViewById(R.id.ivBid_CI);
        }

        public void onBind() {
            Cash cash = mData.get(getPosition());
            tv1.setText(cash.cashName);
            tv2.setText(cash.cashAsk);
            tv3.setText(cash.cashBid);
            if (cash.cashAskIndex.equals("green")) {
                tv2.setTextColor(mContext.getResources().getColor(R.color.green));
                iv1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_green_arrow_up));
            } else {
                tv2.setTextColor(mContext.getResources().getColor(R.color.red));
                iv1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_red_arrow_down));
            }
            if (cash.cashBidIndex.equals("green")) {
                tv3.setTextColor(mContext.getResources().getColor(R.color.green));
                iv2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_green_arrow_up));
            } else {
                tv3.setTextColor(mContext.getResources().getColor(R.color.red));
                iv2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_red_arrow_down));
            }
        }
    }
}
