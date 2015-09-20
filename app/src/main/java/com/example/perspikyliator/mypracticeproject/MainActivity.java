package com.example.perspikyliator.mypracticeproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.example.perspikyliator.mypracticeproject.adapter.CustomBankInfoAdapter;
import com.example.perspikyliator.mypracticeproject.database.BankDatabase;
import com.example.perspikyliator.mypracticeproject.interfaces.CallbackBankInfoLoader;
import com.example.perspikyliator.mypracticeproject.loader.DatabaseAsyncTask;
import com.example.perspikyliator.mypracticeproject.loader.InternetAsyncTask;
import com.example.perspikyliator.mypracticeproject.model.BankArray;
import com.example.perspikyliator.mypracticeproject.model.BankInfo;
import com.example.perspikyliator.mypracticeproject.service.LoadService;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements CallbackBankInfoLoader, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TITLE_KEY = "title";
    public static final String SERVICE = "title";
    public static final String APP = "application";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    BankArray bankArray = new BankArray();

    private BankDatabase mBankDatabase;
    private SQLiteDatabase mSQLiteDatabase;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        setRecyclerView();

        intent = new Intent(this, LoadService.class);
        startService(intent);

        alarmStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alarmStart();
    }

    public void findViews() {
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_AM);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mBankDatabase = new BankDatabase(this, BankDatabase.DATABASE_NAME, null, 1);
        mSQLiteDatabase = mBankDatabase.getReadableDatabase();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_AM);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    public void setRecyclerView() {
        Cursor cursor = mSQLiteDatabase.query(mBankDatabase.DATE_TABLE, new String[]{
                BankDatabase.DATA}, null, null, null, null, null);

        if (checkNetworkState()) {
            new InternetAsyncTask(this, this).execute(APP);
        } else if (cursor.moveToNext()){
            new DatabaseAsyncTask(this, this).execute();
        } else {
            Toast.makeText(this, "Database is empty", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    @Override
    public void onBankSuccess(BankArray _bankArray) {
        bankArray = _bankArray;
        mAdapter = new CustomBankInfoAdapter(getApplicationContext(), bankArray.bankInfoArrayList);
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBankFailure(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public boolean checkNetworkState() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String _newText) {
        BankArray ba = filterBankArray(_newText);
        mAdapter = new CustomBankInfoAdapter(getApplicationContext(), ba.bankInfoArrayList);
        mRecyclerView.setAdapter(mAdapter);
        return false;
    }

    public BankArray filterBankArray(String _text) {
        BankArray newBankArray = new BankArray();
        newBankArray.bankInfoArrayList = new ArrayList<>();
        for (int i = 0; i < bankArray.bankInfoArrayList.size(); i++) {
            BankInfo ba = bankArray.bankInfoArrayList.get(i);
            String title = ba.title;
            String region = ba.region;
            String city = ba.city;
            if (title.toLowerCase().contains(_text) || region.toLowerCase().contains(_text)
                    || city.toLowerCase().contains(_text)) {
                newBankArray.bankInfoArrayList.add(ba);
            }
        }
        return newBankArray;
    }

    public void alarmStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 10);

        PendingIntent pIntent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                30 * 60 * 1000, pIntent);
    }

    public void alarmStop() {
        PendingIntent pIntent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    @Override
    public void onRefresh() {
        setRecyclerView();
    }
}
