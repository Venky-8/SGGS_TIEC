package com.example.android.sggstiec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class CheckOutActivity extends AppCompatActivity {

    private static final String TAG = "CheckOutActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String document_id;
    SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.sggstiec";
    boolean isServiceRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        findViewById(R.id.indeterminateBar).setVisibility(View.INVISIBLE);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
//        int isCheckedIn = mPreferences.getInt("isCheckedIn", 0);
        document_id = mPreferences.getString("document_id", "");
        isServiceRunning = mPreferences.getBoolean("isServiceRunning", false);

        Log.d(TAG, "isServiceRunning: " + isServiceRunning);

        if (!isServiceRunning) {
            Log.d(TAG, "Service started");
            Intent serviceIntent = new Intent(CheckOutActivity.this, MyForegroundService.class);
            serviceIntent.setAction(MyForegroundService.ACTION_START_FOREGROUND_SERVICE);
            startService(serviceIntent);
        }

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean("isServiceRunning", true);
        preferencesEditor.apply();

        Button checkOut = findViewById(R.id.checkOutButton);
        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findViewById(R.id.indeterminateBar).setVisibility(View.VISIBLE);

                DocumentReference attendanceRef = db.collection("attendance").document(document_id);
                attendanceRef
                        .update("time_out", FieldValue.serverTimestamp())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");

                                // Update checked in to 0 and clear document_id in shared pref file
                                mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
                                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                                preferencesEditor.putInt("isCheckedIn", 0);
                                preferencesEditor.putString("document_id", "");
                                preferencesEditor.putBoolean("isServiceRunning", false);
                                preferencesEditor.apply();

                                Log.d(TAG, "Service stopped");

                                Intent intent = new Intent(CheckOutActivity.this, MyForegroundService.class);
                                intent.setAction(MyForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                                startService(intent);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishAffinity();
    }
}
