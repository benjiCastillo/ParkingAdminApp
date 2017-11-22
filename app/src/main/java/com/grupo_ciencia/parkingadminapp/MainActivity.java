package com.grupo_ciencia.parkingadminapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "MainActivity";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRootReference = firebaseDatabase.getReference();
    private DatabaseReference mChildReference = mRootReference.child("parking");

    private TextView txtEmail, txtNameParking,txtNameAdmin,txtLastNameAdmin, displayInteger ;
    private EditText edtSpaces_quantity;
    private ToggleButton btnDisponible;
    private Button btnUpdateSpaces;

    private GoogleApiClient googleApiClient;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private LinearLayout mainLayout;
    private ProgressBar progressBar;

    private int mMediumAnimationDuration;



    Parking parking = new Parking();
    Visitor visitor = new Visitor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);

        txtNameAdmin = (TextView) findViewById(R.id.txtNameAdmin);
        txtNameParking = (TextView) findViewById(R.id.txtNameParking);
        edtSpaces_quantity = (EditText) findViewById(R.id.edtSpaces);
        btnDisponible = (ToggleButton) findViewById(R.id.btn_toggle_disponible);
        btnUpdateSpaces = (Button) findViewById(R.id.btn_update_spaces);
        displayInteger = (TextView) findViewById(
                R.id.integer_number);
        mainLayout.setVisibility(View.INVISIBLE);

        // Retrieve and cache the system's default "short" animation time.
        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);





    firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Toast.makeText(getApplicationContext(),  firebaseUser.getEmail(),Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Usuario: " + firebaseUser.getEmail());
                } else {
                    Log.i(TAG, "SingOut");
                    goLogInScreen();
                }
            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnUpdateSpaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strSpaces = "";
                int spaces;
                strSpaces = edtSpaces_quantity.getText().toString();
                spaces = Integer.valueOf(strSpaces);

                upDateSpaces(spaces);

            }
        });

        btnDisponible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    updateDataStatus(b);
                    Log.i(TAG,updateDataStatus(b)+"");
                }else {
                    Log.i(TAG,updateDataStatus(b)+"");
                    updateDataStatus(b);
                }
            }
        });
    }



    public void logOut() {
        firebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLogInScreen();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_close_session, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
        getKey();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(R.id.menu == id){
            logOut();
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    private void goLogInScreen() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void getKey(){
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null){
            mChildReference.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int spaces;
                    String strSpaces;
                    boolean status;
                    String strStatus;

                    for(DataSnapshot child:dataSnapshot.getChildren()){

                        strSpaces = child.child("spaces_quantity").getValue().toString();
                        spaces = Integer.valueOf(strSpaces);

                        strStatus = child.child("status").getValue().toString();
                        status = Boolean.valueOf(strStatus);

                        Log.i(TAG, "user key "+child.getKey());
                        Log.i(TAG, "user ref "+child.getRef().toString());
                        Log.i(TAG, "user val "+child.getValue().toString());
                        Log.i(TAG, "user val "+child.child("name_admin").getValue().toString());
                        parking.setId_parking(child.getKey().toString());
                        parking.setName_admin(child.child("name_admin").getValue().toString());
                        parking.setLast_name_admin(child.child("last_name_admin").getValue().toString());

                        parking.setName_parking(child.child("name").getValue().toString());
                        parking.setSpaces_quantity(spaces);
                        parking.setStatus(status);

                        mainLayout.setAlpha(0f);
                        mainLayout.setVisibility(View.VISIBLE);
                        // Animate the content view to 100% opacity, and clear any animation
                        // listener set on the view.
                        mainLayout.animate()
                                .alpha(1f)
                                .setDuration(mMediumAnimationDuration)
                                .setListener(null);


                        //  progressBar.setVisibility(View.GONE);
                        progressBar.animate()
                                .alpha(0f)
                                .setDuration(mMediumAnimationDuration)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                    }
                    sendUserData();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }


    }
    private void sendUserData(){
        String spaces = "";
        int intSpaces = parking.getSpaces_quantity();
        spaces = String.valueOf(intSpaces);
        txtNameAdmin.setText(parking.getName_admin()+" "+parking.getLast_name_admin());
        edtSpaces_quantity.setText(spaces);
        displayInteger.setText(spaces);
        txtNameParking.setText(parking.getName_parking());

    }

    private void upDateSpaces(int spaces){

        if (parking.getId_parking() != null){
            Log.i(TAG," id Taxi "+parking.getId_parking());
            mChildReference.child(parking.getId_parking()).child("spaces_quantity").setValue(spaces);
            //insertNumberUser(parking.getId_parking().toString());
            checkNumberUser(parking.getId_parking().toString());
        }
    }

    private boolean updateDataStatus(boolean status){
        boolean response = false;
        if(parking.getId_parking() !=  null) {

            mChildReference.child(parking.getId_parking()).child("status").setValue(status, new DatabaseReference.CompletionListener() {

                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        System.out.println("" + databaseError.getMessage());

                    } else {
                        System.out.println("Disponibilidad Actualizada");
                    }
                }

            });
        }else{
            response = false;
        }
        return response;
    }

    //decrease and increase quantity
    public void increaseInteger(View view) {
        int minteger = 0;
        String sinte = displayInteger.getText().toString();
        minteger = Integer.valueOf(sinte);
        minteger = minteger + 1;
        upDateSpaces(minteger);
        display(minteger);
    }

    public void decreaseInteger(View view) {
        int minteger = 0;
        String sinte = displayInteger.getText().toString();
        minteger = Integer.valueOf(sinte);
        minteger = minteger - 1;
        if(minteger < 0){
            Toast.makeText(getApplicationContext(),"La cantidad no puede ser menor a 0",Toast.LENGTH_SHORT).show();
        }else{
            upDateSpaces(minteger);
            display(minteger);
        }

    }

    private void display(int number) {
        TextView displayInteger = (TextView) findViewById(
                R.id.integer_number);
        displayInteger.setText("" + number);
    }

    private void checkNumberUser(String id){
       final String  id1 = id;


        mChildReference.child(id1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("visitors").exists()){
                    findVisitors(id1);
                    insertNumberUser(id1);

                }else{
                   insertNumberUser(id1);
                    Toast.makeText(getApplicationContext(), "No existe visitor", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void insertNumberUser(String id){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy/MM/dd");
        String strDate =  mdformat.format(calendar.getTime());
        visitor.setDate(strDate);
        visitor.setQuantity(1);
        mChildReference.child(id).child("visitors").push().setValue(visitor, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(getApplicationContext(), "Ocurrio un Error : |", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Data visitor", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void findVisitors(String id){
        mChildReference.child(id).child("visitors").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                if(dataSnapshot.getKey().equals("visitors")){

//                    Log.i("FIND VISITOR ",dataSnapshot.getKey()+"");
                for(DataSnapshot child:dataSnapshot.getChildren())  {
                    Log.i("CHILD  ", child.getValue()+"");
                }

//                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void upDateNumberVisitors(String id, int quantity){

        if (parking.getId_parking() != null){
            Log.i(TAG," id Taxi "+parking.getId_parking());
            mChildReference.child(parking.getId_parking()).child("visitors").child(id).child("quantity").setValue(quantity);
           // insertNumberUser(parking.getId_parking().toString());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
