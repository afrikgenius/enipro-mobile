package com.enipro.presentation.signup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.enipro.R;

import butterknife.BindFont;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VerifyEmailActivity extends AppCompatActivity {

    @BindView(R.id.email_edit)
    EditText email_edit;
    @BindView(R.id.send_ver_email)
    Button email_verification_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        ButterKnife.bind(this);
    }
}
