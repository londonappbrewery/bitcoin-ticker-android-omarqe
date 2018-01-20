package com.londonappbrewery.bitcointicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    // Constants:
    // TODO: Create the base URL
    private final String BASE_URL = "https://apiv2.bitcoinaverage.com/indices/global/ticker/BTC";

    // Member Variables:
    TextView mPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPriceTextView = (TextView) findViewById(R.id.priceLabel);
        Spinner spinner = (Spinner) findViewById(R.id.currency_spinner);

        // Create an ArrayAdapter using the String array and a spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, R.layout.spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // TODO: Set an OnItemSelected listener on the spinner
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                Object currency = parent.getItemAtPosition(i);
                Log.d("Bitcoin", "The selected item is: " + currency.toString().toUpperCase());

                letsDoSomeNetworking(BASE_URL + currency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(MainActivity.this, "Please select a currency.", Toast.LENGTH_SHORT);
            }
        });
    }

    // TODO: complete the letsDoSomeNetworking() method
    private void letsDoSomeNetworking(String url) {
        final String requestURL = url;

        new AsyncHttpClient().get(url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject o){
                Log.d("Bitcoin", "Request URL: " + requestURL);
                Log.d("Bitcoin", "Response: " + o.toString());

                try {
                    Double priceNow = o.getDouble("last");
                    updateUI(priceNow);
                } catch (JSONException e){
                    String eText = "Error parsing JSON. Details: " + e.toString();
                    Log.e("Clima", eText);

                    Toast.makeText(MainActivity.this, eText, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject o){
                Log.e("Bitcoin", "Failed to fetch data from the server ["+statusCode+"]. Details: " + e.toString());
                Log.d("Bitcoin", "Request URL: " + requestURL);
                Log.d("Bitcoin", "Response: " + o.toString());

                Toast.makeText(MainActivity.this, "Failed to fetch data from the server.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Double priceNow) {
        mPriceTextView.setText(priceNow.toString());
    }
}
