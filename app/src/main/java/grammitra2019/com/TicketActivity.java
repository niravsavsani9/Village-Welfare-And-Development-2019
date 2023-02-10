package grammitra2019.com;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import grammitra2019.com.Model.Tickets;

public class TicketActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private Intent  mIntent;
    private Spinner mIssueSpinner, mCitySpinner;
    private TextInputEditText mEditTextIssueDescription;
    private TextInputLayout   mEditTextIssueDescriptionLayout;
    private EditText          mEditTextCustomIssue;
    private ImageView         mIssueImageView;
    private Uri               mImageUri;
    private Button            mSubmit;
    public  ProgressDialog    mProgressDialog;
    public  Tickets           mTickets;
    private String            ticketCategory;
    private LocationManager   locationManager;
    String city;
    String ticketCityName;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    Geocoder geocoder;
    List<Address> addresses;

    DatabaseReference mDatabaseTicket, mDatabaseProfiles;
    FirebaseAuth     mFirebaseAuth;
    StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        getSupportActionBar().hide();

        registerIssueIDconfigue();

        OneSignal.startInit(this).init();

        mFirebaseAuth = Utility.FIREBASE_AUTH;
        mStorageReference = Utility.STORAGE_REFERENCE_TICKET;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(TicketActivity.this);

        mDatabaseTicket = FirebaseDatabase.getInstance().getReference("Tickets");
        mDatabaseProfiles = FirebaseDatabase.getInstance().getReference("Profiles");
        mProgressDialog = new ProgressDialog(this);

        mIssueImageView.setOnClickListener(this);
        mSubmit.setOnClickListener(this);

        mIssueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mIssueSpinner.getSelectedItem().toString().equalsIgnoreCase("Other")) {
                    mEditTextCustomIssue.setVisibility(View.VISIBLE);
                } else {
                    mEditTextCustomIssue.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }


    private void registerIssueIDconfigue() {

        mIssueSpinner = findViewById(R.id.registerIssue_issueSpinner);
        mCitySpinner = findViewById(R.id.registerIssue_citySpinner);
        mIssueImageView = findViewById(R.id.registerIssue_issueImage);
        mSubmit = findViewById(R.id.registerIssue_btn_submit);
        mEditTextIssueDescriptionLayout = findViewById(R.id.registerIssue_issue_desc);
        mEditTextIssueDescription = findViewById(R.id.registerIssue_sub_issue_desc);
        mEditTextCustomIssue = findViewById(R.id.registerIssue_issueSpinner_coustom);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.registerIssue_issueImage:
                chooseImageView();
                break;

            case R.id.registerIssue_btn_submit:
                submitTicket();
                break;
        }
    }

    private void submitTicket() {

        Calendar     calendar          = Calendar.getInstance();
        final String ticketCurrentDate = DateFormat.getDateTimeInstance().format(calendar.getTime());

        if (TextUtils.isEmpty(mEditTextIssueDescription.getText())) {
            mEditTextIssueDescriptionLayout.setError("Example: Write Brief Description");
            mEditTextIssueDescriptionLayout.requestFocus();
            return;
        }

        if (mIssueSpinner.getSelectedItem().toString().equalsIgnoreCase("Other")) {

            mEditTextCustomIssue.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(mEditTextCustomIssue.getText())) {
                mEditTextCustomIssue.setError("Please write your issue");
                mEditTextCustomIssue.requestFocus();
                return;
            }
            ticketCategory = mEditTextCustomIssue.getText().toString();

        } else {
            ticketCategory = mIssueSpinner.getSelectedItem().toString();
            mEditTextCustomIssue.setVisibility(View.GONE);
        }

        if (mCitySpinner.getSelectedItem().toString().equals("Other")){
            fetchLocation();
        }
        else {
            ticketCityName = mCitySpinner.getSelectedItem().toString();
        }
        final String ticketDescription = mEditTextIssueDescription.getText().toString();
        final String ticketStatus      = "Status : Requested";

        mProgressDialog.setMessage("Sending Complaint...");
        mProgressDialog.show();

        if (mImageUri != null) {
            final StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            final String ticketImageDownloadUrl = uri.toString();
                                            final String userUID                = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                            mDatabaseProfiles.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    String name   = dataSnapshot.child("userName").getValue().toString();
                                                    String email  = dataSnapshot.child("userEmail").getValue().toString();
                                                    String mobile = dataSnapshot.child("userMobile").getValue().toString();
                                                    String age    = dataSnapshot.child("userAge").getValue().toString();

                                                    String ticketKey = mDatabaseTicket.push().getKey();

                                                    mTickets = new Tickets(name, email, age, mobile, ticketKey, ticketCurrentDate, ticketCategory, ticketCityName, ticketDescription, ticketImageDownloadUrl, ticketStatus, userUID);
                                                    mDatabaseTicket.child(ticketKey).setValue(mTickets);

                                                    mProgressDialog.dismiss();
                                                    Snackbar.make(findViewById(android.R.id.content), "Complaint has been Registered", Snackbar.LENGTH_SHORT).show();

                                                    //sendNotificationToAdmin();

                                                    startActivity(new Intent(TicketActivity.this, MainActivity.class));
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
                                        }
                                    });
                        }
                    });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Please Attach Image", Snackbar.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
        }
    }

    private void fetchLocation() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission")
                        .setMessage("You have to give this permission to access the feature")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(TicketActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(TicketActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null){
                                onLocationChanged(location);
                            }
                        }
                    });
        }
    }

    private void sendNotificationToAdmin() {

        final String data = "89ecfa3308114318";
        OneSignal.sendTag("DEVICE_ID", data);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                int SDK_INT = Build.VERSION.SDK_INT;
                if (SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }

                try {
                    String jsonResponse;

                    URL               url = new URL("https://onesignal.com/api/v1/notifications");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setUseCaches(false);
                    con.setDoOutput(true);
                    con.setDoInput(true);

                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    con.setRequestProperty("Authorization", "Basic Mjc5ZWNiMGUtOWQzNC00NWVjLTljNWYtZjE4MjMwNjNhYzBl");
                    con.setRequestMethod("POST");

                    String strJsonBody = "{"
                            + "\"app_id\": \"9614e8be-a170-4d53-9bd4-44aab2327b27\","
                            + "\"filters\": [{\"field\": \"tag\", \"key\": \"DEVICE_ID\", \"relation\": \"=\", \"value\": \"" + data + "\"}],"
                            + "\"data\": {\"foo\": \"bar\"},"
                            + "\"contents\": {\"en\": \"New Ticket Arrived\"}"
                            + "}";


                    System.out.println("strJsonBody:\n" + strJsonBody);

                    byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                    con.setFixedLengthStreamingMode(sendBytes.length);

                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(sendBytes);

                    int httpResponse = con.getResponseCode();
                    System.out.println("httpResponse: " + httpResponse);

                    if (httpResponse >= HttpURLConnection.HTTP_OK
                            && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                        Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                        scanner.close();
                    } else {
                        Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                        scanner.close();
                    }
                    System.out.println("jsonResponse:\n" + jsonResponse);

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap     mimeTypeMap     = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void chooseImageView() {

        mIntent = new Intent();
        mIntent.setType("image/*");
        mIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(mIntent, Utility.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utility.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(mIssueImageView);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double longitute = location.getLongitude();
        double latitue   = location.getLatitude();

        try {
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(latitue, longitute, 1);
            ticketCityName = addresses.get(0).getSubLocality();

            if (ticketCityName == null){
                ticketCityName = addresses.get(0).getLocality();
            }

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
