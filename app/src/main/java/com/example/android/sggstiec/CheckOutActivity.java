package com.example.android.sggstiec;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class CheckOutActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Chronometer simpleChronometer;
    String document_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        simpleChronometer = findViewById(R.id.simpleChronometer);
        simpleChronometer.start();

        Intent intent = getIntent();
        document_id = intent.getStringExtra("document_id");

        Button checkOut = findViewById(R.id.checkOutButton);
        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleChronometer.stop();
                DocumentReference attendanceRef = db.collection("attendance").document(document_id);
                attendanceRef.update("time_out", FieldValue.serverTimestamp());
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }
}
