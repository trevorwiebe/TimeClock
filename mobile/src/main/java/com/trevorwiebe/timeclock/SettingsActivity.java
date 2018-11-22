package com.trevorwiebe.timeclock;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

//        final Button send = this.findViewById(R.id.button1);
//
//        send.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                String fromEmail = ((TextView) findViewById(R.id.editText1)).getText().toString();
//
//                String fromPassword = ((TextView) findViewById(R.id.editText2)).getText().toString();
//
//                String toEmails = ((TextView) findViewById(R.id.editText3)).getText().toString();
//
//                List<String> toEmailList = Arrays.asList(toEmails.split("\\s*,\\s*"));
//
//                String emailSubject = ((TextView) findViewById(R.id.editText4)).getText().toString();
//
//                String emailBody = ((TextView) findViewById(R.id.editText5)).getText().toString();
//
//                new SendMailTask().execute(fromEmail, fromPassword, toEmailList, emailSubject, emailBody);
//            }
//        });
    }

    public void sendHours(View view){
        Intent selectSendHoursIntent = new Intent(SettingsActivity.this, SelectHoursSentRecurrenceActivity.class);
        startActivity(selectSendHoursIntent);
    }
}