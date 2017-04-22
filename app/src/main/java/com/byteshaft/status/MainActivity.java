package com.byteshaft.status;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.concurrent.TimeUnit;

import static com.byteshaft.status.AppGlobals.KEY_ID;
import static com.byteshaft.status.AppGlobals.KEY_PASSWORD;
import static com.byteshaft.status.AppGlobals.getStringFromSharedPreferences;

public class MainActivity extends AppCompatActivity {

    private EditText idField;
    private EditText passwordField;
    private EditText intervalField;
    private Button saveButton;

    private String mIdString;
    private String mPasswordString;
    private String mIntervalString;
    private static MainActivity sInstance;

    public static MainActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sInstance = this;
        idField = (EditText) findViewById(R.id.id);
        passwordField = (EditText) findViewById(R.id.password);
        intervalField = (EditText) findViewById(R.id.interval);
        saveButton = (Button) findViewById(R.id.button_save);
        if (AppGlobals.isDataSaved()) {
            passwordField.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_PASSWORD));
            idField.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_ID));
            intervalField.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_SECONDS));
            Log.i("TAG", "service null " + String.valueOf(StatusService.getInstance() == null));
            if (StatusService.getInstance() == null) {
                startService(new Intent(getApplicationContext(), StatusService.class));
                String intervalInSeconds = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_SECONDS);
                int seconds = Integer.parseInt(intervalInSeconds);
                AlarmHelpers.setAlarmForInterval(TimeUnit.SECONDS.toMillis(seconds));
            }
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    System.out.println("OK");
                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_SECONDS, mIntervalString);
                    AppGlobals.saveDataToSharedPreferences(KEY_PASSWORD, mPasswordString);
                    AppGlobals.saveDataToSharedPreferences(KEY_ID, mIdString);
                    AppGlobals.GET_URL = String.format("https://sourceway.de/admin/online.php?id=%s&amp;pw=%s",
                            getStringFromSharedPreferences(KEY_ID),
                            getStringFromSharedPreferences(KEY_PASSWORD));
                    AppGlobals.saveData(true);
                    String intervalInSeconds = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_SECONDS);
                    int seconds = Integer.parseInt(intervalInSeconds);
                    AlarmHelpers.setAlarmForInterval(TimeUnit.SECONDS.toMillis(seconds));
                    startService(new Intent(getApplicationContext(), StatusService.class));
                    View currentFocus = getCurrentFocus();
                    if (currentFocus != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                        Snackbar.make(findViewById(android.R.id.content), "Service is Running", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        mIdString = idField.getText().toString();
        mPasswordString = passwordField.getText().toString();
        mIntervalString = intervalField.getText().toString();

        if (mIdString.trim().isEmpty()) {
            idField.setError("Please provide an id");
            valid = false;
        } else {
            idField.setError(null);
        }
        if (mPasswordString.isEmpty()) {
            passwordField.setError("Please provide password");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        if (mIntervalString.isEmpty()) {
            intervalField.setError("Please provide interval");
        } else {
            intervalField.setError(null);
        }
        return valid;
    }
}
