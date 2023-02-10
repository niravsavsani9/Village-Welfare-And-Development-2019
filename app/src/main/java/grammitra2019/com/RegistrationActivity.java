package grammitra2019.com;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import grammitra2019.com.Model.Profiles;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button          mSignIn;
    private TextInputLayout mEditTextUsernamelayout, mEditTextMailIdlayout, mEditTextMobilenolayout, mEditTextAgeLayout, mEditTextpasswordLayout;
    private TextInputEditText mEditTextUsername, mEditTextMailID, mEditTextMobileno, mEditTextAge, mEditTextPassword;
    private ProgressDialog mProgressDialog;
    public  Profiles       profiles;

    private DatabaseReference databaseUserRegister;
    private FirebaseAuth      firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registration);

        RegistrationIDenque();

        firebaseAuth = Utility.FIREBASE_AUTH;
        databaseUserRegister = Utility.DATABASE_REFERENCE_ROOT;

        mProgressDialog = new ProgressDialog(this);

        mSignIn.setOnClickListener(this);
    }

    private void RegistrationIDenque() {

        mSignIn = findViewById(R.id.reg_btn_submit);

        mEditTextUsernamelayout = findViewById(R.id.reg_user);
        mEditTextMailIdlayout = findViewById(R.id.reg_email);
        mEditTextMobilenolayout = findViewById(R.id.reg_mobile);
        mEditTextAgeLayout = findViewById(R.id.reg_age);
        mEditTextpasswordLayout = findViewById(R.id.reg_pwd);

        mEditTextUsername = findViewById(R.id.reg_sub_user);
        mEditTextMailID = findViewById(R.id.reg_sub_email);
        mEditTextMobileno = findViewById(R.id.reg_sub_mobile);
        mEditTextAge = findViewById(R.id.reg_sub_age);
        mEditTextPassword = findViewById(R.id.reg_sub_pwd);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.reg_btn_submit:
                RegisterUser();
                break;
        }
    }

    private void RegisterUser() {

        final String userName, userMail, userMobileno, userAge, userPassword;

        userName = mEditTextUsername.getText().toString().trim();
        userMobileno = mEditTextMobileno.getText().toString().trim();
        userAge = mEditTextAge.getText().toString().trim();
        userMail = mEditTextMailID.getText().toString().trim();
        userPassword = mEditTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(mEditTextUsername.getText())) {
            mEditTextUsernamelayout.setError("Example: User");
            mEditTextUsernamelayout.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mEditTextMailID.getText())) {
            mEditTextMailIdlayout.setError("Example: User123@gmail.com");
            mEditTextMailIdlayout.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mEditTextMobileno.getText())) {
            mEditTextMobilenolayout.setError("Example: 9517531237");
            mEditTextMobilenolayout.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mEditTextAge.getText())) {
            mEditTextAgeLayout.setError("Example: 20");
            mEditTextAgeLayout.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mEditTextPassword.getText())) {
            mEditTextpasswordLayout.setError("Example: User123user");
            mEditTextpasswordLayout.requestFocus();
            return;
        }

        mProgressDialog.setMessage("Registering User");
        mProgressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(userMail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            profiles = new Profiles(userName, userPassword, userMail, userAge, userMobileno);

                            databaseUserRegister
                                    .child(firebaseAuth.getCurrentUser().getUid())
                                    .setValue(profiles);

                            mProgressDialog.dismiss();
                            Snackbar.make(findViewById(android.R.id.content), "Registration Successful", Snackbar.LENGTH_LONG).show();

                            startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));

                        } else {
                            mProgressDialog.dismiss();
                            Snackbar.make(findViewById(android.R.id.content), "Please Enter Valid Details", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this,LoginActivity.class));
    }
}