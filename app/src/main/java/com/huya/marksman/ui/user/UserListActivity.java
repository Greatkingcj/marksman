package com.huya.marksman.ui.user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.huya.marksman.R;
import com.huya.marksman.data.db.UserDatabase;
import com.huya.marksman.data.userdao.UserDao;
import com.huya.marksman.data.userdao.UserDataSource;
import com.huya.marksman.data.userdao.UserRepository;
import com.huya.marksman.data.userdao.UsersLocalDataSource;
import com.huya.marksman.di.Injection;
import com.huya.marksman.util.ActivityUtils;
import com.huya.marksman.util.AppExecutors;

public class UserListActivity extends AppCompatActivity {

    UserListPresenter userListPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        UserListFragment userListFragment =
                (UserListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (userListFragment == null) {
            userListFragment = UserListFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    userListFragment,
                    R.id.contentFrame
            );
        }

        userListPresenter = new UserListPresenter(Injection.provideUserRepository(this), userListFragment);
    }
}
