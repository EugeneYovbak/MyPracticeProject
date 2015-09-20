package com.example.perspikyliator.mypracticeproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.perspikyliator.mypracticeproject.database.BankDatabase;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final float ZOOM = 14;

    private GoogleMap mGoogleMap;

    private BankDatabase mBankDatabase;
    private SQLiteDatabase mSQLiteDatabase;

    private LatLng latLng;

    private String id;
    private String title;
    private String city;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        id = intent.getStringExtra(MainActivity.TITLE_KEY);

        mBankDatabase = new BankDatabase(this, BankDatabase.DATABASE_NAME, null, 1);
        mSQLiteDatabase = mBankDatabase.getReadableDatabase();

        getAddress(id);
        setTitle(title);

        try {
            latLng = getBankLocation(city, address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MapFragment mapFragment = (MapFragment)
                getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (mGoogleMap == null) return;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mGoogleMap.setMyLocationEnabled(true);
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker()));
        marker.showInfoWindow();
        goToLocation(latLng, ZOOM);
    }

    public LatLng getBankLocation(String _city, String _address) throws IOException {
        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(_city + ", " + _address, 1);
        if (list.size() == 0)
            list = gc.getFromLocationName(_city, 1);
        Address add = list.get(0);
        String loc = add.getLocality();
        Toast.makeText(MapsActivity.this, loc, Toast.LENGTH_SHORT).show();
        LatLng ll = new LatLng(add.getLatitude(),add.getLongitude());
        return ll;
    }

    public void getAddress(String _id) {
        Cursor cursor = mSQLiteDatabase.query(BankDatabase.BANK_TABLE, new String[]{BankDatabase.ID_COLUMN,
                        BankDatabase.TITLE_COLUMN, BankDatabase.REGION_COLUMN, BankDatabase.CITY_COLUMN,
                        BankDatabase.PHONE_COLUMN, BankDatabase.ADDRESS_COLUMN, BankDatabase.LINK_COLUMN},
                BankDatabase.ID_COLUMN + " LIKE" + "'%" + _id + "%'", null, null, null, null);
        cursor.moveToFirst();
        title = cursor.getString(cursor.getColumnIndex(mBankDatabase.TITLE_COLUMN));
        city = cursor.getString(cursor.getColumnIndex(mBankDatabase.CITY_COLUMN));
        address = cursor.getString(cursor.getColumnIndex(mBankDatabase.ADDRESS_COLUMN));
    }

    public void goToLocation(LatLng _latLng, float _zoom) {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(_latLng, _zoom);
        mGoogleMap.moveCamera(update);
    }
}