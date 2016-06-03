package io.github.easyuber.easyuber;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


import javax.net.ssl.HttpsURLConnection;


public class AuthActivity extends AppCompatActivity {
    private final String AUTH_URL = "https://login.uber.com/oauth/v2/authorize?client_id=" +
            UberAPIWrapper.getCLIENT_ID()+"&response_type=code";
    UberAPI uber = new UberAPI();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent(this, MainActivity.class);

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
            boolean authComplete = false;

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                String summary = "<html><body></body></html>";
                view.loadData(summary, "text/html", null);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("?code=") && authComplete != true) {
                    Uri uri = Uri.parse(url);
                    final String authCode = uri.getQueryParameter("code");
                    Log.i("", "CODE : " + authCode);

                    authComplete = true;

                    new RequestAccessTokenTask().execute(new DataHolder(authCode, intent));
                    String summary = "<html><body></body></html>";
                    view.loadData(summary, "text/html", null);
                }else if(url.contains("?authToken=")){
                    Uri uri = Uri.parse(url);
                    final Boolean HAS_AUTH_TOKEN =
                            Boolean.parseBoolean(uri.getQueryParameter("authToken"));
                    if(HAS_AUTH_TOKEN){
                        Log.d("FINALLY GOT AUTH", "FINALLY GOT AUTH");
                    }

                }
                else if(url.contains("error=access_denied")){
                    Log.i("", "ACCESS_DENIED_HERE");
                    authComplete = true;
                    Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        });
        webview.loadUrl(AUTH_URL);
    }
    private class RequestAccessTokenTask extends AsyncTask<DataHolder, Void, Map<String, String>>{
        @Override
        protected Map<String, String> doInBackground(DataHolder... params) {
            final String AUTH_CODE = params[0].getAuthCode();
            final Intent intent = params[0].getIntent();
            Log.d("AUTH_CODE", AUTH_CODE);
            Map<String,String> accessToken = null;

            try {
                accessToken = uber.getAccessToken(AUTH_CODE);
                Log.d("ACCESS TOKEN", accessToken.get("access_token"));

                intent.putExtra("access_token", accessToken.get("access_token"));
                intent.putExtra("refresh_token", accessToken.get("refresh_token"));

                startActivity(intent);
                finish();
            } catch (IOException e) {
                Log.e("IOEXCEPTION in getAuth", e.toString());

                for(StackTraceElement el:e.getStackTrace()){
                    Log.e("IOEXCEPTION in getAuth", el.toString());
                }

            } catch (JSONException e) {
                Log.e("JSON in getAuth", e.toString());

                for(StackTraceElement el:e.getStackTrace()){
                    Log.e("JSON in getAuth", el.toString());
                }
            }

            return accessToken;
        }
    }
}
class DataHolder{
    public DataHolder(String authCode, Intent intent) {
        this.authCode = authCode;
        this.intent = intent;
    }

    public String getAuthCode() {
        return authCode;
    }

    public Intent getIntent() {
        return intent;
    }

    private String authCode;
    private Intent intent;
}
