package com.huya.marksman.ui.user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.huya.marksman.R;
import com.huya.marksman.data.db.UserDatabase;
import com.huya.marksman.data.userdao.UserRepository;
import com.huya.marksman.data.userdao.UsersLocalDataSource;
import com.huya.marksman.util.ActivityUtils;
import com.huya.marksman.util.AppExecutors;

public class UserDetailActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "USER_ID";
    UserDetailContract.Presenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detial);

        // Get the requested task id
        String userId = getIntent().getStringExtra(EXTRA_USER_ID);

        UserDetailFragment userDetialFragment = (UserDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (userDetialFragment == null) {
           userDetialFragment = UserDetailFragment.newInstance(userId);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    userDetialFragment, R.id.contentFrame);
        }

        UserDatabase userDatabase = UserDatabase.getInstance(this);
        UsersLocalDataSource usersLocalDataSource = UsersLocalDataSource.getInstance(new AppExecutors(), userDatabase.userDao());
        UserRepository repository = UserRepository.getInstance(usersLocalDataSource);

        presenter = new UserDetailPresenter(userDetialFragment, repository, userId);
    }
}
