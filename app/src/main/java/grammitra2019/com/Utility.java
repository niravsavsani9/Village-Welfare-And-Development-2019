package grammitra2019.com;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Utility {

    //FOR FIRE BASE
    static final FirebaseAuth      FIREBASE_AUTH                = FirebaseAuth.getInstance();
    static final DatabaseReference DATABASE_REFERENCE_ROOT      = FirebaseDatabase.getInstance().getReference("Profiles");
    static final DatabaseReference DATABASE_REFERENCE_ROOT_USER = FirebaseDatabase.getInstance().getReference("Tickets");
    static final StorageReference  STORAGE_REFERENCE_TICKET     = FirebaseStorage.getInstance().getReference("UserData");

    //REQUEST PERMISSION
    static final String[] mReqPermissions              = new String[]{
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE};
    static final int      PERMISSIONS_MULTIPLE_REQUEST = 104;

    //SharedPreferences
    public static SharedPreferences getPreference(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    static final String USERMAILID   = "usermailid";
    static final String USERPASSWORD = "userpassword";

    static final int PICK_IMAGE_REQUEST = 1;
}