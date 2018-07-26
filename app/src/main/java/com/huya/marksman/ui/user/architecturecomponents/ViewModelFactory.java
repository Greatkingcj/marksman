package com.huya.marksman.ui.user.architecturecomponents;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.huya.marksman.data.userdao.UserRepository;
import com.huya.marksman.di.Injection;

/**
 * Created by charles on 2018/7/26.
 */

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private static volatile  ViewModelFactory INSTANCE;

    private final Application mApplication;

    private final UserRepository userRepository;

    public static ViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application, Injection.provideUserRepository(application));
                }
            }
        }
        return INSTANCE;
    }

    private ViewModelFactory(Application application, UserRepository repository) {
        mApplication = application;
        userRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserListViewModel.class)) {
            return (T) new UserListViewModel(mApplication, userRepository);
        } else if (modelClass.isAssignableFrom(UserDetailViewModel.class)) {
            return (T) new UserDetailViewModel(mApplication, userRepository);
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
