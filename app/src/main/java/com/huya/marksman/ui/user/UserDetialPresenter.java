package com.huya.marksman.ui.user;

import com.huya.marksman.data.userdao.User;
import com.huya.marksman.data.userdao.UserDataSource;
import com.huya.marksman.data.userdao.UserRepository;

/**
 * Created by charles on 2018/7/25.
 */

public class UserDetialPresenter implements UserDetialContract.Presenter{


    UserDetialContract.View userView;

    UserRepository userRepository;

    String userId;

    public UserDetialPresenter(UserDetialContract.View userView, UserRepository userRepository, String userId) {
        this.userView = userView;
        this.userRepository = userRepository;
        this.userId = userId;
        userView.setPresenter(this);
    }
    @Override
    public void start() {
        openUser(userId);
    }

    @Override
    public void openUser(String userId) {
        userRepository.getUser(userId, new UserDataSource.GetUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                User neUser = new User("charles", "18399895345", "tail@gmail.com");
                processUser(neUser);
            }

            @Override
            public void onDataNotAvailable() {
                User neUser = new User("charles", "18399895345", "tail@gmail.com");
                processUser(neUser);
            }
        });
    }

    private void processUser(User user) {
        userView.showUser(user);
    }
}
