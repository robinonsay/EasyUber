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

    private final String CLIENT_ID = "AU9FqUuXs9m1-W62TXtrgU7wY008JI_T";
    private final String AUTH_URL = "https://login.uber.com/oauth/v2/authorize?client_id=" +
            CLIENT_ID+"&response_type=code";
    private final String TOKEN_URL = "https://login.uber.com/oauth/v2/token";

    private final String CLIENT_SECRET = "v4CpuSEAOAgB2x9SsO4L8rnU4-euw8qFuRlpgCNk";
    private final String REDIRECT_URL = "http://localhost?authToken=true";

    private Map<String, String> getAccessToken(final String AUTH_CODE) throws IOException, JSONException {
        URL url = new URL(TOKEN_URL);

        HttpsURLConnection tokenConnection = (HttpsURLConnection) url.openConnection();
        String urlParams = "client_secret="+CLIENT_SECRET+
                "&client_id="+CLIENT_ID+
                "&grant_type=authorization_code&redirect_uri="+REDIRECT_URL+
                "&code="+AUTH_CODE;

        tokenConnection.setRequestMethod("POST");
        tokenConnection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
        tokenConnection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
        tokenConnection.setDoOutput(true);

        DataOutputStream dStream = new DataOutputStream(tokenConnection.getOutputStream());

        dStream.writeBytes(urlParams);
        dStream.flush();
        dStream.close();

        int responseCode = tokenConnection.getResponseCode();

        String output = "Request URL " + url;
        output += "\n" + "Request Params " + urlParams;
        output += "\n" + "Response Code " + responseCode;

        BufferedReader br = new BufferedReader(
                new InputStreamReader(tokenConnection.getInputStream()));
        String line = "";
        StringBuilder responseOut = new StringBuilder();

        while((line = br.readLine()) != null){
            responseOut.append(line);
        }

        br.close();

        output += "\n" + responseOut.toString();
        Log.d("OUTPUT", output);
        JSONObject jsonOut = new JSONObject(responseOut.toString());
        Map tokenMap = new HashMap<String, String>();
        tokenMap.put("access_token", jsonOut.getString("access_token"));
        tokenMap.put("refresh_token", jsonOut.getString("refresh_token"));
        return tokenMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, HomeActivity.class);

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
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("?code=") && authComplete != true) {
                    Uri uri = Uri.parse(url);
                    final String authCode = uri.getQueryParameter("code");
                    Log.i("", "CODE : " + authCode);

                    authComplete = true;

                    new RequestAccessTokenTask().execute(authCode);

                    Toast.makeText(getApplicationContext(),"Authorization Code is: " +
                            authCode, Toast.LENGTH_SHORT).show();

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
    private class RequestAccessTokenTask extends AsyncTask<String, Void, Map<String, String>>{

        @Override
        protected Map<String, String> doInBackground(String... params) {
            final String AUTH_CODE = params[0];
            Log.d("AUTH_CODE", AUTH_CODE);
            Map<String,String> accessToken = null;

            try {
                accessToken = getAccessToken(AUTH_CODE);
                Log.d("ACCESS TOKEN", accessToken.get("access_token"));
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

        @Override
        protected void onPostExecute(Map<String, String> accessToken) {
            super.onPostExecute(accessToken);
        }
    }
}
