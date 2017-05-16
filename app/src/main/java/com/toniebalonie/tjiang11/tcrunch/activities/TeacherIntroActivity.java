package com.toniebalonie.tjiang11.tcrunch.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.toniebalonie.tjiang11.tcrunch.fragments.IntroSlide;
import com.toniebalonie.tjiang11.tcrunch.R;

/**
 * Created by tjiang11 on 5/7/17.
 */

public class TeacherIntroActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(IntroSlide.newInstance(R.layout.teacher_intro_1));
        addSlide(IntroSlide.newInstance(R.layout.teacher_intro_2));
        addSlide(IntroSlide.newInstance(R.layout.teacher_intro_3));
        addSlide(IntroSlide.newInstance(R.layout.teacher_intro_4));
        addSlide(IntroSlide.newInstance(R.layout.teacher_intro_5));
        addSlide(IntroSlide.newInstance(R.layout.teacher_intro_6));
        addSlide(IntroSlide.newInstance(R.layout.teacher_intro_7));
        addSlide(IntroSlide.newInstance(R.layout.teacher_intro_8));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        finish();
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        finish();
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
