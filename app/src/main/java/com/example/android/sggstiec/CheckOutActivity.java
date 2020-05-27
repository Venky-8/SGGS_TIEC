package com.example.android.sggstiec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    TextView timeTextView;
    private FirebaseAuth mAuth;
    SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.sggstiec";
    boolean isServiceRunning;
    View progressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        timeTextView = findViewById(R.id.timeTextView);
        mAuth = FirebaseAuth.getInstance();

        progressOverlay = findViewById(R.id.progress_overlay);
        setInvisible();

//        Intent mainActivityIntent = getIntent();
//        document_id = mainActivityIntent.getStringExtra("document_id");

//        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                        document_id = String.valueOf(document.getData().get("document_id"));
//                        isCheckedIn = (boolean) document.getData().get("isCheckedIn");
//                        Log.v(TAG, "Document id: " + document_id);
//                        Log.v(TAG, "is Checked in?: " + isCheckedIn);
//                        if(isCheckedIn) {
//                            Intent serviceIntent = new Intent(CheckOutActivity.this, MyForegroundService.class);
//                            serviceIntent.setAction(MyForegroundService.ACTION_START_FOREGROUND_SERVICE);
//                            startService(serviceIntent);
//                        }
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        int isCheckedIn = mPreferences.getInt("isCheckedIn", 0);
        document_id = mPreferences.getString("document_id", "");
        isServiceRunning = mPreferences.getBoolean("isServiceRunning", false);

        if(!isServiceRunning) {
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
//                DocumentReference currentUserRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
//                currentUserRef
//                        .update("isCheckedIn", false)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.d(TAG, "User Checked out set to 0. DocumentSnapshot successfully updated!");
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "User not checked out, not set to 0. Error updating document", e);
//                            }
//                        });
//
//                currentUserRef
//                        .update("document_id", "")
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.d(TAG, "User Checked out set to document id to empty. DocumentSnapshot successfully updated!");
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "User not checked out, not updated document id to empty. Error updating document", e);
//                            }
//                        });
                setVisible();
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
                                preferencesEditor.apply();

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

//    private BroadcastReceiver br = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            updateGUI(intent); // or whatever method used to update your GUI fields
//        }
//    };
//
//    private void updateGUI(Intent intent) {
//        if (intent.getExtras() != null) {
//            long elapsedMillis = intent.getLongExtra("timer", 0);
//            Log.i(TAG, "Seconds Elapsed: " +  elapsedMillis / 1000);
//
//            long seconds = elapsedMillis / 1000;
//            long minutes = seconds / 60;
//            long hours = minutes / 60;
//            String time = hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
//
//            Log.d(TAG, "Time: " + time);
//
//            timeTextView.setText(time);
//        }
//    }

    public void setInvisible() {
        progressOverlay.setVisibility(View.INVISIBLE);
    }
    public void setVisible() {
        progressOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(br, new IntentFilter(MyForegroundService.COUNTDOWN_BR));
//        Log.i(TAG, "Registered broadcast receiver");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(br);
//        Log.i(TAG, "Unregistered broadcast receiver");
    }

    @Override
    protected void onStop() {
//        try {
//            unregisterReceiver(br);
//        } catch (Exception e) {
//            // Receiver was probably already stopped in onPause()
//        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //TODO: Handle when user press back in Check out activity
    }
}
