package com.toniebalonie.tjiang11.tcrunch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by tjiang11 on 5/6/17.
 */

public class StudentIntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Title", "Description", R.drawable.class_icon, ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Title 2", "Description 2", R.drawable.ic_arrow_forward_white, ContextCompat.getColor(this, R.color.colorPrimary)));

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        finish();
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
