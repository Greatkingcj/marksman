package com.huya.marksman.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.huya.marksman.repository.UserRepository;
import com.huya.marksman.vo.User;

import javax.inject.Inject;

/**
 * Created by charles on 2018/6/25.
 */

public class UserProfileViewModel extends ViewModel{
    private String userId;
    private LiveData<User> user;
    private UserRepository userRepo;

    @Inject
    public UserProfileViewModel(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void init(long userId) {
        if (this.user != null) {
            return;
        }
        user = userRepo.getUser(userId);
    }

    public LiveData<User> getUser() {
        return user;
    }
}
