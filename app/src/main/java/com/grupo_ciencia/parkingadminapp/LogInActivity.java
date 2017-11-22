package com.grupo_ciencia.parkingadminapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    public static final String TAG = "LogInActivity";
    private EditText edtEmail, edtPassword;
    private Button btnCreate;
    private Button btnSignIn;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private int mMediumAnimationDuration;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        edtEmail = (EditText) findViewById(R.id.username);
        edtPassword = (EditText) findViewById(R.id.password);
        btnSignIn = (Button) findViewById(R.id.btnLogIn);

        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);

        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);
        progressBar.setVisibility(View.GONE);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String email = edtEmail.getText().toString();
                    String password = edtPassword.getText().toString();
                    SetUpSingIn(email, password);

                }catch (Exception e){
                    Log.i(TAG,e.getMessage());
                }
            }
        });

        firebaseAuth = firebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    goMainScreen();
                }
            }
        };


    }

    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void SetUpSingIn(String email, String password){
        progressBar.setAlpha(0f);
        progressBar.setVisibility(View.VISIBLE);
        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        progressBar.animate()
                .alpha(1f)
                .setDuration(mMediumAnimationDuration)
                .setListener(null);

        //  progressBar.setVisibility(View.GONE);
        btnSignIn.animate()
                .alpha(0f)
                .setDuration(mMediumAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        btnSignIn.setVisibility(View.VISIBLE);
                    }
                });
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Inicio de Sesion Correcta", Toast.LENGTH_SHORT).show();
                            goMainScreen();


                        }else {
                            progressBar.setVisibility(View.GONE);
                            btnSignIn.animate()
                                    .alpha(1f)
                                    .setDuration(mMediumAnimationDuration)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            btnSignIn.setVisibility(View.VISIBLE);
                                        }
                                    });
                            Toast.makeText(getApplicationContext(),"Ups! algo anda mal", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void goMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
