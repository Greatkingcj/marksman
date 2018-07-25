package com.huya.marksman.ui.user;

import com.huya.marksman.data.userdao.User;
import com.huya.marksman.data.userdao.UserDataSource;
import com.huya.marksman.data.userdao.UserRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by charles on 2018/7/25.
 */

public class UserListPresenter implements UserListContract.Presenter{

    private final UserRepository userRepository;

    private final UserListContract.View userView;

    public UserListPresenter(UserRepository userRepository, UserListContract.View userView) {
        this.userRepository = userRepository;
        this.userView = userView;
        userView.setPresenter(this);
    }

    @Override
    public void start() {
        loadUsers();
    }

    @Override
    public void loadUsers() {
        userRepository.getUsers(new UserDataSource.LoadUsersCallback() {
            @Override
            public void onUsersLoaded(List<User> users) {
                for (int i = 0; i < 5; i ++) {
                    User user = new User("charles", "18663789908", "tial@gmail.com");
                    users.add(user);
                }
                processUsers(users);
            }

            @Override
            public void onDataNotAvailable() {
                //todo: process data available
                List<User> users = new ArrayList<>();
                for (int i = 0; i < 5; i ++) {
                    User user = new User("charles", "18663789908", "tial@gmail.com");
                    users.add(user);
                }
                processUsers(users);
            }
        });
    }

    @Override
    public void openUserDetials(User user) {
        userView.showUserDetailsUi(user.getId());
    }

    private void processUsers(List<User> users) {
        if (users.isEmpty()) {
            //show no users or toast something
        } else {
            userView.showUsers(users);
        }
    }
}
