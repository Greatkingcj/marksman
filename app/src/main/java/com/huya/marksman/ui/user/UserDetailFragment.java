package com.huya.marksman.ui.user;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huya.marksman.R;
import com.huya.marksman.data.userdao.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailFragment extends Fragment implements UserDetailContract.View{

    private static final String ARGUMENT_USER_ID = "TASK_ID";
    private UserDetailContract.Presenter presenter;
    private View root;
    private TextView name;
    private TextView email;
    private TextView phone;


    public UserDetailFragment() {
        // Required empty public constructor
    }

    public static UserDetailFragment newInstance(String userId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_USER_ID, userId);
        UserDetailFragment fragment =  new UserDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_user_detial, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        name = root.findViewById(R.id.name);
        email = root.findViewById(R.id.email);
        phone = root.findViewById(R.id.phone);
    }

    @Override
    public void setPresenter(UserDetailContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showUser(User user) {
        name.setText(user.getName());
        email.setText(user.getEmail());
        phone.setText(user.getPhoneNum());
    }
}
