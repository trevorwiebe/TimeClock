package com.trevorwiebe.timeclock;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.trevorwiebe.timeclock.object.SendEmailObject;
import com.trevorwiebe.timeclock.utils.Utility;

import java.util.ArrayList;

public class SelectHoursSentRecurrenceActivity extends AppCompatActivity {

    private static final String TAG = "SelectHoursSentRecurren";

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    // widgets
    Spinner mSendingMethodSpinner;
    Spinner mWhenToSendSpinner;
    TextView mNumberOfTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_hours_sent_recurrence);

        // Spinner to choose how you want to send the data to the employer
        mSendingMethodSpinner = findViewById(R.id.sending_method);

        // Layout and child views used to manipulate the email used to send with EMAIL
        final LinearLayout method_text_message_layout = findViewById(R.id.method_text_layout);
        final TextView method_text_message_text_view = findViewById(R.id.method_text_message_text_view);

        // Layout and child views used to show the phone number used to send the TEXT MESSAGE
        final LinearLayout method_email_layout = findViewById(R.id.method_email_layout);
        final TextView method_sending_textView = findViewById(R.id.method_email_text_view);

        // Layout and child views used deal with TELEGRAM

        mWhenToSendSpinner = findViewById(R.id.time_chooser_spinner);
        mNumberOfTimes = findViewById(R.id.number_of_times);

        ArrayList<String> sending_method_list = new ArrayList<>();
        sending_method_list.add("Text message");
        sending_method_list.add("Telegram");
        if(mUser != null) {
            sending_method_list.add("Email");
        }
        ArrayAdapter<String> sendingMethodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sending_method_list);
        mSendingMethodSpinner.setAdapter(sendingMethodAdapter);

        mSendingMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        // Send with Text Message
                        method_email_layout.setVisibility(View.GONE);
                        method_text_message_layout.setVisibility(View.VISIBLE);
                        String send_with_text_message_text = "Time Clock will send your hour with this devices phone number: " + "-phone number-" + ".";
                        method_text_message_text_view.setText(send_with_text_message_text);
                        break;
                    case 1:
                        // Send with Telegram
                        method_sending_textView.setVisibility(View.GONE);
                        method_text_message_layout.setVisibility(View.GONE);
                        break;
                    case 2:
                        // Send with Email
                        method_text_message_layout.setVisibility(View.GONE);
                        method_email_layout.setVisibility(View.VISIBLE);
                        String send_with_email_text = "Time Clock will send your hours with email: " + mUser.getEmail() + ".";
                        method_sending_textView.setText(send_with_email_text);
                        break;
                    default:
                        Toast.makeText(SelectHoursSentRecurrenceActivity.this, "There was an error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayList<String> when_to_send_list = new ArrayList<>();
        when_to_send_list.add("day");
        when_to_send_list.add("week");
        when_to_send_list.add("month");
        when_to_send_list.add("year");
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, when_to_send_list);
        mWhenToSendSpinner.setAdapter(itemsAdapter);

    }

    public void editEmail(View view){
        View editTextLayout = LayoutInflater.from(this).inflate(R.layout.dialog_edit_email, null);
        AlertDialog.Builder editEmailDialog = new AlertDialog.Builder(this)
                .setTitle("Edit email")
                .setView(editTextLayout)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        editEmailDialog.show();
        EditText editEmailEditText = editTextLayout.findViewById(R.id.edit_email_edit_text);
        editEmailEditText.setText(mUser.getEmail());
        int length = editEmailEditText.length();
        editEmailEditText.setSelection(length);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_send_data_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.save_send_data){
            int method_to_send = mSendingMethodSpinner.getSelectedItemPosition();
            String email = "";
            String phoneNumber = "";
            if(method_to_send == SendEmailObject.SEND_WITH_EMAIL){
                email = mUser.getEmail();
            }
            if(method_to_send == SendEmailObject.SEND_WITH_TEXT_MESSAGE){
                phoneNumber = "-phone_number-";
            }

            String whenToSendString = "";
            switch (mWhenToSendSpinner.getSelectedItemPosition()){
                case 0:
                    whenToSendString = whenToSendString + "d,";
                    String timePerDays = mNumberOfTimes.getText().toString();
                    whenToSendString = whenToSendString + timePerDays;
                    break;
                case 1:
                    whenToSendString = whenToSendString + "w,";
                    break;
                case 2:
                    whenToSendString = whenToSendString + "m,";
                    break;
                case 3:
                    whenToSendString = whenToSendString + "y,";
                    String timePerYears = mNumberOfTimes.getText().toString();
                    whenToSendString = whenToSendString + timePerYears;
                    break;
                default:
                    Log.e(TAG, "onOptionsItemSelected: error when adding the first letter of the whenToSendString");
            }
            SendEmailObject sendEmailObject = new SendEmailObject(method_to_send, "when to send string", email, phoneNumber, Utility.getTimeFromWhenToString(whenToSendString));
            Log.d(TAG, "onOptionsItemSelected: " + sendEmailObject.getWhenToSendNext());
        }
        return super.onOptionsItemSelected(item);
    }
}
