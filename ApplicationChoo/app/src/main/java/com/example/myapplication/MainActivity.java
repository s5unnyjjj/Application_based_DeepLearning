package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button=(Button) findViewById(R.id.start_button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), BuildingModelActivity.class);
                startActivity(intent);
            }
        });

        Button explanation_start=(Button)findViewById(R.id.explanation_start);
        explanation_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), AppExplanationActivity.class);
                startActivity(intent);
            }
        });

        ImageView babogaegif=(ImageView)findViewById(R.id.babogaegif);
        Glide.with(this).load(R.raw.babogae).into(babogaegif);
    }
}