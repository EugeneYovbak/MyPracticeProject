package com.example.perspikyliator.mypracticeproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.perspikyliator.mypracticeproject.R;
import com.example.perspikyliator.mypracticeproject.model.BankInfo;
import com.example.perspikyliator.mypracticeproject.model.BankView;

import java.util.ArrayList;

public class CustomBankInfoAdapter extends RecyclerView.Adapter<CustomBankInfoAdapter.CustomViewHolder> {

    private final Context mContext;
    private final ArrayList<BankInfo> mData;

    public CustomBankInfoAdapter( Context _context, ArrayList<BankInfo> _data) {
        mContext = _context;
        mData = _data;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup _viewGroup, int _viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.bank_item, _viewGroup, false);
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


    class CustomViewHolder extends RecyclerView.ViewHolder{
        BankView view;

        public CustomViewHolder(View itemView) {
            super(itemView);
            view = (BankView) itemView.findViewById(R.id.view);
        }

        public void onBind() {
            BankInfo bankInfo = mData.get(getPosition());
            view.setTitle(bankInfo.title);
            view.setRegion(bankInfo.region);
            view.setCity(bankInfo.city);
            view.setPhone(bankInfo.phone);
            view.setAddress(bankInfo.address);
            view.bankInfo = bankInfo;
        }
    }
}
