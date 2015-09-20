package com.example.perspikyliator.mypracticeproject.loader;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.perspikyliator.mypracticeproject.MainActivity;
import com.example.perspikyliator.mypracticeproject.database.BankDatabase;
import com.example.perspikyliator.mypracticeproject.interfaces.CallbackBankInfoLoader;
import com.example.perspikyliator.mypracticeproject.model.BankArray;
import com.example.perspikyliator.mypracticeproject.model.BankInfo;
import com.example.perspikyliator.mypracticeproject.model.Cash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class InternetAsyncTask extends AsyncTask<String, Integer, BankArray>{

    public static final String CURRENCY_URL = "http://resources.finance.ua/ru/public/currency-cash.json";
    public static final int id = 12345;

    // message - shows which class has called this loader
    private String message;

    private BankDatabase mBankDatabase;
    private SQLiteDatabase mSQLiteDatabase;
    private JSONObject jsonObject;

    // index - true if date from JSON is newer
    private boolean index = false;

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    private CallbackBankInfoLoader mCallbackBankInfoLoader;
    private Context mContext;

    public InternetAsyncTask(CallbackBankInfoLoader _callbackBankInfoLoader, Context _context) {
        mCallbackBankInfoLoader = _callbackBankInfoLoader;
        mContext = _context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mBankDatabase = new BankDatabase(mContext, BankDatabase.DATABASE_NAME, null, 1);
        mSQLiteDatabase = mBankDatabase.getWritableDatabase();
    }

    @Override
    protected BankArray doInBackground(String... params) {
        message = params[0];
        BankArray bankArray = null;
        try {
            jsonObject = getJSONFromUrl(CURRENCY_URL);
            checkDate();
            switch (message) {
                // Loader was called from MainActivity - asyncTask just load the main information
                case MainActivity.APP:
                    bankArray = loadData();
                    break;
                // Loader was called from Service - setting data to database if needed
                case MainActivity.SERVICE:
                    if (!index) {
                        // Updating database only if date is newer!
                        createNotification();
                        publishProgress(-1);
                        bankArray = loadData();
                        setCurrencyTable();
                        setNewDate();
                        publishProgress(-2);
                    }
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bankArray;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (values[0] == -1)
            Toast.makeText(mContext.getApplicationContext(), "Database is rewriting. Please wait!", Toast.LENGTH_SHORT).show();
        else if (values[0] == -2)
            Toast.makeText(mContext.getApplicationContext(), "Database has been updated!", Toast.LENGTH_SHORT).show();
        else {
            mBuilder.setProgress(100, values[0], false);
            mNotifyManager.notify(id, mBuilder.build());
        }
    }

    @Override
    protected void onPostExecute(BankArray bankArray) {
        super.onPostExecute(bankArray);
        if (bankArray != null){
            mCallbackBankInfoLoader.onBankSuccess(bankArray);
        } else
            mCallbackBankInfoLoader.onBankFailure("Error parsing");
        if (!index && message.equals(MainActivity.SERVICE)) {
            mBuilder.setOngoing(false);
            mNotifyManager.notify(id, mBuilder.build());
        }
    }

    public void createNotification() {
        mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle("Downloading")
                .setContentText("Updating database")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setProgress(100, 0, false)
                .setOngoing(true);
        mNotifyManager.notify(id, mBuilder.build());
    }

    public JSONObject getJSONFromUrl(String _url) throws IOException, JSONException {

        URL url = new URL(_url);

        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setRequestProperty("Content-length", "0");
        c.setUseCaches(false);
        c.setAllowUserInteraction(false);
        c.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        String json = sb.toString();
        JSONObject jObj = new JSONObject(json);

        return jObj;
    }

    // Loading data from JSON
    public BankArray loadData() throws IOException, JSONException {

        if (!index && message.equals(MainActivity.SERVICE)) {
            mSQLiteDatabase.delete(BankDatabase.BANK_TABLE, null, null);
        }

        BankArray bankArray = new BankArray();
        bankArray.bankInfoArrayList = new ArrayList<>();

        JSONArray jsonArray = jsonObject.getJSONArray("organizations");

        for (int i = 0; i < jsonArray.length(); i++) {
            if (message.equals(MainActivity.SERVICE)) {
                int a = ((i + 1) * 100) / jsonArray.length();
                publishProgress(a);
            }
            BankInfo ba = getInfo(jsonArray.getJSONObject(i));
            bankArray.bankInfoArrayList.add(ba);
        }
        return bankArray;
    }

    public BankInfo getInfo(JSONObject _jsonObject) throws JSONException {

        BankInfo bankInfo = new BankInfo();
        bankInfo.cash = new ArrayList<>();

        JSONObject jsonRegObject = jsonObject.getJSONObject("regions");
        JSONObject jsonCitObject = jsonObject.getJSONObject("cities");
        String regionId = _jsonObject.getString("regionId");
        String cityId = _jsonObject.getString("cityId");

        bankInfo.id = _jsonObject.getString("id");
        bankInfo.title = _jsonObject.getString("title");
        bankInfo.region = jsonRegObject.getString(regionId);
        bankInfo.city = jsonCitObject.getString(cityId);
        bankInfo.phone = _jsonObject.getString("phone");
        bankInfo.address = _jsonObject.getString("address");
        bankInfo.link = _jsonObject.getString("link");

        JSONObject jsonCashObject = _jsonObject.getJSONObject("currencies");
        JSONArray names = jsonCashObject.names();

        for (int i = 0; i < names.length(); i++) {

            Cash cash = new Cash();
            cash.cashName = names.getString(i);
            JSONObject jsonCashChildObject = jsonCashObject.getJSONObject(cash.cashName);
            cash.cashAsk = jsonCashChildObject.getString("ask");
            cash.cashBid = jsonCashChildObject.getString("bid");
            bankInfo.cash.add(cash);
        }

        // Updating database only if loader was called from service
        if (!index && message.equals(MainActivity.SERVICE)) {
            setBankAndCashTables(bankInfo);
        }

        return bankInfo;
    }

    // Next 3 methods - Updating database
    public void setBankAndCashTables(BankInfo _bankInfo) {
        ContentValues newValues = new ContentValues();

        newValues.put(BankDatabase.ID_COLUMN, _bankInfo.id);
        newValues.put(BankDatabase.TITLE_COLUMN, _bankInfo.title);
        newValues.put(BankDatabase.REGION_COLUMN, _bankInfo.region);
        newValues.put(BankDatabase.CITY_COLUMN, _bankInfo.city);
        newValues.put(BankDatabase.PHONE_COLUMN, _bankInfo.phone);
        newValues.put(BankDatabase.ADDRESS_COLUMN, _bankInfo.address);
        newValues.put(BankDatabase.LINK_COLUMN, _bankInfo.link);
        mSQLiteDatabase.insert(BankDatabase.BANK_TABLE, null, newValues);
        newValues.clear();

        for (int i = 0; i < _bankInfo.cash.size(); i++) {
            Cash cash = _bankInfo.cash.get(i);
            String id = cash.cashName;
            String ask = cash.cashAsk;
            String bid = cash.cashBid;
            newValues.put(BankDatabase.BANK_COLUMN, _bankInfo.id);
            newValues.put(BankDatabase.CASH_COLUMN, id);
            newValues.put(BankDatabase.ASK_COLUMN, ask);
            newValues.put(BankDatabase.BID_COLUMN, bid);

            Cursor cursor = mSQLiteDatabase.query(BankDatabase.CASH_TABLE, new String[]{
                            BankDatabase.BANK_COLUMN, BankDatabase.CASH_COLUMN,
                            BankDatabase.ASK_COLUMN, BankDatabase.BID_COLUMN},
                            BankDatabase.BANK_COLUMN + " LIKE" + "'%" + _bankInfo.id + "%' AND " +
                            BankDatabase.CASH_COLUMN + " LIKE" + "'%" + cash.cashName + "%'",null, null, null, null);

            if (cursor.moveToFirst()) {
                double askD = Double.parseDouble(ask);
                double bidD = Double.parseDouble(bid);
                double askAlt = Double.parseDouble(cursor.getString(cursor.getColumnIndex(BankDatabase.ASK_COLUMN)));
                double bidAlt = Double.parseDouble(cursor.getString(cursor.getColumnIndex(BankDatabase.BID_COLUMN)));
                if (askD < askAlt)
                    newValues.put(BankDatabase.ASK_INDEX_COLUMN, "red");
                else
                    newValues.put(BankDatabase.ASK_INDEX_COLUMN, "green");
                if (bidD < bidAlt)
                    newValues.put(BankDatabase.BID_INDEX_COLUMN, "red");
                else
                    newValues.put(BankDatabase.BID_INDEX_COLUMN, "green");
                mSQLiteDatabase.update(BankDatabase.CASH_TABLE, newValues, BankDatabase.BANK_COLUMN + " = '" + _bankInfo.id + "' AND "
                                + BankDatabase.CASH_COLUMN + " = '" + cash.cashName + "'", null);
            } else {
                newValues.put(BankDatabase.ASK_INDEX_COLUMN, "green");
                newValues.put(BankDatabase.BID_INDEX_COLUMN, "green");
                mSQLiteDatabase.insert(BankDatabase.CASH_TABLE, null, newValues);
                cursor.close();
            }

            newValues.clear();
        }
    }

    private void setCurrencyTable() throws JSONException {
        mSQLiteDatabase.delete(BankDatabase.CURRENCY_TABLE, null, null);

        JSONObject jsonChildObject = jsonObject.getJSONObject("currencies");
        JSONArray names = jsonChildObject.names();

        ContentValues newValues = new ContentValues();

        for (int i = 0; i < names.length(); i++) {
            String id = names.getString(i);
            String value = jsonChildObject.getString(id);
            newValues.put(BankDatabase.CURRENCY_ID, id);
            newValues.put(BankDatabase.CURRENCY_NAME, value);
            mSQLiteDatabase.insert(BankDatabase.CURRENCY_TABLE, null, newValues);
            newValues.clear();
        }
    }

    private void setNewDate() throws JSONException {
        mSQLiteDatabase.delete(BankDatabase.DATE_TABLE, null, null);

        ContentValues newValues = new ContentValues();

        String date = jsonObject.getString("date");
        newValues.put(BankDatabase.DATA, date);
        mSQLiteDatabase.insert(BankDatabase.DATE_TABLE, null, newValues);
        newValues.clear();
    }

    // Checking date
    private void checkDate() throws JSONException {
        Cursor cursor = mSQLiteDatabase.query(mBankDatabase.DATE_TABLE, new String[]{
                BankDatabase.DATA}, null, null, null, null, null);
        String date1 = "";
        while (cursor.moveToNext()) {
            date1 = cursor.getString(cursor.getColumnIndex(mBankDatabase.DATA));
        }
        String date2 = jsonObject.getString("date");
        index = date1.equals(date2);
        cursor.close();
    }

}