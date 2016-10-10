package com.viveksb007.attendenceclient;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pub.devrel.easypermissions.EasyPermissions;

public class WelcomeActivity extends BaseActivity {

    private static final String TAG = "Welcome Activity";
    private static final int RC_READ_PHONE_STATE = 101;
    private static final int RC_CHANGE_WIFI_STATE = 102;
    private TextView tvDeviceID;
    private PrefManager manager;
    private String DEVICE_ID;
    private String USER_NAME;
    private Spinner year;
    private Spinner branches;
    private EditText section;
    private EditText rollNo;
    private EditText etUname;
    private String stYear;
    private String stBranch;
    private String stSection;
    private String stRollNo;
    private boolean UserAuthenticated = false;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new PrefManager(this);
        if (manager.isFirstTime()) {
            getPermissions();
        } else {
            gotoNextActivity();
        }
        setContentView(R.layout.activity_welcome);

        // Firebase Anonymous Authentication for Storing initial Details //
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(WelcomeActivity.this, "Check your Internet Connection or drop me a message ( See About )", Toast.LENGTH_SHORT).show();
                } else {
                    UserAuthenticated = true;
                    database = FirebaseDatabase.getInstance();
                }
            }
        });
        //
        tvDeviceID = (TextView) findViewById(R.id.tv_device_id);

        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_PHONE_STATE)) {
            DEVICE_ID = getDeviceID(this);
            tvDeviceID.setText("Device ID : " + DEVICE_ID);
        }

        year = (Spinner) findViewById(R.id.spinner_year);
        branches = (Spinner) findViewById(R.id.spinner_branches);
        section = (EditText) findViewById(R.id.et_select_section);

        rollNo = (EditText) findViewById(R.id.et_select_roll_no);
        rollNo.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        Button btnRegister = (Button) findViewById(R.id.btn_register);
        etUname = (EditText) findViewById(R.id.et_user_name);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateForm()) {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.ll_welcome_activity), "Form Not Filled Correctly", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else if (UserAuthenticated) {
                    manager.setFirstLaunch(false);
                    registerUser();
                    gotoNextActivity();
                }
            }
        });

    }

    private boolean validateForm() {
        return !String.valueOf(year.getSelectedItem()).equals("") &&
                !String.valueOf(branches.getSelectedItem()).equals("") &&
                !section.getText().toString().trim().equals("") &&
                !etUname.getText().toString().trim().equals("") &&
                !rollNo.getText().toString().trim().equals("");
    }

    private void gotoNextActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void registerUser() {
        manager.setUserNameAndDeviceID(USER_NAME, DEVICE_ID);
        stYear = String.valueOf(year.getSelectedItem());
        stBranch = String.valueOf(branches.getSelectedItem());
        stSection = section.getText().toString();
        stRollNo = rollNo.getText().toString();

        manager.initialSelection(stYear, stBranch, stSection, stRollNo);

        // Register with Firebase { DEVICE-ID: NAME } //
        reference = database.getReference(stYear).child(stBranch).child(stSection);
        reference.child(DEVICE_ID).setValue(stRollNo);
        //

    }

    private void getPermissions() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_PHONE_STATE)) {
            EasyPermissions.requestPermissions(this, "This app needs to Read Device ID for classification", RC_READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
        }
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.CHANGE_WIFI_STATE)) {
            EasyPermissions.requestPermissions(this, "This app needs this permission to function properly", RC_CHANGE_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DEVICE_ID = getDeviceID(this);
        tvDeviceID.setText("Device ID : " + DEVICE_ID);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public static String getDeviceID(Context context) {
        final String deviceID = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (deviceID != null) {
            return deviceID;
        } else {
            return Build.SERIAL;
        }
    }

}
