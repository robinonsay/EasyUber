package io.github.easyuber.easyuber;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * EasyUber
 * Created by robinonsay on 6/3/16.
 */
public class UberAPI {
    private final String TOKEN_URL = "https://login.uber.com/oauth/v2/token";

    private final String REDIRECT_URL = "http://localhost?authToken=true";

    private final String REVOKE_URL = "https://login.uber.com/oauth/revoke";

    private final String SANDBOX_URL = "https://sandbox-api.uber.com";

    private final String UBER_API_URL = "https://api.uber.com";

    private final String PRODUCTS_ENDPOINT = "/v1/products";

    private final String ETA_ENDPOINT = "/v1/estimates/time";

    private final String REQUEST_ENDPOINT = "/v1/requests";

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

    private JSONObject httpsPOSTRequest(URL url, JSONObject params, String accessToken)throws IOException, JSONException {

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty( "Accept", "*/*" );
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("USER-AGENT", "Mozilla/5.0");
        conn.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(params.toString());
        wr.flush();

        int responseCode = conn.getResponseCode();

        String output = "Request URL " + url;
        output += "\n" + "Request Params " + params.toString();
        output += "\n" + "Response Code " + responseCode;
        Log.d("OUTPUT", output);
        Log.d("SERVER MESSAGE", conn.getResponseMessage());
        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"));
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

    private JSONObject httpsGETRequest(String urlS, String urlParams, String accessToken)throws IOException, JSONException {
        URL url = new URL(urlS + "?" + urlParams);
        HttpsURLConnection tokenConnection = (HttpsURLConnection) url.openConnection();
        tokenConnection.setRequestMethod("GET");
        tokenConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        tokenConnection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
        tokenConnection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");

        int responseCode = tokenConnection.getResponseCode();

        String output = "Request URL " + url;
        output += "\n" + "Request Params " + urlParams;
        output += "\n" + "Response Code " + responseCode;
        Log.d("OUTPUT", output);
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

    public Map<String, String> callUber(String productID, String startLat, String startLong,
                                        String endLat, String endLong, String accessToken)throws IOException, JSONException{
        URL url = new URL(SANDBOX_URL+REQUEST_ENDPOINT);

//        String urlParams = "product_id="+productID+
//                "&start_latitude="+startLat+
//                "&start_longitude="+startLong+
//                "&end_latitude="+endLat+
//                "&end_longitude="+endLong;
        JSONObject urlParams = new JSONObject();
        urlParams.put("product_id", productID);
        urlParams.put("start_latitude", startLat);
        urlParams.put("start_longitude", startLong);
        urlParams.put("end_latitude", endLat);
        urlParams.put("end_longitude", endLong);

        JSONObject jsonRequestOut = httpsPOSTRequest(url,urlParams,accessToken);
        Map response = new HashMap<>();
        if(jsonRequestOut.has("request_id")) {
            response.put("request_id", jsonRequestOut.getString("request_id"));
            response.put("status", jsonRequestOut.getString("status"));
            response.put("vehicle", jsonRequestOut.getString("vehicle"));
            response.put("driver", jsonRequestOut.getString("driver"));
            response.put("location", jsonRequestOut.getString("location"));
            response.put("eta", jsonRequestOut.getString("eta"));
            response.put("surge_multiplier", jsonRequestOut.getString("surge_multiplier"));
        }

        return response;
    }

    public ArrayList<Map<String,String>> getProducts
            (double latitude, double longitude, String accessToken)throws IOException, JSONException{
        ArrayList products = new ArrayList<>();
        String url = SANDBOX_URL+PRODUCTS_ENDPOINT;
        String urlParams = "latitude="+latitude+"&longitude="+longitude;
        JSONObject result = httpsGETRequest(url,urlParams, accessToken);
        JSONArray productsJSONArray = result.getJSONArray("products");
        for(int i = 0; i<productsJSONArray.length(); i++){
            Map map = new HashMap<>();
            JSONObject json = productsJSONArray.getJSONObject(i);
            map.put("capacity",json.getString("capacity"));
            map.put("description",json.getString("description"));
            map.put("distance_unit",json.getJSONObject("price_details").getString("distance_unit"));
            map.put("cost_per_minute",json.getJSONObject("price_details").getString("cost_per_minute"));
            map.put("minimum",json.getJSONObject("price_details").getString("minimum"));
            map.put("cost_per_distance",json.getJSONObject("price_details").getString("cost_per_distance"));
            map.put("base",json.getJSONObject("price_details").getString("base"));
            map.put("cancellation_fee",json.getJSONObject("price_details").getString("cancellation_fee"));
            map.put("currency_code",json.getJSONObject("price_details").getString("currency_code"));
            map.put("display_name",json.getString("display_name"));
            map.put("product_id", json.getString("product_id"));
            map.put("shared",json.getString("shared"));
            Map eta = getETA(latitude,longitude, json.getString("product_id"), accessToken);
            if(eta != null){
                map.putAll(eta);
            }
            products.add(map);
        }
        return products;
    }

    public Map<String,String> getETA(double latitude, double longitude, String productID, String accessToken)throws IOException, JSONException{
        String url = SANDBOX_URL+ETA_ENDPOINT;
        String urlParams = "start_latitude="+latitude+"&start_longitude="+longitude+"&product_id="+productID;
        JSONArray result = httpsGETRequest(url,urlParams, accessToken).getJSONArray("times");
        if(result.length() > 0) {
            JSONObject product = result.getJSONObject(0);
            Map map = new HashMap<String,String>();
            if(product.has("estimate")) {
                map.put("estimate", product.getString("estimate"));
            }
            return map;
        }
        return null;
    }

    public void revokeAccessToken(final String ACCESS_TOKEN) throws IOException, JSONException{
        URL url = new URL(REVOKE_URL);

        String urlParams = "client_secret="+UberAPIWrapper.getClientSecret()+
                "&client_id="+UberAPIWrapper.getCLIENT_ID()+
                "&token="+ACCESS_TOKEN;
        httpsPOSTRequest(url, urlParams);
    }

}
