package grammitra2019.com;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.onesignal.OneSignal;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mLogin, mRegister;
    private TextView        mForgotPassword;
    private TextInputLayout mEditTextMailLayout, mEditTextpasswordLayout;
    private TextInputEditText mEditTextMail, mEditTextPassoword;
    private ProgressDialog mProgressDialog;
    public  Intent         mIntent;
    public  long           mBackPressedTime;
    public  CheckBox       mCheckSave;

    private FirebaseAuth      mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    public  SharedPreferences mSharedPreferences;

    @Override
    protected void onStart() {
        super.onStart();

        mSharedPreferences = Utility.getPreference(this);

        if (!(mSharedPreferences.getString(Utility.USERMAILID, "") == null && mSharedPreferences.getString(Utility.USERPASSWORD, "") == null)) {

            mEditTextMail.setText(mSharedPreferences.getString(Utility.USERMAILID, ""));
            mEditTextPassoword.setText(mSharedPreferences.getString(Utility.USERPASSWORD, ""));
            mCheckSave.setChecked(true);
        } else {

            mCheckSave.setChecked(false);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requiredPermissions();
        loginIDengue();

        mFirebaseAuth = Utility.FIREBASE_AUTH;
        mDatabaseReference = Utility.DATABASE_REFERENCE_ROOT;
        mProgressDialog = new ProgressDialog(this);

        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mForgotPassword.setOnClickListener(this);

        mCheckSave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String mEmail    = mEditTextMail.getText().toString().trim();
                String mPassword = mEditTextPassoword.getText().toString().trim();

                if (mCheckSave.isChecked()) {
                    //if checkbox is checked than value added into SharedPreference
                    mSharedPreferences.edit().putString(Utility.USERMAILID, mEmail).commit();
                    mSharedPreferences.edit().putString(Utility.USERPASSWORD, mPassword).commit();
                    return;
                } else {
                    //SharedPreference cleared
                    mSharedPreferences.edit().clear().commit();
                    return;
                }
            }
        });
    }

    private void loginIDengue() {

        // Buttons
        mLogin = findViewById(R.id.login_btn_login);
        mRegister = findViewById(R.id.login_btn_register);
        mForgotPassword = findViewById(R.id.login_forgot_pwd);

        // EditTextInputLayout
        mEditTextMailLayout = findViewById(R.id.login_email);
        mEditTextMail = findViewById(R.id.login_sub_email);
        mEditTextpasswordLayout = findViewById(R.id.login_pwd);
        mEditTextPassoword = findViewById(R.id.login_sub_pwd);

        // Checkbox
        mCheckSave = findViewById(R.id.login_chkbox);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.login_btn_login:
                loggingUser();
                break;

            case R.id.login_btn_register:
                registerUser();
                break;

            case R.id.login_forgot_pwd:
                forgotPasswordUser();
                break;
        }
    }

    private void loggingUser() {

        if (TextUtils.isEmpty(mEditTextMail.getText())) {
            mEditTextMailLayout.setError("Example: User123@gmail.com");
            mEditTextMailLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mEditTextPassoword.getText())) {
            mEditTextpasswordLayout.setError("Example :User123user");
            mEditTextpasswordLayout.requestFocus();
            return;
        }

        final String mUserMail     = mEditTextMail.getText().toString().trim();
        final String mUserPassword = mEditTextPassoword.getText().toString().trim();

        mProgressDialog.setMessage("Logging user ");
        mProgressDialog.show();

        mFirebaseAuth.signInWithEmailAndPassword(mUserMail, mUserPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //Every time user entered password value will be override and set into database
                            mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("userPassword").setValue(mUserPassword);

                            //checking email and password for admin
                            if (Utility.FIREBASE_AUTH.getCurrentUser().getUid().equals("hnPwOtM56gTC1rqmznVBAC30Brm1")) {

                                mProgressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                finish();

                            } else {

                                mProgressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        } else {

                            mProgressDialog.dismiss();
                            Snackbar.make(findViewById(android.R.id.content), "Please enter your valid email or password", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void registerUser() {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }

    private void forgotPasswordUser() {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    private void requiredPermissions() {

        //permission is granted
        if ((ActivityCompat.checkSelfPermission(this, Utility.mReqPermissions[0]) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this, Utility.mReqPermissions[1]) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this, Utility.mReqPermissions[2]) == PackageManager.PERMISSION_GRANTED)) {

            Snackbar.make(findViewById(android.R.id.content), "Welcome to Gram Mitra", Snackbar.LENGTH_LONG).show();
            setContentView(R.layout.activity_login);

        } else {
            //permission is not granted
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Utility.mReqPermissions[0])) &&
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Utility.mReqPermissions[1])) &&
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Utility.mReqPermissions[2]))) {

                Snackbar.make(findViewById(android.R.id.content), "Need permission to load data", Snackbar.LENGTH_INDEFINITE)
                        .setAction("ENABLE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                ActivityCompat.requestPermissions(LoginActivity.this, Utility.mReqPermissions, Utility.PERMISSIONS_MULTIPLE_REQUEST);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(LoginActivity.this, Utility.mReqPermissions, Utility.PERMISSIONS_MULTIPLE_REQUEST);
            }
        }
    }

    @Override
    public void onBackPressed() {

        mBackPressedTime = System.currentTimeMillis();

        if (mBackPressedTime + 2000 > System.currentTimeMillis()) {
            mIntent = new Intent(Intent.ACTION_MAIN);
            mIntent.addCategory(Intent.CATEGORY_HOME);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mIntent);
            finish();
            super.onBackPressed();

        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
    }
}