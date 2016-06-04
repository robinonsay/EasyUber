package io.github.easyuber.easyuber;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        Log.d("EXTRAS", new Boolean(extras != null).toString());

        if (extras != null) {
            setContentView(R.layout.activity_home);

            final String ACCESS_TOKEN = extras.getString("access_token");
            final String REFRESH_TOKEN = extras.getString("refresh_token");

            TextView errorText = (TextView) findViewById(R.id.home_error_textview);
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            ListView productsList = (ListView) findViewById(R.id.products_listview);
            Button refresh = (Button) findViewById(R.id.refresh_button);


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
                final double LONGITUDE = location.getLongitude();
                final double LATITUDE = location.getLatitude();
                new GetProductsTask().execute(new Container(LATITUDE,LONGITUDE, ACCESS_TOKEN));
                refresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new GetProductsTask().execute(new Container(LATITUDE,LONGITUDE, ACCESS_TOKEN));
                    }
                });
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

    private class GetProductsTask extends AsyncTask<Container, Void, ProductsListAdapter>{

        @Override
        protected ProductsListAdapter doInBackground(Container... container) {
            Container params = container[0];
            ProductsListAdapter adapter = null;
            try {
                ArrayList products = uberAPI.getProducts(params.getLatitude(),params.getLongitude(),
                        params.getAccessToken());
                adapter = new ProductsListAdapter(MainActivity.this, products, params);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return adapter;
        }

        @Override
        protected void onPostExecute(ProductsListAdapter adapter) {
            final ListView lv1 = (ListView) findViewById(R.id.products_listview);
            lv1.setAdapter(adapter);
        }
    }


}

class Container{
    private double latitude;
    private double longitude;
    private String accessToken;

    public Container(double latitude, double longitude, String accessToken) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accessToken = accessToken;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAccessToken() {
        return accessToken;
    }
}

