package com.trevorwiebe.timeclock.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class SendMailTask extends AsyncTask {


    @Override
    protected Object doInBackground(Object... args) {
        try {
            GMailSender androidEmail = new GMailSender(args[0].toString(),
                    args[1].toString(), (List) args[2], args[3].toString(),
                    args[4].toString());

            androidEmail.createEmailMessage();
            androidEmail.sendEmail();
            Log.i("SendMailTask", "Mail Sent.");
        } catch (Exception e) {
            Log.e("SendMailTask", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {

    }
}
