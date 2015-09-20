package com.example.perspikyliator.mypracticeproject.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.perspikyliator.mypracticeproject.R;
import com.example.perspikyliator.mypracticeproject.adapter.ShareAdapter;
import com.example.perspikyliator.mypracticeproject.database.BankDatabase;
import com.example.perspikyliator.mypracticeproject.model.BankInfo;
import com.example.perspikyliator.mypracticeproject.model.Cash;

import java.util.ArrayList;

public class DialogShare extends DialogFragment implements View.OnClickListener {

    private BankDatabase mBankDatabase;
    private SQLiteDatabase mSQLiteDatabase;

    private Button btnShare;
    private ImageView mImageView;

    private BankInfo mBankInfo;
    private ArrayList<Cash> mCash;

    public DialogShare(BankInfo _bankInfo, ArrayList<Cash> _cash) {
        mBankInfo = _bankInfo;
        mCash = _cash;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setRetainInstance(true);
        return super.onCreateDialog(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        mBankDatabase = new BankDatabase(getActivity(), BankDatabase.DATABASE_NAME, null, 1);
        mSQLiteDatabase = mBankDatabase.getReadableDatabase();

        View view = inflater.inflate(R.layout.dialog_share, null);

        mImageView = (ImageView) view.findViewById(R.id.iv_DS);
        mImageView.setImageBitmap(setShareImage());

        btnShare = (Button) view.findViewById(R.id.btn_share_DS);
        btnShare.setOnClickListener(this);



        return view;
    }

    public Bitmap setShareImage() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bitmap_layout,
                null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle_BL);
        tvTitle.setText(mBankInfo.title);

        TextView tvCity = (TextView) view.findViewById(R.id.tvCity_BL);
        tvCity.setText(mBankInfo.region + '\n' + mBankInfo.city);

        ListView mListView = (ListView) view.findViewById(R.id.lv_BL);
        ShareAdapter mAdapter = new ShareAdapter(getActivity(), setCash(mCash));
        mListView.setAdapter(mAdapter);

        mListView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mListView.layout(0, 0, mListView.getMeasuredWidth() * 2, mListView.getMeasuredHeight() * (mCash.size()));

        mListView.setLayoutParams(new LinearLayout.LayoutParams(mListView.getMeasuredWidth() * 2, mListView.getMeasuredHeight() * (mCash.size())));

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        final Bitmap viewBitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(viewBitmap);
        view.draw(canvas);

        return viewBitmap;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    public ArrayList<Cash> setCash(ArrayList<Cash> _cash) {
        ArrayList<Cash> cashList = new ArrayList<>();
        for (int i = 0; i < _cash.size(); i++) {
            Cash cash = new Cash();
            Cursor cursor2 = mSQLiteDatabase.query(BankDatabase.CURRENCY_TABLE, new String[]{
                            BankDatabase.CURRENCY_ID, BankDatabase.CURRENCY_NAME},
                    BankDatabase.CURRENCY_NAME + " LIKE" + "'%" + _cash.get(i).cashName + "%'", null, null, null, null);
            cursor2.moveToFirst();
            cash.cashName = cursor2.getString(cursor2.getColumnIndex(mBankDatabase.CURRENCY_ID));
            cash.cashAsk = _cash.get(i).cashAsk;
            cash.cashBid = _cash.get(i).cashBid;
            cursor2.close();
            cashList.add(cash);
        }
        return cashList;
    }

    @Override
    public void onClick(View v) {
        String pathToBmp = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), setShareImage(),"title", null);
        Uri bmpUri = Uri.parse(pathToBmp);
        final Intent intent = new Intent(     android.content.Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        intent.setType("image/png");
        startActivity(intent);
    }
}
