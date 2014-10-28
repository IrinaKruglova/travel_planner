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

public class WelcomeActivity extends Activity implements IApiAware<Boolean>, View.OnClickListener {

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

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_register).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        isNewUser = (view.getId() == R.id.button_register);
        String user = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();
        if (!isWord(user) || !isWord(password)) {
            Toast.makeText(this, "Username and password must contain only latin letters and digits",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        if (isNewUser) {
            Controller.getInstance().runSignupTask(user, password, this);
        }
        else {
            Controller.getInstance().runLoginTask(user, password, this);
        }
    }

    @Override
    public void onGetResponse(Boolean response) {
        mProgressBar.setVisibility(View.GONE);
        if (Boolean.TRUE.equals(response)) {
            Controller.getInstance().setUser(mUserView.getText().toString());
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

    private boolean isWord(String word) {
        for (int i = 0; i<word.length(); ++i) {
            char c = word.charAt(i);
            if (!Character.isLetter(c) && !Character.isDigit(c))
                return false;
        }
        return true;
    }
}
