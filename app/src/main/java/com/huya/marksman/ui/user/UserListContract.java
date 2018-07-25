package com.huya.marksman.ui.user;

import com.huya.marksman.BasePresenter;
import com.huya.marksman.BaseView;
import com.huya.marksman.data.userdao.User;

import java.util.List;

/**
 * Created by charles on 2018/7/25.
 */

public interface UserListContract {
    interface View extends BaseView<Presenter> {
        void showUsers(List<User> users);

        void showUserDetailsUi(String taskId);
    }

    interface Presenter extends BasePresenter {

        void loadUsers();

        void openUserDetials(User user);
    }
}
