package com.example.perspikyliator.mypracticeproject.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class BankDatabase extends SQLiteOpenHelper implements BaseColumns {

    public static final String DATABASE_NAME = "mydatabase.db";
    public static final int DATABASE_VERSION = 1;

    public static final String BANK_TABLE = "banks";
    public static final String CASH_TABLE = "cash";
    public static final String CURRENCY_TABLE = "currency";
    public static final String DATE_TABLE = "date";

    public static final String ID_COLUMN = "id";
    public static final String TITLE_COLUMN = "title";
    public static final String REGION_COLUMN = "region";
    public static final String CITY_COLUMN = "city";
    public static final String PHONE_COLUMN = "phone";
    public static final String ADDRESS_COLUMN = "address";
    public static final String LINK_COLUMN = "link";

    public static final String BANK_COLUMN = "bank";
    public static final String CASH_COLUMN = "cash";
    public static final String ASK_COLUMN = "ask";
    public static final String BID_COLUMN = "bid";
    public static final String ASK_INDEX_COLUMN = "askIndex";
    public static final String BID_INDEX_COLUMN = "bidIndex";

    public static final String CURRENCY_ID = "currencyId";
    public static final String CURRENCY_NAME = "currencyName";

    public static final String DATA = "data";

    private static final String DATABASE_CREATE_BANK_TABLE = "CREATE TABLE "
            + BANK_TABLE + " (" + ID_COLUMN + " TEXT, " + TITLE_COLUMN
            + " TEXT, " + REGION_COLUMN + " TEXT, " + CITY_COLUMN
            + " TEXT, " + PHONE_COLUMN + " TEXT, " + ADDRESS_COLUMN
            + " TEXT, " + LINK_COLUMN + " TEXT);";

    private static final String DATABASE_CREATE_CASH_TABLE = "CREATE TABLE "
            + CASH_TABLE + " (" + BANK_COLUMN
            + " TEXT, " + CASH_COLUMN + " TEXT, " + ASK_COLUMN
            + " TEXT, " + BID_COLUMN + " TEXT, " + ASK_INDEX_COLUMN
            + " TEXT, " + BID_INDEX_COLUMN + " TEXT);";

    private static final String DATABASE_CREATE_CURRENCY_TABLE = "CREATE TABLE "
            + CURRENCY_TABLE + " (" +  CURRENCY_ID
            + " TEXT, " + CURRENCY_NAME + " TEXT);";

    private static final String DATABASE_CREATE_DATE_TABLE = "CREATE TABLE "
            + DATE_TABLE + " (" +  DATA + " TEXT);";

    public BankDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public BankDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public BankDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_BANK_TABLE);
        db.execSQL(DATABASE_CREATE_CASH_TABLE);
        db.execSQL(DATABASE_CREATE_CURRENCY_TABLE);
        db.execSQL(DATABASE_CREATE_DATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_BANK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_CASH_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_CURRENCY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_DATE_TABLE);
        onCreate(db);
    }
}
