package com.huya.marksman.ui.user;

import com.huya.marksman.BasePresenter;
import com.huya.marksman.BaseView;
import com.huya.marksman.data.userdao.User;

/**
 * Created by charles on 2018/7/25.
 */

public class UserDetailContract {
    interface View extends BaseView<UserDetailContract.Presenter> {
        void showUser(User user);
    }

    interface Presenter extends BasePresenter {
        void openUser(String userId);
    }
}
