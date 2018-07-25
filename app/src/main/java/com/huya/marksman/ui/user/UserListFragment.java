package com.huya.marksman.ui.user;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.huya.marksman.R;
import com.huya.marksman.data.userdao.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment implements UserListContract.View {

    private UserListContract.Presenter presenter;
    private UsersAdapter usersAdapter;
    View root;

    public UserListFragment() {
        // Required empty public constructor
    }

    public static UserListFragment newInstance() {
        return new UserListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usersAdapter = new UsersAdapter(new ArrayList<User>(), itemListener);
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
        root = inflater.inflate(R.layout.fragment_user_list, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = root.findViewById(R.id.user_list);
        listView.setAdapter(usersAdapter);
    }

    UserItemListener itemListener = new UserItemListener() {
        @Override
        public void onTaskClick(User clickedUser) {
            presenter.openUserDetials(clickedUser);
        }
    };

    @Override
    public void setPresenter(UserListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showUsers(List<User> users) {
        usersAdapter.replaceData(users);
    }

    @Override
    public void showUserDetailsUi(String taskId) {
        Intent intent = new Intent(getContext(), UserDetialActivity.class);
        intent.putExtra(UserDetialActivity.EXTRA_USER_ID, taskId);
        startActivity(intent);
    }

    private static class UsersAdapter extends BaseAdapter {

        private List<User> mUsers;
        private UserItemListener itemListener;

        public UsersAdapter(List<User> users, UserItemListener itemListener) {
            this.mUsers = users;
            this.itemListener = itemListener;
        }

        public void replaceData(List<User> users) {
            this.mUsers = users;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mUsers.size();
        }

        @Override
        public User getItem(int position) {
            return mUsers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.user_item, parent, false);
            }

            final User user = getItem(position);
            TextView name = rowView.findViewById(R.id.name);
            name.setText(user.getName());
            TextView email = rowView.findViewById(R.id.email);
            email.setText(user.getEmail());
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onTaskClick(user);
                }
            });
            return rowView;
        }
    }

    public interface UserItemListener {

        void onTaskClick(User clickedUser);

    }

}
