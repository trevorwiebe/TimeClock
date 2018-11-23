package com.trevorwiebe.timeclock;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.trevorwiebe.timeclock.utils.Utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG = "CreateAccountActivity";

    private EditText mNewUserName;
    private EditText mNewUserEmail;
    private EditText mNewUserPassword;
    private SignInButton mCreateGoogleUserBtn;
    private static final int GOOGLE_CREATE_USER_CODE = 696;

    private FirebaseAuth mAuth;

    private AlertDialog mCreatingUserDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        mAuth = FirebaseAuth.getInstance();

        mNewUserName = findViewById(R.id.new_user_name);
        mNewUserEmail = findViewById(R.id.new_user_email);
        mNewUserPassword = findViewById(R.id.new_user_password);
        mCreateGoogleUserBtn = findViewById(R.id.google_create_user_btn);

        setGooglePlusButtonText(mCreateGoogleUserBtn, "Create account");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        final GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        mCreateGoogleUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_CREATE_USER_CODE);
            }
        });

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("Creating your shining new account!")
                .setCancelable(false)
                .setView(view);

        mCreatingUserDialog = dialog.create();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_CREATE_USER_CODE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Toast.makeText(this, "Error signing in", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        mCreatingUserDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null) {
                                Toast.makeText(CreateAccountActivity.this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            mCreatingUserDialog.dismiss();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void createUser(View view){

        Log.d(TAG, "createUser: here");

        mCreatingUserDialog.show();

        if(mNewUserName.length() == 0 || mNewUserPassword.length() == 0 || mNewUserEmail.length() == 0){
            mCreatingUserDialog.dismiss();
            return;
        }

        Log.d(TAG, "createUser: here2");

        String email = mNewUserEmail.getText().toString();
        final String name = mNewUserName.getText().toString();
        String password = mNewUserPassword.getText().toString();

        Log.d(TAG, "createUser: here3");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();

                            if(user != null) {
                                user.updateProfile(profileUpdates);
                            }
                            mCreatingUserDialog.dismiss();
                            finish();
                        }else{
                            mCreatingUserDialog.dismiss();
                            Toast.makeText(CreateAccountActivity.this, "There was an error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

}
