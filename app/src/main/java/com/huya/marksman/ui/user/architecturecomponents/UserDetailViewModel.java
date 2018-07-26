package com.huya.marksman.ui.user.architecturecomponents;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.huya.marksman.data.userdao.User;
import com.huya.marksman.data.userdao.UserDataSource;
import com.huya.marksman.data.userdao.UserRepository;

/**
 * Created by charles on 2018/7/26.
 */

public class UserDetailViewModel extends AndroidViewModel{
    UserRepository userRepository;
    private LiveData<User> userLiveData;

    public UserDetailViewModel(Application application, UserRepository userRepository) {
        super(application);
        this.userRepository = userRepository;
    }

    public LiveData<User> loadUser(String userId) {
        final MutableLiveData<User> data = new MutableLiveData<>();
        userRepository.getUser(userId, new UserDataSource.GetUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                User neUser = new User("jie", "hello", "dddds");
                data.postValue(neUser);
            }

            @Override
            public void onDataNotAvailable() {
                User neUser = new User("jie", "hello", "dddds");
                data.postValue(neUser);
            }
        });
        return userLiveData = data;
    }
}
