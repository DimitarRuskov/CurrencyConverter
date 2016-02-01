package com.example.dimitar.currencycalculator;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

public class CurrencyRateClient extends AsyncTask<String, String, HashMap> {
    private OnTaskCompleted listener;
    public CurrencyRateClient(OnTaskCompleted listener){
        this.listener=listener;
    }

    @Override
    protected HashMap doInBackground(String... params) {
        URL url;
        HttpURLConnection urlConnection = null;
        JSONObject response = new JSONObject();
        JSONObject rates = new JSONObject();

        try {
            url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            int responseCode = urlConnection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                String responseString = readStream(urlConnection.getInputStream());
                Log.v("CurrencyRateClient", responseString);
                response = new JSONObject(responseString);
            }else{
                Log.v("CurrencyRateClient", "Response code:"+ responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }

        try {
            rates = response.getJSONObject("rates");
        }
        catch (JSONException e) {
            Log.v("CurrencyRateClient", e.toString());
        }

        HashMap<String, Double> map = new HashMap<String, Double>();
        Iterator<?> keys = rates.keys();

        try {
            while (keys.hasNext()) {
                String key = (String) keys.next();
                map.put(key, rates.getDouble(key));
            }
        }
        catch (JSONException e) {
            Log.v("CurrencyRateClient", e.toString());
        }


        return map;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(HashMap result) {
        listener.onTaskCompleted(result);
    }
}
