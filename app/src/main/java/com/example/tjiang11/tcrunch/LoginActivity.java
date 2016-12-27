package com.example.tjiang11.tcrunch;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by tjiang11 on 12/26/16.
 */

public class LoginActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener,
        StudentLoginFragment.OnFragmentInteractionListener,
        TeacherLoginFragment.OnFragmentInteractionListener{


    private TabLayout tabLayout;
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            LoginFragment fragment = new LoginFragment();
            transaction.replace(R.id.login_content_fragment, fragment);
            transaction.commit();
        }
    }
}
