package com.enipro.presentation.signup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.enipro.R;

import butterknife.ButterKnife;

public class VerifyMobileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);
        ButterKnife.bind(this);
    }
}
