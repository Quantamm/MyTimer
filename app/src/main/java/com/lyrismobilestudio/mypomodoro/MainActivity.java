package com.lyrismobilestudio.mypomodoro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    String[] buttonNames = {"Break", "Lunch", "Meeting", "Consult", "Other", "Defect #1", "Defect #2", "Defect #3", "Stop Working"};
    List<Button> buttons;
    Drawable background;
    long[] totalTimes;
    long startTime;
    int runningButton;

    private void setOnClickListeners() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.buttonHolder);
        buttons = new ArrayList<Button>();
        for(String name : buttonNames) {
            Button button = (Button) getLayoutInflater().inflate(R.layout.pomodoro_button, null);
            button.setText(name);
            ll.addView(button);
            background = button.getBackground();
            buttons.add(button);
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleClick(v);
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setOnClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);

        totalTimes = new long[buttonNames.length];
        int index = 0;
        for(String name : buttonNames) {
            totalTimes[index++] = prefs.getLong(name, 0);
        }
        startTime = prefs.getLong("startTime", 0);
        runningButton = prefs.getInt("runningButton", -1);
        if(runningButton != -1) {
            buttons.get(runningButton).setBackgroundColor(Color.RED);
        }
    }

    private void writePrefs() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int index = 0;
        for(String name : buttonNames) {
            editor.putLong(name, totalTimes[index]);
            index++;
        }
        editor.putLong("startTime", startTime);
        editor.putInt("runningButton", runningButton);
        editor.commit();
    }

    private void clearData() {
        getPreferences(MODE_PRIVATE).edit().clear().commit();
    }

    private void handleClick(View v) {
        Button b = (Button) v;
        String text = b.getText().toString();
        if(text.equals(buttonNames[buttonNames.length-1])) {
            stopTiming();
            presentResults();
        } else {
            handleTimingButtonClick(v);
        }
    }

    private void presentResults() {
        clearData();
        Intent intent =new Intent(this, ResultsActivity.class);
        String results = getResultsString();
        intent.putExtra("results", results);
        startActivity(intent);
        finish();
    }

    private String getResultsString() {
        String resultString = "";
        for(Button button : buttons) {
            if(button.getText().toString().equals(buttonNames[buttonNames.length-1])) {
                continue;
            }
            String buttonName = button.getText().toString();
            resultString+=buttonName+":\t\t";
            int index = buttons.indexOf(button);
            if(index!=-1) {
                long time = totalTimes[index];
                resultString += String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time), TimeUnit.MILLISECONDS.toMinutes(time)%60, TimeUnit.MILLISECONDS.toSeconds(time)%60);
            }
            resultString+="\n\n";

        }
        return resultString;
    }

    private void handleTimingButtonClick(View v) {
        stopTiming();
        v.setBackgroundColor(Color.RED);
        startTime = System.currentTimeMillis();
        for(Button button : buttons) {
            if(button == v) {
                runningButton = buttons.indexOf(button);
            }
        }
        writePrefs();
    }

    private void stopTiming() {
        for(Button button : buttons) {
            button.setBackgroundDrawable(background);
        }
        if(runningButton!=-1) {
            totalTimes[runningButton] += System.currentTimeMillis() - startTime;
            runningButton = -1;
        }
    }


}
