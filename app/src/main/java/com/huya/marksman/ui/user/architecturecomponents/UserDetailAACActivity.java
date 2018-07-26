package com.huya.marksman.ui.user.architecturecomponents;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huya.marksman.R;
import com.huya.marksman.data.userdao.User;

public class UserDetailAACActivity extends AppCompatActivity {
    UserDetailViewModel userDetailViewModel;
    public static final String EXTRA_USER_ID = "USER_ID";
    private TextView name;
    private TextView email;
    private TextView phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_aac);
        // Get the requested task id
        String userId = getIntent().getStringExtra(EXTRA_USER_ID);
        initView();
        userDetailViewModel = obtainViewModel(this);
        userDetailViewModel.loadUser(userId).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                showUserDetail(user);
            }
        });
    }

    public static UserDetailViewModel obtainViewModel(AppCompatActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        UserDetailViewModel viewModel = ViewModelProviders.of(activity, factory).get(UserDetailViewModel.class);
        return viewModel;
    }

    private void initView() {
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
    }
    private void showUserDetail(User user) {
        name.setText(user.getName());
        email.setText(user.getEmail());
        phone.setText(user.getPhoneNum());
    }
}
