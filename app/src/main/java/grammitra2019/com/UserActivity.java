package grammitra2019.com;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import grammitra2019.com.Model.Tickets;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    Button          mSubmit;
    TextInputLayout mEditTextUserNameLayout, mEditTextMailIdLayout, mEditTextMobileLayout, mEditTextAgeLayout;
    TextInputEditText mEditTextUsername, mEditTextMailID, mEditTextMobile, mEditTextAge;
    List<Tickets>     mTicketList;
    ProgressDialog    mProgressDialog;
    FirebaseAuth      mFireBaseAuth;
    DatabaseReference mDataBaseReferenceUser, mDataBaseReferenceTicket;
    String mFireBaseUserUID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        UserIDenque();

        mFireBaseAuth = Utility.FIREBASE_AUTH;
        mDataBaseReferenceUser = Utility.DATABASE_REFERENCE_ROOT;
        mDataBaseReferenceTicket = Utility.DATABASE_REFERENCE_ROOT_USER;
        mFireBaseUserUID = mFireBaseAuth.getCurrentUser().getUid();

        mTicketList = new ArrayList<>();

        fetchDataFromDatabase();

        mProgressDialog = new ProgressDialog(this);
        mSubmit.setOnClickListener(this);
    }

    private void fetchDataFromDatabase() {

        mDataBaseReferenceUser.child(mFireBaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name   = dataSnapshot.child("userName").getValue().toString();
                String email  = dataSnapshot.child("userEmail").getValue().toString();
                String mobile = dataSnapshot.child("userMobile").getValue().toString();
                String age    = dataSnapshot.child("userAge").getValue().toString();

                mEditTextUsername.setText(name);
                mEditTextMailID.setText(email);
                mEditTextMobile.setText(mobile);
                mEditTextAge.setText(age);

                // Email ID is not editable for user
                mEditTextMailID.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void UserIDenque() {

        mSubmit = findViewById(R.id.user_btn_submit);

        mEditTextUserNameLayout = findViewById(R.id.user_name);
        mEditTextMailIdLayout = findViewById(R.id.user_email);
        mEditTextMobileLayout = findViewById(R.id.user_mobile);
        mEditTextAgeLayout = findViewById(R.id.user_age);

        mEditTextUsername = findViewById(R.id.user_sub_name);
        mEditTextMailID = findViewById(R.id.user_sub_email);
        mEditTextMobile = findViewById(R.id.user_sub_mobile);
        mEditTextAge = findViewById(R.id.user_sub_age);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.user_btn_submit:
                UpdateDetailsUser();
                break;
        }
    }

    private void UpdateDetailsUser() {

        mProgressDialog.setMessage("Updating Data");
        mProgressDialog.show();
        final String userName, userMobile, userAge;

        userName = mEditTextUsername.getText().toString().trim();
        userMobile = mEditTextMobile.getText().toString().trim();
        userAge = mEditTextAge.getText().toString().trim();

        mDataBaseReferenceTicket.orderByChild("userUID").equalTo(mFireBaseUserUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ticketUserSnapshot : dataSnapshot.getChildren()) {
                    String key = ticketUserSnapshot.getKey();

                    mDataBaseReferenceTicket.child(key).child("userAge").setValue(userAge);
                    mDataBaseReferenceUser.child(mFireBaseUserUID).child("userName").setValue(userName);
                    mDataBaseReferenceUser.child(mFireBaseUserUID).child("userMobile").setValue(userMobile);
                    mDataBaseReferenceUser.child(mFireBaseUserUID).child("userAge").setValue(userAge);
                }
                mProgressDialog.dismiss();
                finish();
                startActivity(new Intent(UserActivity.this, MainActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

