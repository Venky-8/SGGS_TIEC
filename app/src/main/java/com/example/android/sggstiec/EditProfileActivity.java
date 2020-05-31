package com.example.android.sggstiec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private PrimaryDrawerItem itemHome = new PrimaryDrawerItem().withIdentifier(1).withName("Home");
    private PrimaryDrawerItem itemEdit = new PrimaryDrawerItem().withIdentifier(2).withName("Edit Profile");
    private PrimaryDrawerItem itemSignOut = new PrimaryDrawerItem().withIdentifier(3).withName("Sign Out");

    private EditText firstNameEditText;
    private EditText middleNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText regIdEditText;
    private Spinner yearDropDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.my_toolbar1);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .addProfiles(MainActivity.profile)
                .withHeaderBackground(R.drawable.drawer_img)
                .withSavedInstance(savedInstanceState)
                .build();

        Drawer drawer = new DrawerBuilder()
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
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                break;
                            case 2:
                                break;
                            default:
                                AuthUI.getInstance()
                                        .signOut(getApplicationContext())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // ...
                                                finishAffinity();
                                            }
                                        });
                                break;
                        }
                        return false;
                    }
                })
                .build();

        drawer.setSelection(2, false);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        middleNameEditText = findViewById(R.id.middleNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        regIdEditText = findViewById(R.id.regIdEditText);
        yearDropDown = findViewById(R.id.spinner);
        Button update = findViewById(R.id.update_profile_button);

        String[] items = new String[]{"BTECH FY", "BTECH SY", "BTECH TY", "BTECH", "MTECH FY", "MTECH"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        yearDropDown.setAdapter(adapter);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> data = new HashMap<>();
                data.put("firstName", firstNameEditText.getText().toString());
                data.put("middleName", middleNameEditText.getText().toString());
                data.put("lastName", lastNameEditText.getText().toString());
                data.put("year", yearDropDown.getSelectedItem().toString());
                data.put("email", emailEditText.getText().toString());
                data.put("regNo", regIdEditText.getText().toString());
                db.collection("users").document(mAuth.getCurrentUser().getUid())
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                Log.d(TAG, "Updated Profile");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }
        });

        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        firstNameEditText.setText(String.valueOf(document.getData().get("firstName")));
                        middleNameEditText.setText(String.valueOf(document.getData().get("middleName")));
                        lastNameEditText.setText(String.valueOf(document.getData().get("lastName")));
                        emailEditText.setText(String.valueOf(document.getData().get("email")));
                        regIdEditText.setText(String.valueOf(document.getData().get("regNo")));
                        yearDropDown.setSelection(adapter.getPosition(String.valueOf(document.getData().get("year"))));
//                        Log.v(TAG, "Document id: " + document_id);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

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
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                                finishAffinity();
                            }
                        });
                break;
            }
            // case blocks for other MenuItems (if any)
        }
        return true;
    }
}
