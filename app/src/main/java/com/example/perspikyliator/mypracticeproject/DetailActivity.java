package com.example.perspikyliator.mypracticeproject;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.perspikyliator.mypracticeproject.adapter.CustomCashAdapter;
import com.example.perspikyliator.mypracticeproject.database.BankDatabase;
import com.example.perspikyliator.mypracticeproject.fragment.DialogShare;
import com.example.perspikyliator.mypracticeproject.model.BankInfo;
import com.example.perspikyliator.mypracticeproject.model.Cash;
import com.example.perspikyliator.mypracticeproject.model.SimpleDividerItemDecoration;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {

    private TextView tvTitle;
    private TextView tvInfo;
    private TextView tvLink;

    private LinearLayout mLinLay;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private BankDatabase mBankDatabase;
    private SQLiteDatabase mSQLiteDatabase;

    private ArrayList<Cash> mCashList = new ArrayList<>();
    private BankInfo mBankInfo = new BankInfo();

    private FloatingActionsMenu mFam;
    private FloatingActionButton mFabLink;
    private FloatingActionButton mFabMap;
    private FloatingActionButton mFabPhone;

    private String id;
    private DialogFragment df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        findViews();

        Intent intent = getIntent();
        id = intent.getStringExtra(MainActivity.TITLE_KEY);

        mBankInfo = setBankInfo(id);
        mCashList = setCashInfo(id);

        tvTitle.setText(mBankInfo.title);
        setTitle(mBankInfo.title);

        tvInfo.setText(mBankInfo.region + '\n'
                + "г. " + mBankInfo.city + '\n' + '\n'
                + "Тел.: " + mBankInfo.phone + '\n'
                + "Адрес: " + mBankInfo.address + '\n' + '\n'
                + "Страница на finance.ua:");
        tvLink.setText(mBankInfo.link);

        mAdapter = new CustomCashAdapter(getApplicationContext(), mCashList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        //doesn't work without handler.
        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mFam.isExpanded()) {
                    mLinLay.setAlpha((float) 0.5);
                } else {
                    mLinLay.setAlpha(1);
                }
            }
        }, 0);
        super.onStart();
    }

    public void findViews() {
        tvTitle = (TextView) findViewById(R.id.tvTitle_AD);
        tvInfo = (TextView) findViewById(R.id.tvCity_AD);
        tvLink = (TextView) findViewById(R.id.tvAddress_AD);
        tvLink.setOnClickListener(this);

        mLinLay = (LinearLayout) findViewById(R.id.ll_AD);

        mFam = (FloatingActionsMenu) findViewById(R.id.fam_AD);
        mFam.setOnFloatingActionsMenuUpdateListener(this);

        mFabLink = (FloatingActionButton) findViewById(R.id.fabLink_AD);
        mFabMap = (FloatingActionButton) findViewById(R.id.fabMap_AD);
        mFabPhone = (FloatingActionButton) findViewById(R.id.fabPhone_AD);
        mFabLink.setSize(FloatingActionButton.SIZE_MINI);
        mFabMap.setSize(FloatingActionButton.SIZE_MINI);
        mFabPhone.setSize(FloatingActionButton.SIZE_MINI);
        mFabLink.setOnClickListener(this);
        mFabMap.setOnClickListener(this);
        mFabPhone.setOnClickListener(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.rv_AD);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        mBankDatabase = new BankDatabase(this, BankDatabase.DATABASE_NAME, null, 1);
        mSQLiteDatabase = mBankDatabase.getReadableDatabase();
    }

    @Override
    public void onClick(View v) {
        if (v == tvLink || v == mFabLink) {
            String url = mBankInfo.link;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if (v == mFabPhone) {
            Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mBankInfo.phone));
            intentCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentCall);
        } else if (v == mFabMap) {
            if (checkNetworkState()) {
                Intent intent = new Intent(this, MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MainActivity.TITLE_KEY, id);
                startActivity(intent);
            } else {
                //Geocoder doesn't work without internet
                Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public BankInfo setBankInfo(String _title) {
        BankInfo ba = new BankInfo();
        Cursor cursor = mSQLiteDatabase.query(BankDatabase.BANK_TABLE, new String[]{BankDatabase.ID_COLUMN,
                        BankDatabase.TITLE_COLUMN, BankDatabase.REGION_COLUMN, BankDatabase.CITY_COLUMN,
                        BankDatabase.PHONE_COLUMN, BankDatabase.ADDRESS_COLUMN, BankDatabase.LINK_COLUMN},
                BankDatabase.ID_COLUMN + " LIKE" + "'%" + _title + "%'",null, null, null, null);
        cursor.moveToFirst();
        ba.title = cursor.getString(cursor.getColumnIndex(mBankDatabase.TITLE_COLUMN));
        ba.region = cursor.getString(cursor.getColumnIndex(mBankDatabase.REGION_COLUMN));
        ba.city = cursor.getString(cursor.getColumnIndex(mBankDatabase.CITY_COLUMN));
        ba.phone = cursor.getString(cursor.getColumnIndex(mBankDatabase.PHONE_COLUMN));
        ba.address = cursor.getString(cursor.getColumnIndex(mBankDatabase.ADDRESS_COLUMN));
        ba.link = cursor.getString(cursor.getColumnIndex(mBankDatabase.LINK_COLUMN));
        cursor.close();
        return ba;
    }

    public ArrayList<Cash> setCashInfo(String _title) {
        ArrayList<Cash> cl = new ArrayList<>();
        Cursor cursor = mSQLiteDatabase.query(BankDatabase.CASH_TABLE, new String[]{
                        BankDatabase.BANK_COLUMN, BankDatabase.CASH_COLUMN,
                        BankDatabase.ASK_COLUMN, BankDatabase.BID_COLUMN,
                        BankDatabase.ASK_INDEX_COLUMN, BankDatabase.BID_INDEX_COLUMN},
                        BankDatabase.BANK_COLUMN + " LIKE" + "'%" + _title + "%'",null, null, null, null);
        while (cursor.moveToNext()) {
            String curName = cursor.getString(cursor.getColumnIndex(mBankDatabase.CASH_COLUMN));
            Cursor cursor2 = mSQLiteDatabase.query(BankDatabase.CURRENCY_TABLE, new String[]{
                            BankDatabase.CURRENCY_ID, BankDatabase.CURRENCY_NAME},
                    BankDatabase.CURRENCY_ID + " LIKE" + "'%" + curName + "%'", null, null, null, null);
            cursor2.moveToFirst();
            Cash cash = new Cash();
            cash.cashName = cursor2.getString(cursor2.getColumnIndex(mBankDatabase.CURRENCY_NAME));
            cash.cashAsk = cursor.getString(cursor.getColumnIndex(mBankDatabase.ASK_COLUMN));
            cash.cashBid = cursor.getString(cursor.getColumnIndex(mBankDatabase.BID_COLUMN));
            cash.cashAskIndex = cursor.getString(cursor.getColumnIndex(mBankDatabase.ASK_INDEX_COLUMN));
            cash.cashBidIndex = cursor.getString(cursor.getColumnIndex(mBankDatabase.BID_INDEX_COLUMN));
            cl.add(cash);
            cursor2.close();
        }
        return cl;
    }

    @Override
    public void onMenuExpanded() {
        final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_expended_alpha);
        mLinLay.startAnimation(anim);
        mLinLay.setAlpha((float) 0.5);
    }

    @Override
    public void onMenuCollapsed() {
        final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_collapsed_alpha);
        mLinLay.startAnimation(anim);
        mLinLay.setAlpha(1);
    }

    public boolean checkNetworkState() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            df = new DialogShare(mBankInfo, mCashList);
            df.show(getFragmentManager(), "df");

        }
        return super.onOptionsItemSelected(item);
    }
}
