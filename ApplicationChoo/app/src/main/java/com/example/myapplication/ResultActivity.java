package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent result_intent=new Intent(this.getIntent());

        String result=result_intent.getStringExtra("result");

        TextView model_result=(TextView)findViewById(R.id.model_result);
        model_result.setText(result);

        /*

        int node_num[]=result_intent.getExtras().getIntArray("nodenum");
        int activation_func[]=result_intent.getExtras().getIntArray("activationfunction");

        TextView model_result=(TextView)findViewById(R.id.model_result);
        model_result.setText(Integer.toString(activation_func[1]));*/

    }
}