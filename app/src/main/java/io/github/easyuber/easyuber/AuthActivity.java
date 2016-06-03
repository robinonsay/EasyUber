package io.github.easyuber.easyuber;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class AuthActivity extends AppCompatActivity {

    private final String CLIENT_ID = "AU9FqUuXs9m1-W62TXtrgU7wY008JI_T";
    private final String REDIRECT = "";
    private final String AUTH_URL = "https://login.uber.com/oauth/v2/authorize?client_id=" +
            CLIENT_ID+"&response_type=code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webview = new WebView(this);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(webview);


        webview.getSettings().setJavaScriptEnabled(true);

        final Activity activity = this;
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        webview.loadUrl(AUTH_URL);
        new ReceiveRedirectTask().execute();

    }

    private class ReceiveRedirectTask extends AsyncTask<Void, Void, Void>{
        private final int SOCKET_PORT = 3000;
        @Override
        protected Void doInBackground(Void... params) {
            String authCode = "";
            try {
                ServerSocket authSocket = new ServerSocket(SOCKET_PORT);
                Socket socket = authSocket.accept();
                InetAddress ip_client = socket.getInetAddress();
                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String request = reader.readLine();
                String[] requestParam = request.split(" ");
                Log.d("READER VALUE", request);
//                for(String param:requestParam){
//                    Log.d("GET PARAM", param);
//                }
                reader.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
//            long initTime = System.currentTimeMillis();
//            long timeout = 10000;
//            while (authCode == "" || timeout > System.currentTimeMillis() - initTime){
//
//            }
            return null;
        }
    }

}
