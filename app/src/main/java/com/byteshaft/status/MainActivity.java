package com.byteshaft.status;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.byteshaft.requests.HttpRequest;

import java.net.HttpURLConnection;

import static com.byteshaft.status.AppGlobals.KEY_ID;
import static com.byteshaft.status.AppGlobals.KEY_PASSWORD;
import static com.byteshaft.status.AppGlobals.getStringFromSharedPreferences;

public class MainActivity extends AppCompatActivity implements
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private EditText idField;
    private EditText passwordField;
    private EditText intervalField;
    private Button saveButton;

    private String mIdString;
    private String mPasswordString;
    private String mIntervalString;

    private HttpRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        idField = (EditText) findViewById(R.id.id);
        passwordField = (EditText) findViewById(R.id.password);
        intervalField = (EditText) findViewById(R.id.interval);
        saveButton = (Button) findViewById(R.id.button_save);
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
                    changeStatus();
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

    private void changeStatus() {
        request = new HttpRequest(this);
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", AppGlobals.GET_URL);
        request.send();
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        request.getResponseText();
                        Log.e("Status: ", request.getResponseText() + "  " + request.getResponseURL());
                        startService(new Intent(this, StatusService.class));
                }
        }
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }
}
