package com.innovapptive.admincontrols;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button btnToProducts, btnToCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnToCategory = findViewById(R.id.button_to_category);
        btnToProducts = findViewById(R.id.button_to_product);
        btnToCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this, CategoryActivity.class);
                startActivity(in);
            }
        });
        btnToProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this,ProductActivity.class);
                startActivity(in);
            }
        });
    }
}