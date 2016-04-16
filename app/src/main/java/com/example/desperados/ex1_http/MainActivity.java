package com.example.desperados.ex1_http;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "HttpExample";
    private static EditText et_inputHTTP;
    private static TextView tv_outputContent;
    private static Button  btn_getHTTP;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_inputHTTP = (EditText)findViewById(R.id.et_inputHTTP);
        tv_outputContent = (TextView) findViewById(R.id.tv_outputContent);
        btn_getHTTP = (Button) findViewById(R.id.btn_getHTTP);
        
        btn_getHTTP.setFocusable(true);
        btn_getHTTP.setFocusableInTouchMode(true);
        btn_getHTTP.requestFocus();

        btn_getHTTP.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                       buttonClicked(v);
                    }
                }
        );

    }

    public void buttonClicked(View view) {
        String url = et_inputHTTP.getText().toString();
        ConnectivityManager connMan = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            new DownloadMaster().execute(url);
            //tv_outputContent.setText("Connected to the network");
        } else {
            tv_outputContent.setText("No network connection available.");
        }
    }

    private class DownloadMaster extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            try {
                return AccessUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to reach the page, please check the URL";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String outputAsync) {
            tv_outputContent.setText(outputAsync);
        }
    }


    private String AccessUrl(String address) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int length = 500;

        try {
            URL userUrl = new URL(address);
            HttpURLConnection connect = (HttpURLConnection) userUrl.openConnection();
            connect.setReadTimeout(10000 /* milliseconds */);
            connect.setConnectTimeout(15000 /* milliseconds */);
            connect.setRequestMethod("GET");
            connect.setDoInput(true);
            // Starts the query
            connect.connect();
            int response = connect.getResponseCode();

            switch (connect.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    Log.d(DEBUG_TAG, " **OK**");
                    break; // fine, go on
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    Log.d(DEBUG_TAG, " **gateway timeout**");
                    break;// retry
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    System.out.println(DEBUG_TAG + "**unavailable**");
                    break;// retry, server is unstable
                default:
                    Log.d(DEBUG_TAG, " **unknown response code**.");
                    break; // abort
            }


            //Log.d(DEBUG_TAG, "The response is: " + response);
            is = connect.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, length);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    public String readIt(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];
        reader.read(buffer);
        return new String(buffer);
    }
}
