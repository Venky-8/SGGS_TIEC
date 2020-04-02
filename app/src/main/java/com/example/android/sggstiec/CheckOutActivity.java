package com.example.android.sggstiec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class CheckOutActivity extends AppCompatActivity {

    private static final String TAG = "CheckOutActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Chronometer simpleChronometer;
    String document_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        simpleChronometer = findViewById(R.id.simpleChronometer);
        simpleChronometer.start();

        Intent mainActivityIntent = getIntent();
        document_id = mainActivityIntent.getStringExtra("document_id");

        Intent serviceIntent = new Intent(CheckOutActivity.this, MyForegroundService.class);
        serviceIntent.setAction(MyForegroundService.ACTION_START_FOREGROUND_SERVICE);
        startService(serviceIntent);

        Button checkOut = findViewById(R.id.checkOutButton);
        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleChronometer.stop();
                DocumentReference attendanceRef = db.collection("attendance").document(document_id);
                attendanceRef
                        .update("time_out", FieldValue.serverTimestamp())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
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
    public void onBackPressed() {
        super.onBackPressed();
        //TODO: Handle when user press back in Check out activity
    }
}
