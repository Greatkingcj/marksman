package com.huya.marksman.ui.user.architecturecomponents;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.huya.marksman.data.userdao.User;
import com.huya.marksman.data.userdao.UserDataSource;
import com.huya.marksman.data.userdao.UserRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by charles on 2018/7/26.
 */

public class UserListViewModel extends AndroidViewModel {
    private LiveData<List<User>> listLiveDataUsers;
    private UserRepository userRepository;

    public UserListViewModel(Application context, UserRepository repository) {
        super(context);
        this.userRepository = repository;
    }

    public LiveData<List<User>> loadUsers() {
        final MutableLiveData<List<User>> data = new MutableLiveData<>();
        userRepository.getUsers(new UserDataSource.LoadUsersCallback() {
            @Override
            public void onUsersLoaded(List<User> users) {
                for (int i = 0; i < 5; i ++) {
                    User user = new User("charles", "18663789908", "tial@gmail.com");
                    users.add(user);
                }
                data.postValue(users);
            }

            @Override
            public void onDataNotAvailable() {
                List<User> users = new ArrayList<>();
                for (int i = 0; i < 5; i ++) {
                    User user = new User("charles", "18663789908", "tial@gmail.com");
                    users.add(user);
                }
                data.postValue(users);
            }
        });
        return listLiveDataUsers = data;
    }
}
