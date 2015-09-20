package com.example.perspikyliator.mypracticeproject.model;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.perspikyliator.mypracticeproject.DetailActivity;
import com.example.perspikyliator.mypracticeproject.MainActivity;
import com.example.perspikyliator.mypracticeproject.MapsActivity;
import com.example.perspikyliator.mypracticeproject.R;

public class BankView extends LinearLayout implements View.OnClickListener {

    public BankInfo bankInfo;
    public Intent intent;

    private TextView tvTitle;
    private TextView tvRegion;
    private TextView tvCity;
    private TextView tvPhone;
    private TextView tvAddress;
    private Button btnLink;
    private Button btnMap;
    private Button btnPhone;
    private Button btnDetail;

    public BankView(Context context) {
        super(context);
        inflate(context, R.layout.bank_view, this);
        findViews();
        btnLink.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        btnPhone.setOnClickListener(this);
        btnDetail.setOnClickListener(this);
    }

    public BankView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.bank_view, this);
        findViews();
        btnLink.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        btnPhone.setOnClickListener(this);
        btnDetail.setOnClickListener(this);
    }

    public BankView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.bank_view, this);
        findViews();
        btnLink.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        btnPhone.setOnClickListener(this);
        btnDetail.setOnClickListener(this);
    }

    public void findViews(){
        tvTitle = (TextView) findViewById(R.id.tv_title_BV);
        tvRegion = (TextView) findViewById(R.id.tv_region_BV);
        tvCity = (TextView) findViewById(R.id.tv_city_BV);
        tvPhone = (TextView) findViewById(R.id.tv_phone_BV);
        tvAddress = (TextView) findViewById(R.id.tv_address_BV);

        btnLink = (Button) findViewById(R.id.btn_link_BV);
        btnMap = (Button) findViewById(R.id.btn_map_BV);
        btnPhone = (Button) findViewById(R.id.btn_phone_BV);
        btnDetail = (Button) findViewById(R.id.btn_detail_BV);
    }

    public void setTitle(String _title) {
        tvTitle.setText(_title);
    }

    public void setRegion(String _region) {
        tvRegion.setText(_region);
    }

    public void setCity(String _city) {
        tvCity.setText(_city);
    }

    public void setPhone(String _phone) {
        tvPhone.setText("Тел.: " + _phone);
    }

    public void setAddress(String _address) {
        tvAddress.setText("Адрес: " + _address);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_phone_BV:
                Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + bankInfo.phone));
                intentCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intentCall);
                break;
            case R.id.btn_link_BV:
                String url = bankInfo.link;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setData(Uri.parse(url));
                getContext().startActivity(i);
                break;
            case R.id.btn_detail_BV:
                intent = new Intent(getContext(), DetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MainActivity.TITLE_KEY, bankInfo.id);
                getContext().startActivity(intent);
                break;
            case R.id.btn_map_BV:
                if (checkNetworkState()) {
                    intent = new Intent(getContext(), MapsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(MainActivity.TITLE_KEY, bankInfo.id);
                    getContext().startActivity(intent);
                } else {
                    //Geocoder doesn't work without internet
                    Toast.makeText(getContext(), "No connection", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public boolean checkNetworkState() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
