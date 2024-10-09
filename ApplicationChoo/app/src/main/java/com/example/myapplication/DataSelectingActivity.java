package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DataSelectingActivity extends AppCompatActivity {

    String[] this_data={"MNIST"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataselecting);

        Intent intent=new Intent(this.getIntent());

        this_data[0] = intent.getStringExtra("dataname");

        final TextView data_explain=(TextView)findViewById(R.id.data_explain);

        data_explain.setText(this_data[0]);

        Button mnist=(Button)findViewById(R.id.mnist);

        mnist.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                this_data[0]="MNIST";
                onBackPressed();
            }
        });

        // 지금은 MNIST만 선택할 수 있도록 했음
        /*
        Button cifar10=(Button)findViewById(R.id.cifar10);

        cifar10.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                this_data[0]="CIFAR-10";
                onBackPressed();
            }
        });*/
    }

    @Override
    public void onBackPressed(){
        Intent intent=new Intent();
        intent.putExtra("dataname",this_data[0]);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}