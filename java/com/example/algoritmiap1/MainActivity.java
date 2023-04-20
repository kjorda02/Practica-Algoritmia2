package com.example.algoritmiap1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void saludar(View view){
        System.out.println("Hola!");
        TextView textview = findViewById(R.id.textViewSaludar);
        EditText editText = findViewById(R.id.editTextName);
        textview.setText("AAAAAAAAAAAAAAAAAAAAAA" + editText.getText().toString());
    }
}