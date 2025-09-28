package com.example.nestera.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.nestera.R;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class demoSpinner extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_spinner);
        // Spinner 1
        Spinner spinner1 = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.spinner1_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        // Spinner 2
        final Spinner spinner2 = findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.spinner2_array_default, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        // Spinner 1 OnItemSelectedListener
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Based on the selection in Spinner 1, update data for Spinner 2
                updateSpinner2Data(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }
    private void updateSpinner2Data(int spinner1Selection) {
        // Update data for Spinner 2 based on the selection in Spinner 1
        ArrayAdapter<CharSequence> adapter2;
        switch (spinner1Selection) {
            case 0:
                adapter2 = ArrayAdapter.createFromResource(this,
                        R.array.spinner2_array_option1, android.R.layout.simple_spinner_item);
                break;
            case 1:
                adapter2 = ArrayAdapter.createFromResource(this,
                        R.array.spinner2_array_option2, android.R.layout.simple_spinner_item);
                break;
            // Add more cases as needed

            default:
                adapter2 = ArrayAdapter.createFromResource(this,
                        R.array.spinner2_array_default, android.R.layout.simple_spinner_item);
        }

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner2 = findViewById(R.id.spinner2);
        spinner2.setAdapter(adapter2);
    }
}