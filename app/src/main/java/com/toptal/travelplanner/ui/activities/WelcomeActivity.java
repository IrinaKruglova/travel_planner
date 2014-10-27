package com.toptal.travelplanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.toptal.travelplanner.R;
import com.toptal.travelplanner.controller.Controller;
import com.toptal.travelplanner.controller.rest_api.IApiAware;

public class WelcomeActivity extends Activity implements IApiAware<Boolean> {

    private boolean isNewUser;

    private TextView mUserView;
    private TextView mPasswordView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mUserView = (TextView)findViewById(R.id.user);
        mPasswordView = (TextView)findViewById(R.id.password);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);

        mProgressBar.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(Controller.getInstance().getUser())) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void onLoginClick(View view) {
        isNewUser = false;
        String user = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();
        mProgressBar.setVisibility(View.VISIBLE);
        Controller.getInstance().runLoginTask(user, password, this);
    }

    public void onRegisterClick(View view) {
        isNewUser = true;
        String user = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();
        mProgressBar.setVisibility(View.VISIBLE);
        Controller.getInstance().runSignupTask(user, password, this);
    }

    @Override
    public void onGetResponse(Boolean response) {
        mProgressBar.setVisibility(View.GONE);
        if (response.equals(true)) {
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }
        else {
            Toast.makeText(this,
                    getString(isNewUser ? R.string.register_fail : R.string.login_fail),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
