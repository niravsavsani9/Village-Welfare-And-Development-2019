package grammitra2019.com;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class EditTicketActivity extends AppCompatActivity implements View.OnClickListener {

    Button            mSubmit;
    TextInputEditText mEditTextTicket;
    ProgressDialog    mProgressDialog;
    FirebaseAuth      mFireBaseAuth;
    DatabaseReference mDataBaseReferenceTicket;
    String            key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ticket);
        getSupportActionBar().hide();

        mEditTextTicket = findViewById(R.id.edit_registerIssue_sub_issue_desc);
        mSubmit = findViewById(R.id.edit_registerIssue_btn_submit);
        key = getIntent().getStringExtra("TICKETKEY");

        mFireBaseAuth = Utility.FIREBASE_AUTH;
        mDataBaseReferenceTicket = Utility.DATABASE_REFERENCE_ROOT_USER;

        fetchDataFromDatabase();

        mProgressDialog = new ProgressDialog(this);
        mSubmit.setOnClickListener(this);
    }

    private void fetchDataFromDatabase() {
        mDataBaseReferenceTicket.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("ticketDescription").getValue().toString();
                mEditTextTicket.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_registerIssue_btn_submit:
                mSubmitTicket();
                break;
        }
    }

    private void mSubmitTicket() {
        mProgressDialog.setMessage("Submitting...");
        mProgressDialog.show();

        String userTicketDescription;
        userTicketDescription = mEditTextTicket.getText().toString();
        mDataBaseReferenceTicket.child(key).child("ticketDescription").setValue(userTicketDescription);
        mProgressDialog.dismiss();
        startActivity(new Intent(EditTicketActivity.this,MainActivity.class));
    }
}
