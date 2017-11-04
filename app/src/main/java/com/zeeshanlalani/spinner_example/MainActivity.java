package com.zeeshanlalani.spinner_example;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    TextView spinnerText;
    List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<>();

        spinner = (Spinner) findViewById(R.id.spinner);
        spinnerText = (TextView) findViewById(R.id.spinnerText);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerText.setText(list.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        new GetData().execute();
    }

    class GetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("https://my-json-server.typicode.com/zzlalani/data/db");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                con.setReadTimeout(10000);
                con.setConnectTimeout(10000);

                InputStream in = con.getInputStream();
                con.connect();
                BufferedReader br = null;
                if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));

                } else {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                }

                JSONObject resp = bufferToJson(br);

                JSONArray courses = resp.getJSONArray("courses");
                for ( int i = 0; i < courses.length(); i++ ) {
                    JSONObject courseItem = courses.getJSONObject(i);
                    list.add(courseItem.getString("title"));
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }

        JSONObject bufferToJson (BufferedReader br) {
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject json = new JSONObject(sb.toString());
                return json;
            } catch (IOException e) {
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
