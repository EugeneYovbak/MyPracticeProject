package com.example.perspikyliator.mypracticeproject.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.example.perspikyliator.mypracticeproject.database.BankDatabase;
import com.example.perspikyliator.mypracticeproject.interfaces.CallbackBankInfoLoader;
import com.example.perspikyliator.mypracticeproject.model.BankArray;
import com.example.perspikyliator.mypracticeproject.model.BankInfo;

import java.io.IOException;
import java.util.ArrayList;

public class DatabaseAsyncTask extends AsyncTask<Void, Void, BankArray> {

    private BankDatabase mBankDatabase;
    private SQLiteDatabase mSQLiteDatabase;

    private CallbackBankInfoLoader mCallbackBankInfoLoader;
    private Context mContext;

    public DatabaseAsyncTask(CallbackBankInfoLoader _callbackBankInfoLoader, Context _context) {
        mCallbackBankInfoLoader = _callbackBankInfoLoader;
        mContext = _context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mBankDatabase = new BankDatabase(mContext, BankDatabase.DATABASE_NAME, null, 1);
        mSQLiteDatabase = mBankDatabase.getReadableDatabase();
    }

    @Override
    protected BankArray doInBackground(Void... params) {
        BankArray bankArray = null;
        try {
            bankArray = loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bankArray;
    }

    @Override
    protected void onPostExecute(BankArray bankArray) {
        super.onPostExecute(bankArray);
        if (bankArray != null){
            mCallbackBankInfoLoader.onBankSuccess(bankArray);
        } else
            mCallbackBankInfoLoader.onBankFailure("Error parsing");
    }

    public BankArray loadData() throws IOException {
        BankArray bankArray = new BankArray();
        bankArray.bankInfoArrayList = new ArrayList<>();

        Cursor cursor = mSQLiteDatabase.query(mBankDatabase.BANK_TABLE, new String[]{BankDatabase.ID_COLUMN,
                BankDatabase.TITLE_COLUMN, BankDatabase.REGION_COLUMN, BankDatabase.CITY_COLUMN,
                BankDatabase.PHONE_COLUMN, BankDatabase.ADDRESS_COLUMN, BankDatabase.LINK_COLUMN},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            BankInfo bankInfo = new BankInfo();
            bankInfo.id = cursor.getString(cursor.getColumnIndex(mBankDatabase.ID_COLUMN));
            bankInfo.title = cursor.getString(cursor.getColumnIndex(mBankDatabase.TITLE_COLUMN));
            bankInfo.region = cursor.getString(cursor.getColumnIndex(mBankDatabase.REGION_COLUMN));
            bankInfo.city = cursor.getString(cursor.getColumnIndex(mBankDatabase.CITY_COLUMN));
            bankInfo.phone = cursor.getString(cursor.getColumnIndex(mBankDatabase.PHONE_COLUMN));
            bankInfo.address = cursor.getString(cursor.getColumnIndex(mBankDatabase.ADDRESS_COLUMN));
            bankInfo.link = cursor.getString(cursor.getColumnIndex(mBankDatabase.LINK_COLUMN));
            bankArray.bankInfoArrayList.add(bankInfo);
        }
        cursor.close();
        return bankArray;
    }
}
