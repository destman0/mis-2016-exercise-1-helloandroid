package com.example.desperados.ex1_http;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

// This code based on the code found here:
// http://developer.android.com/training/basics/network-ops/connecting.html

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "HttpExample";
    private static String toastText = "";
    private static EditText et_inputHTTP;
    private static TextView tv_outputContent;
    private static Button  btn_getHTTP;
    private static WebView webDisplay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_inputHTTP = (EditText)findViewById(R.id.et_inputHTTP);
        tv_outputContent = (TextView) findViewById(R.id.tv_outputContent);
        btn_getHTTP = (Button) findViewById(R.id.btn_getHTTP);
        webDisplay = (WebView) findViewById(R.id.wv_WebContent);

        // Prevents the keyboard from opening automatically on app start
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

    //Handles toasts for error messages. Based on code from: http://stackoverflow.com/a/23936088
    public void showToast(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast myToast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG);
                myToast.show();
            }
        });
    }

    public void buttonClicked(View view) {
        String url = et_inputHTTP.getText().toString();

        // Uses context to make use of system services available and establishes a connection
        ConnectivityManager connMan = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //Checks for a network connection
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            new ContentRetrieval().execute(url);
            //tv_outputContent.setText("Connected to the network");
            if (Patterns.WEB_URL.matcher(url).matches() == true){
                webDisplay.loadUrl(url);
            }
            else {
                toastText = "The URL is not valid. Please try again";
                showToast();
            }
        } else {
            //tv_outputContent.setText("No network connection available.");

            toastText = "No network connection available";
            showToast();
        }
    }

    private class ContentRetrieval extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // app checks if it can find the web address
            try {
                return AccessUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to reach the page, please check the URL";
            }
        }
        // onPostExecute displays the results of the AsyncTask i.e. our HTML content.
        @Override
        protected void onPostExecute(String outputAsync) {
            tv_outputContent.setText(outputAsync);
        }
    }


    private String AccessUrl(String address) throws IOException {
        InputStream webContent = null;

        // Only display the first 500 characters of the retrieved
        // web page content.
        int charsToDisplay = 500;

        try {
            URL userUrl = new URL(address);
            HttpURLConnection connectMe = (HttpURLConnection) userUrl.openConnection();
            connectMe.setReadTimeout(10000 /* milliseconds */);
            connectMe.setConnectTimeout(15000 /* milliseconds */);
            connectMe.setRequestMethod("GET");
            connectMe.setDoInput(true);
            // Starts the query
            connectMe.connect();
            int response = connectMe.getResponseCode();

            //different things happen depending on the response code received
            switch (connectMe.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    Log.d(DEBUG_TAG, " **OK**");

                    toastText = "Connection successful";
                    showToast();

                    break; // continue
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    Log.d(DEBUG_TAG, " **gateway timeout**");

                    toastText = "Timeout";
                    showToast();
                    break;// retry
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    Log.d(DEBUG_TAG, "**unavailable**");

                    toastText = "Server unavailable";
                    showToast();
                    break;// problem server side, retry
                default:
                    Log.d(DEBUG_TAG, " **unknown response code**.");

                    toastText = "Unknown response code received";
                    showToast();
                    break; // abort
            }


            //Log.d(DEBUG_TAG, "The response is: " + response);
            webContent = connectMe.getInputStream();

            // Convert the InputStream into a string and display the max chars as set before
            String contentToString = useAppReader(webContent, charsToDisplay);
            return contentToString;

            // Once information is retrieved, close InputStream
        } finally {
            if (webContent != null) {
                webContent.close();
            }
        }
    }
    /*
    use the java Reader to convert the input data to characters and return string
    and allocate space (buffer) in memory to the stream
    */
    public String useAppReader(InputStream receivedData, int length) throws IOException, UnsupportedEncodingException {
        Reader showContent = null;
        showContent = new InputStreamReader(receivedData, "UTF-8");
        char[] allocateBuffer = new char[length];
        showContent.read(allocateBuffer);
        return new String(allocateBuffer);
    }
}
