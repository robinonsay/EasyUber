package io.github.easyuber.easyuber;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.security.keystore.*;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.*;

public class MainActivity extends AppCompatActivity {

//    AccountManager am = AccountManager.get(this);
//    Bundle options = new Bundle();
    UberAPI uberAPI = new UberAPI();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        Log.d("EXTRAS", new Boolean(extras == null).toString());
        if (extras != null) {
            setContentView(R.layout.activity_home);

            TextView errorText = (TextView) findViewById(R.id.home_error_textview);
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            ListView productsList = (ListView) findViewById(R.id.products_listview);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                try {
                    ArrayList products = uberAPI.getProducts(latitude,longitude);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else{
                errorText.setText("Sorry, we could not get your location at this time");
            }
        }else {
            setContentView(R.layout.activity_main);

            final Button uberLogin = (Button) findViewById(R.id.uber_login);
            uberLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loginIntent = new Intent(v.getContext(), AuthActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
            });
        }
    }
}

