package com.example.tjiang11.tcrunch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.pm.ActivityInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by tjiang11 on 12/26/16.
 */

public class LoginActivity extends AppCompatActivity
        implements StudentLoginFragment.OnStudentLoginListener,
        TeacherLoginFragment.OnTeacherLoginListener{

    public static final String PREFS_NAME = "appPrefs";

    private SharedPreferences sharedPrefs;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseInstanceId mInstanceId;

    private TabLayout tabLayout;

    @Override
    public void onTeacherLoginPressed(final String email, final String password) {
        final Activity loginActivity = this;

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter an email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("Auth", "signInWithEmail:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w("Auth", "signInWithEmail:failed", task.getException());
                    Toast.makeText(LoginActivity.this, "Invalid username/password",
                            Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
                    sharedPrefsEditor.putBoolean("teacher_logged_in", true);
                    sharedPrefsEditor.putString("email", email);
                    sharedPrefsEditor.putString("password", password);
                    sharedPrefsEditor.apply();
                    Intent intent = new Intent(loginActivity, TeacherTicketListActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void onTeacherRegisterPressed(String email, String password) {
        Log.i("URI", "Teacher register pressed");
        createTeacherAccount(email, password);
    }

    public void createTeacherAccount(final String email, final String password) {
        Log.d("Auth", email);
        Log.d("Auth", password);
        final Activity loginActivity = this;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("AUTH", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (mAuth.getCurrentUser() != null) {
                            Log.d("AUTH", mAuth.getCurrentUser().getEmail());
                        }
                        if (!task.isSuccessful()) {
                            Log.e("Auth", "Sign up failed");
                            Toast.makeText(LoginActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(loginActivity, TeacherTicketListActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    public void onStudentLoginPressed(Uri uri) {
        SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
        sharedPrefsEditor.putBoolean("student_logged_in", true);
        sharedPrefsEditor.apply();
        Intent intent = new Intent(this, StudentTicketListActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (sharedPrefs.getBoolean("teacher_logged_in", false)) {
            Intent intent = new Intent(this, TeacherTicketListActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (sharedPrefs.getBoolean("student_logged_in", false)) {
            Intent intent = new Intent(this, StudentTicketListActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        mInstanceId = FirebaseInstanceId.getInstance();
        Log.i("INSTANCE", mInstanceId.getId());
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Auth", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("Auth", "onAuthStateChanged:signed_out");
                }
            }
        };

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            LoginFragment fragment = new LoginFragment();
            transaction.replace(R.id.login_content_fragment, fragment);
            transaction.commit();
        }

        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
