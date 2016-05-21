package com.lyrismobilestudio.mypomodoro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView results = (TextView) findViewById(R.id.resultsTextView);
        Intent intent = getIntent();
        String resultsString = intent.getStringExtra("results");
        if (results != null) {
            results.setText(resultsString);
        }
    }
}
