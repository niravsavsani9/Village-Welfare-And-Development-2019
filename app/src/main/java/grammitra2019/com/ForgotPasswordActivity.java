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
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button            mForgotSubmit;
    private TextInputEditText mEditTextEmail;
    private TextInputLayout   mEditTextEmailLayout;
    public  FirebaseAuth      mFireBaseAuth;
    public  ProgressDialog    mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_forgot_password);

        // displaying ID
        ForgotPasswordIDconfique();

        //without getting instance of firebase we cant get current user so we defining the instance of firebaseAuth
        mFireBaseAuth = Utility.FIREBASE_AUTH;

        mProgressDialog = new ProgressDialog(this);

        mForgotSubmit.setOnClickListener(this);

    }

    private void ForgotPasswordIDconfique() {

        mEditTextEmail = findViewById(R.id.forgot_sub_email);
        mForgotSubmit = findViewById(R.id.forgot_btn_submit);
        mEditTextEmailLayout = findViewById(R.id.forgot_email);

    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.forgot_btn_submit:
                ForgotUser();
                break;
        }
    }

    private void ForgotUser() {

        final String forgotPassword;

        // email id is not equal to null
        if (TextUtils.isEmpty(mEditTextEmail.getText())) {
            mEditTextEmailLayout.setError("Example: User123@gmail.com");
            mEditTextEmailLayout.requestFocus();
            return;
        }

        // getting value from edit text
        forgotPassword = mEditTextEmail.getText().toString().trim();

        mProgressDialog.setMessage("Verifying Email");
        mProgressDialog.show();

        // here we are sending the reset password link
        mFireBaseAuth.sendPasswordResetEmail(forgotPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Snackbar.make(findViewById(android.R.id.content), "Verification Successful check your email", Snackbar.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        } else {
                            mProgressDialog.dismiss();
                            Snackbar.make(findViewById(android.R.id.content), "Internet Connection Required or Slow", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // on BackPressed method we are handling up back activity
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
    }
}