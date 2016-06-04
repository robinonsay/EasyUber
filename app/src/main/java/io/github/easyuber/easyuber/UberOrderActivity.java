package io.github.easyuber.easyuber;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UberOrderActivity extends AppCompatActivity {

    UberAPI uber = new UberAPI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber_order);
        final Bundle extras = getIntent().getExtras();

        final EditText ADDRESS = (EditText) findViewById(R.id.address_edit_text);
        final EditText COUNT = (EditText) findViewById(R.id.passengers_edit_text);
        final TextView price = (TextView) findViewById(R.id.price_text_view);
        final TextView info = (TextView) findViewById(R.id.info_textview);

        final Intent intent = new Intent(this, UberCalledActivity.class);

        Button calcPrice = (Button) findViewById(R.id.show_price_button);

        calcPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button callUber = (Button) findViewById(R.id.call_uber_button);

        assert callUber != null;
        callUber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = ADDRESS.getText().toString();
                Geocoder coder = new Geocoder(UberOrderActivity.this);
                List<Address> addressList;
                try {
                    addressList = coder.getFromLocationName(address,5);
                    if (address==null) {
                        return;
                    }
                    Address location=addressList.get(0);


                    Container container = new Container(extras,Double.toString(location.getLatitude()),
                            Double.toString(location.getLongitude()), intent);
                    Log.d("UBER", "BEING CALLED");
                    new CallUberTask().execute(container);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private class CalcPriceTask extends AsyncTask<Bundle, Void, Void>{

        @Override
        protected Void doInBackground(Bundle... params) {
            return null;
        }
    }

    private class CallUberTask extends AsyncTask<Container, Void, Void>{

        @Override
        protected Void doInBackground(Container... params) {
            Container container = params[0];
            String productId = container.getExtras().getString("product_id");
            String accessToken = container.getExtras().getString("access_token");
            String startLong = Double.toString(container.getExtras().getDouble("start_longitude"));
            String startLat = Double.toString(container.getExtras().getDouble("start_latitude"));
            Log.d("LONG AND LAT", container.getEndLat() + " : " + container.getEndLong());
            try {
                Map<String, String> response = uber.callUber(productId,startLat,startLong,
                        container.getEndLat(),container.getEndLong(),accessToken);
                if(response.containsKey("request_id")){
                    Log.d("UBER", "CALLED");
                    Intent intent = container.getIntent();
                    intent.putExtra("vehicle", response.get("vehicle"));
                    intent.putExtra("driver", response.get("driver"));
                    intent.putExtra("eta", response.get("eta"));
                    intent.putExtra("location", response.get("location"));
                    startActivity(intent);
                    finish();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class Container{
        private Bundle extras;
        private String endLat;
        private String endLong;
        private String count;
        private Intent intent;

        public String getCount() {
            return count;
        }

        public Intent getIntent() {
            return intent;
        }

        public Container(Bundle extras, String endLat, String endLong, Intent intent) {
            this.extras = extras;
            this.endLat = endLat;
            this.endLong = endLong;
            this.intent = intent;

        }

        public Container(Bundle extras, String endLat, String endLong, String count) {

            this.extras = extras;
            this.endLat = endLat;
            this.endLong = endLong;
            this.count = count;
        }

        public Bundle getExtras() {
            return extras;
        }

        public String getEndLat() {
            return endLat;
        }

        public String getEndLong() {
            return endLong;
        }
    }
}
