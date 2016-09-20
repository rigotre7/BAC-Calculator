package com.example.rigot.baccalculator;

/*
    Homework 1
    Name: Rodrigo Trejo
    Name: Anal Shah
 */

import android.annotation.TargetApi;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {

    Double alcLevel;
    Double bacLevel = 0.0;
    Double bacLevelFinal = 0.0;
    Double drinkSizeAndStrength = 0.0;
    int weight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DecimalFormat twoPlaces = new DecimalFormat("#.000");
        final ArrayList<Double> drinks = new ArrayList<>();
        final TextView status = (TextView) findViewById(R.id.displayStatus);
        final TextView bacLabel = (TextView) findViewById(R.id.bacLabel);
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        final SeekBar percentBar = (SeekBar) findViewById(R.id.seekBar);
        final EditText weightText = (EditText)findViewById(R.id.enterWeight);
        final ToggleButton genderToggle = (ToggleButton)findViewById(R.id.genderButton);
        final Button saveButton = (Button)findViewById(R.id.saveButton);
        final Button addDrink = (Button) findViewById(R.id.addDrinkButton);
        Button resetButton = (Button) findViewById(R.id.resetButton);
        final RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup);
        final TextView alcLevelLabel = (TextView) findViewById(R.id.seekBarValue);
        status.setBackgroundColor(Color.parseColor("#00CC00"));
        percentBar.setMax(5);
        percentBar.setProgress(1);  //set default percent bar values
        alcLevelLabel.setText("5%");


        //this displays the percent of alcohol as the slider is moved
        percentBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                alcLevel = i*5*.01;
                alcLevelLabel.setText((alcLevel*100) + " %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        addDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = rg.getCheckedRadioButtonId();  //get id of checked radio button
                if(weightText.length() != 0){   //make sure a value has been entered
                    weight = Integer.parseInt(String.valueOf(weightText.getText()));    //get weight from user
                    switch (id){    //switch statement for code execution depending on what drink size is chosen
                        case R.id.oneOzDrink:
                            drinkSizeAndStrength = 1*alcLevel;
                            if(genderToggle.isChecked()){       //if-else statement to determine gender
                                bacLevel = (drinkSizeAndStrength/(weight*.55));
                            }else{
                                bacLevel = (drinkSizeAndStrength/(weight*.68));
                            }break;
                        case R.id.fiveOzDrink:
                            drinkSizeAndStrength = 5*alcLevel;
                            if(genderToggle.isChecked()){
                                bacLevel = (drinkSizeAndStrength/(weight*.55));
                            }else{
                                bacLevel = (drinkSizeAndStrength/(weight*.68));
                            }break;
                        case R.id.twelveOzDrink:
                            drinkSizeAndStrength = 12*alcLevel;
                            if(genderToggle.isChecked()){
                                bacLevel = (drinkSizeAndStrength/(weight*.55));
                            }else{
                                bacLevel = (drinkSizeAndStrength/(weight*.68));
                            }break;
                        default: break;
                    }
                    //here we calc the current bac level
                    bacLevelFinal += bacLevel;
                    //store individual drink in case recalculations need to be done
                    drinks.add(drinkSizeAndStrength);
                    //if statements determine what message to display and color
                    if(bacLevelFinal > .08 && bacLevelFinal < .2){
                        status.setBackgroundColor(Color.parseColor("#FFC300"));
                        status.setText(R.string.carefulString);
                    }else if(bacLevelFinal >= .2){
                        status.setBackgroundColor(Color.parseColor("#ff0000"));
                        status.setText(R.string.overString);

                        //if over the limit, disable all buttons except reset
                        if(bacLevelFinal>=.25){
                            saveButton.setEnabled(false);
                            weightText.setEnabled(false);
                            genderToggle.setEnabled(false);
                            addDrink.setEnabled(false);
                            percentBar.setEnabled(false);
                            for(int j=0; j<rg.getChildCount(); j++){
                                rg.getChildAt(j).setEnabled(false);
                            }
                            Toast.makeText(MainActivity.this, "No more drinks for you!", Toast.LENGTH_LONG).show();
                        }
                    }
                    //display the bac level
                    bacLabel.setText("BAC Level: " + twoPlaces.format(bacLevelFinal));
                    int level = (int)(1000*bacLevel);
                    progressBar.incrementProgressBy(level);



                }else{
                    weightText.setError("Please enter a valid weight!");
                }
            }
        });

        //save button recalculates the bac level if gender and/or weight has been changed
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //here we get the new weight and/or gender and recalculate
                bacLevelFinal=0.0;
                if(weightText.length()!=0){
                    weight = Integer.parseInt(weightText.getText().toString());

                    if(genderToggle.isChecked()){   //female
                        for( int j=0; j<drinks.size(); j++){
                            bacLevelFinal+=(drinks.get(j)/(weight*.55));
                        }
                    }else{  //male
                        for(int j=0; j<drinks.size(); j++){
                            bacLevelFinal += (drinks.get(j)/(weight*.68));
                        }
                    }
                    bacLabel.setText("BAC Level: " + twoPlaces.format(bacLevelFinal));
                    progressBar.setProgress((int)(1000*bacLevelFinal));


                    //determine the message and color to display based on bac level
                    if(bacLevelFinal<.08){
                        status.setBackgroundColor(Color.parseColor("#00CC00"));
                        status.setText(R.string.okayString);
                    }else if(bacLevelFinal > .08 && bacLevelFinal < .2){
                        status.setBackgroundColor(Color.parseColor("#FFC300"));
                        status.setText(R.string.carefulString);
                    }else if(bacLevelFinal >= .2){
                        status.setBackgroundColor(Color.parseColor("#ff0000"));
                        status.setText(R.string.overString);

                        //if over the limit, disable all buttons except reset
                        if(bacLevelFinal>=.25){
                            saveButton.setEnabled(false);
                            weightText.setEnabled(false);
                            genderToggle.setEnabled(false);
                            addDrink.setEnabled(false);
                            percentBar.setEnabled(false);
                            for(int j=0; j<rg.getChildCount(); j++){
                                rg.getChildAt(j).setEnabled(false);
                            }
                            Toast.makeText(MainActivity.this, "No more drinks for you!", Toast.LENGTH_LONG).show();
                        }
                    }
                }else
                    weightText.setError("Please enter a valid weight!");

            }
        });

        //set defaults when reset button is clicked
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rg.check(R.id.oneOzDrink);
                percentBar.setProgress(1);
                weight = 0;
                weightText.getText().clear();
                genderToggle.setChecked(false);
                bacLevelFinal=0.0;
                bacLabel.setText("BAC Level :" + bacLevelFinal);
                status.setBackgroundColor(Color.parseColor("#00CC00"));
                status.setText(R.string.okayString);
                progressBar.setProgress(0);
                saveButton.setEnabled(true);
                weightText.setEnabled(true);
                genderToggle.setEnabled(true);
                drinks.clear();
                addDrink.setEnabled(true);
                percentBar.setEnabled(true);
                for(int j=0; j<rg.getChildCount(); j++){
                    rg.getChildAt(j).setEnabled(true);
                }
            }
        });

    }
}
