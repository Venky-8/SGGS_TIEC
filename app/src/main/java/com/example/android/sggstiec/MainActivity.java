package com.example.android.sggstiec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int SIGN_IN_REQUEST_CODE = 1;
    private static final int LAUNCH_SCAN_CODE_ACTIVITY = 2;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userName = "Welcome!";
    private String email = "Get Started";

    View progressOverlay;
    boolean profileFilled = false;
    private SimpleLocation location;
    private Button scan_btn;
    private Spinner purposeDropDown;

    private AccountHeader headerResult;
    private Drawer drawer;
    public static IProfile profile;
    private PrimaryDrawerItem itemHome = new PrimaryDrawerItem().withIdentifier(1).withName("Home");
    private PrimaryDrawerItem itemEdit = new PrimaryDrawerItem().withIdentifier(2).withName("Edit Profile");
    private PrimaryDrawerItem itemSignOut = new PrimaryDrawerItem().withIdentifier(3).withName("Sign Out");

    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.sggstiec";
//    String document_id;
//    boolean isCheckedIn = false;
//    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressOverlay = findViewById(R.id.progress_overlay);
        setInvisible();

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        int mIsCheckedIn = mPreferences.getInt("isCheckedIn", 0);
//        String mDocumentId = mPreferences.getString("document_id", "");

        if(mIsCheckedIn == 1) {
            startActivity(new Intent(getApplicationContext(), CheckOutActivity.class));
        }

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

//        Log.i(TAG, "UID of current user: " + currentUser.getUid());
//        Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);

        profile = new ProfileDrawerItem().withName(userName).withEmail(email).withIcon(getResources().getDrawable(R.mipmap.profile)).withIdentifier(100);
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .addProfiles(profile)
                .withSavedInstance(savedInstanceState)
                .withHeaderBackground(R.drawable.drawer_img)
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        itemHome,
                        new DividerDrawerItem(),
                        itemEdit,
                        new DividerDrawerItem(),
                        itemSignOut,
                        new DividerDrawerItem()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int) drawerItem.getIdentifier()) {
                            case 1:
                                break;
                            case 2:
                                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
                                break;
                            default:
                                Log.d(TAG, "identifier:" + (int) drawerItem.getIdentifier());
                                mAuth.signOut();
                                finishAffinity();
                                break;
                        }
                        return false;
                    }
                })
                .build();

        scan_btn = findViewById(R.id.scan_button);
        purposeDropDown = findViewById(R.id.purpose_spinner);

        String[] items = new String[]{"Study", "Project", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        purposeDropDown.setAdapter(adapter);

        location = new SimpleLocation(this);
        marshmallowGPSPremissionCheck();

        scan_btn.setVisibility(View.VISIBLE);
        scan_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                // SimpleLocation.openSettings(this) does not work
//                // if we can't access the location yet
//                if (!location.hasLocationEnabled()) {
//                    // ask the user to enable location access
//                    SimpleLocation.openSettings(MainActivity.this);
//                }
                if(profileFilled) {
                    final double latitude = location.getLatitude();
                    final double longitude = location.getLongitude();

                    // TODO
                    Toast.makeText(MainActivity.this, "Latitude: " + latitude + "Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                    float distanceInMeters = getDistance(latitude, longitude);
                    if (distanceInMeters <= 100) {
                        //TODO: CAMERA
                        startActivityForResult(new Intent(getApplicationContext(), ScanCodeActivity.class), LAUNCH_SCAN_CODE_ACTIVITY);
                    } else {
                        Toast.makeText(MainActivity.this, "You are not near TIEC Lab", Toast.LENGTH_SHORT).show();
                        startActivityForResult(new Intent(getApplicationContext(), ScanCodeActivity.class), LAUNCH_SCAN_CODE_ACTIVITY);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please complete your profile information from left top menu to mark attendance!", Toast.LENGTH_LONG).show();
                }
            }

        });

        if (currentUser == null) {
            Log.d(TAG, "Current user is null");
            signIn();
        } else {
            updateUI(currentUser);
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // make the device update its location
//        marshmallowGPSPremissionCheck();
//        location.beginUpdates();
//
//        // ...
//    }

    public void setInvisible() {
        progressOverlay.setVisibility(View.INVISIBLE);
    }
    public void setVisible() {
        progressOverlay.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "On Start Called");
    }

    @Override
    protected void onPause() {
        // stop location updates (saves battery)
        location.endUpdates();

        // ...

        super.onPause();

        Log.d(TAG, "On Pause Called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d(TAG, "On Restart Called");
    }

    private void signIn() {
        // Check if user is signed in (non-null) and update UI accordingly.
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        Log.d(TAG, "updateUI() Called");
        userName = currentUser.getDisplayName();
        email = currentUser.getEmail();
        profile.withName(userName);
        profile.withEmail(email);
        headerResult.updateProfile(profile);

        isProfileFilled();

//        Log.d(TAG, "Current user id = " + currentUser.getUid());
//        DocumentReference docRef = db.collection("users").document(currentUser.getUid());
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                        name = String.valueOf(document.getData().get("firstName"));
//                        document_id = String.valueOf(document.getData().get("document_id"));
//                        isCheckedIn = (boolean) document.getData().get("isCheckedIn");
//
//                        Log.d(TAG, "isCheckedIn = " + isCheckedIn);
//                        Log.d(TAG, "First Name = " + name);
//                        if(isCheckedIn) {
//                            startActivity(new Intent(getApplicationContext(), CheckOutActivity.class));
//                        }
//
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                updateUI(user);
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        } else if (requestCode == LAUNCH_SCAN_CODE_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                String resultText = data.getStringExtra("result");
                markAttendance(resultText);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public static String getCurrentTime(long epochTime) {
        Date date = new Date(epochTime);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
        return formatter.format(date);
    }

    private void markAttendance(String resultText) {
        if (resultText.equals("SGGS_TIEC_ENTRY")) {
            // Set progress overlay
            setVisible();

            Map<String, Object> data = new HashMap<>();
            data.put("user", db.document("users/" + mAuth.getCurrentUser().getUid()));
            data.put("time_in", FieldValue.serverTimestamp());
            data.put("time_out", FieldValue.serverTimestamp());
            data.put("purpose", purposeDropDown.getSelectedItem().toString());
//            data.put("regNo", regIdEditText.getText().toString());
            db.collection("attendance")
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String document_id = documentReference.getId();
                            Log.d(TAG, "DocumentSnapshot written with ID: " + document_id);
                            Toast.makeText(MainActivity.this, "Checked In!", Toast.LENGTH_LONG).show();
                            Intent myIntent = new Intent(getApplicationContext(), CheckOutActivity.class);
//                            myIntent.putExtra("document_id", document_id);

                            // Update user document with id
//                            DocumentReference currentUserRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
//                            currentUserRef
//                                    .update("isCheckedIn", true)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            Log.d(TAG, "User Checked in set to 1. DocumentSnapshot successfully updated!");
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Log.w(TAG, "User not checked in, not set to 1. Error updating document", e);
//                                        }
//                                    });
//
//                            currentUserRef
//                                    .update("document_id", document_id)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            Log.d(TAG, "Document id updated. DocumentSnapshot successfully updated!");
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Log.w(TAG, "User not checked in, not updated document id. Error updating document", e);
//                                        }
//                                    });

                            SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                            preferencesEditor.putInt("isCheckedIn", 1);
                            preferencesEditor.putString("document_id", document_id);
                            preferencesEditor.apply();

                            startActivity(myIntent);
//                            checkedIn(document_id);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out: {
                // do your sign-out stuff
                mAuth.signOut();
                finishAffinity();
                break;
            }
            // case blocks for other MenuItems (if any)
        }
        return true;
    }

    void isProfileFilled() {
        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if( document.get("firstName") != null && !String.valueOf(document.get("firstName")).isEmpty()
                        && document.get("middleName") != null && !String.valueOf(document.get("middleName")).isEmpty()
                        && document.get("lastName") != null && !String.valueOf(document.get("lastName")).isEmpty()
                        && document.get("email") != null && !String.valueOf(document.get("email")).isEmpty()
                        && document.get("year") != null && !String.valueOf(document.get("year")).isEmpty()) {
                            profileFilled = true;
                        }
                        Log.d(TAG, "First Name: " + document.get("firstName"));
                        Log.d(TAG, "Last Name: " + document.get("lastName"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private float getDistance(double latitude, double longitude) {
        // User location
        Location userLocation = new Location("");
        userLocation.setLatitude(latitude);
        userLocation.setLongitude(longitude);

        // TIEC Lab location
        Location targetLocation = new Location("");
        targetLocation.setLatitude(19.110851d);
        targetLocation.setLongitude(77.293924d);

        float distanceInMeters = targetLocation.distanceTo(userLocation);
        Toast.makeText(this, "Distance: " + distanceInMeters, Toast.LENGTH_SHORT).show();

        return distanceInMeters;
    }

    private void marshmallowGPSPremissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && this.checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && this.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CAMERA}
                    , MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            //   gps functions.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  gps functionality
        }
    }

}
