package io.github.easyuber.easyuber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.util.*;

public class MainActivity extends AppCompatActivity {

//    AccountManager am = AccountManager.get(this);
//    Bundle options = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button uberLogin = (Button) findViewById(R.id.uber_login);
        uberLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(v.getContext(), AuthActivity.class);
                startActivity(loginIntent);
            }
        });

    }
}
