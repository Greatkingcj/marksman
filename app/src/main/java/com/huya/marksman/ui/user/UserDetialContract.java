package com.huya.marksman.ui.user;

import com.huya.marksman.BasePresenter;
import com.huya.marksman.BaseView;
import com.huya.marksman.data.userdao.User;

import java.util.List;

/**
 * Created by charles on 2018/7/25.
 */

public class UserDetialContract {
    interface View extends BaseView<UserDetialContract.Presenter> {
        void showUser(User user);
    }

    interface Presenter extends BasePresenter {
        void openUser(String userId);
    }
}
