package io.github.easyuber.easyuber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UberCalledActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber_called);
        TextView uberCalledMessage = (TextView) findViewById(R.id.uber_called_textview);
        Bundle extras = getIntent().getExtras();
        if(!extras.getString("driver").equals("null")) {
            uberCalledMessage.setText("Your Uber has been called. " + extras.getString("driver") +
                    " in a " + extras.getString("vehicle")+"."+" They will arrive in "+extras.getString("eta")+"minutes");
        }else{
            uberCalledMessage.setText("Your Uber has been called.");
        }
    }
}
