package com.example.desperados.ex1_http;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    private static EditText et_inputHTTP;
    private static TextView tv_outputContent;
    private static Button  btn_getHTTP;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_getHTTP = (Button) findViewById(R.id.btn_getHTTP);
        btn_getHTTP.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        TextView pablosText = (TextView) findViewById(R.id.tv_outputContent);
                        pablosText.setText("Good job Boss");
                    }
                }
        );
    }
}
