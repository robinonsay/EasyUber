package io.github.easyuber.easyuber;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by robinonsay on 6/3/16.
 */
public class UberAPI {
    private final String TOKEN_URL = "https://login.uber.com/oauth/v2/token";

    private final String REDIRECT_URL = "http://localhost?authToken=true";

    private final String REVOKE_URL = "https://login.uber.com/oauth/revoke";

    private final String SANDBOX_URL = "https://sandbox-api.uber.com";

    private final String PRODUCTS_ENDPOINT = "/v1/products";

    private JSONObject httpsPOSTRequest(URL url, String urlParams)throws IOException, JSONException {
        HttpsURLConnection tokenConnection = (HttpsURLConnection) url.openConnection();

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

        return new JSONObject(responseOut.toString());
    }

    private JSONObject httpsGETRequest(URL url, String urlParams)throws IOException, JSONException {
        HttpsURLConnection tokenConnection = (HttpsURLConnection) url.openConnection();

        tokenConnection.setRequestMethod("GET");
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

        return new JSONObject(responseOut.toString());
    }

    public Map<String, String> getAccessToken(final String AUTH_CODE)
            throws IOException, JSONException {
        URL url = new URL(TOKEN_URL);

        String urlParams = "client_secret="+UberAPIWrapper.getClientSecret()+
                "&client_id="+UberAPIWrapper.getCLIENT_ID()+
                "&grant_type=authorization_code&redirect_uri="+REDIRECT_URL+
                "&code="+AUTH_CODE;

        JSONObject jsonOut = httpsPOSTRequest(url, urlParams);
        Map tokenMap = new HashMap<String, String>();
        if(jsonOut.has("access_token") && jsonOut.has("refresh_token")) {
            tokenMap.put("access_token", jsonOut.getString("access_token"));
            tokenMap.put("refresh_token", jsonOut.getString("refresh_token"));
        }
        return tokenMap;
    }

    public Map<String, String> refreshAccessToken(final String REFRESH_TOKEN)throws IOException, JSONException {
        URL url = new URL(TOKEN_URL);

        String urlParams = "client_secret="+UberAPIWrapper.getClientSecret()+
                "&client_id="+UberAPIWrapper.getCLIENT_ID()+
                "&grant_type=refresh_token&redirect_uri="+REDIRECT_URL+
                "&refresh_token="+REFRESH_TOKEN;

        JSONObject jsonOut = httpsPOSTRequest(url, urlParams);
        Map tokenMap = new HashMap<String, String>();
        if(jsonOut.has("access_token")) {
            tokenMap.put("access_token", jsonOut.getString("access_token"));
        }
        return tokenMap;
    }
    public ArrayList<Map<String,String>> getProducts(double latitude, double longitude)throws IOException, JSONException{
        ArrayList products = new ArrayList<Map<String,String>>();
        URL url = new URL(SANDBOX_URL+PRODUCTS_ENDPOINT);
        String urlParams = "latitude="+latitude+"&longitude="+longitude;
        JSONObject result = httpsGETRequest(url,urlParams);
        JSONArray productsJSONArray = result.getJSONArray("products");
        for(int i = 0; i<productsJSONArray.length(); i++){
            Map map = new HashMap<String,String>();
            JSONObject json = productsJSONArray.getJSONObject(i);
            map.put("capacity",json.getInt("capacity"));
            map.put("description",json.getString("description"));
            map.put("distance_unit",json.getJSONObject("price_details").getString("distance_unit"));
            map.put("cost_per_minute",json.getJSONObject("price_details").getDouble("cost_per_minute"));
            map.put("minimum",json.getJSONObject("price_details").getDouble("minimum"));
            map.put("cost_per_distance",json.getJSONObject("price_details").getDouble("cost_per_distance"));
            map.put("base",json.getJSONObject("price_details").getDouble("base"));
            map.put("cancellation_fee",json.getJSONObject("price_details").getDouble("cancellation_fee"));
            map.put("currency_code",json.getJSONObject("price_details").getString("currency_code"));
            map.put("display_name",json.getString("display_name"));
            map.put("shared",json.getBoolean("shared"));

            products.add(map);
        }
        return products;
    }
    public void revokeAccessToken(final String ACCESS_TOKEN) throws IOException, JSONException{
        URL url = new URL(REVOKE_URL);

        String urlParams = "client_secret="+UberAPIWrapper.getClientSecret()+
                "&client_id="+UberAPIWrapper.getCLIENT_ID()+
                "&token="+ACCESS_TOKEN;
        httpsPOSTRequest(url, urlParams);
    }

}
