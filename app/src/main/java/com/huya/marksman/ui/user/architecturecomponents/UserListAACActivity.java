package com.huya.marksman.ui.user.architecturecomponents;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.huya.marksman.R;
import com.huya.marksman.data.userdao.User;
import com.huya.marksman.ui.user.UserDetailActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.internal.cache.DiskLruCache;

public class UserListAACActivity extends AppCompatActivity {

    UserListViewModel userListViewModel;
    private UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_aac);
        userListViewModel = obtainViewModel(this);
        usersAdapter = new UsersAdapter(new ArrayList<User>(), itemListener);
        ListView listView = findViewById(R.id.user_list);
        listView.setAdapter(usersAdapter);
        userListViewModel.loadUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                showUsers(users);
            }
        });
    }

    public void showUsers(List<User> users) {
        usersAdapter.replaceData(users);
    }

    public void showUserDetailsUi(String taskId) {
        Intent intent = new Intent(this, UserDetailAACActivity.class);
        intent.putExtra(UserDetailAACActivity.EXTRA_USER_ID, taskId);
        startActivity(intent);
    }

    public static UserListViewModel obtainViewModel(AppCompatActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        UserListViewModel viewModel = ViewModelProviders.of(activity, factory).get(UserListViewModel.class);
        return viewModel;
    }

    UserItemListener itemListener = new UserItemListener() {
        @Override
        public void onTaskClick(User clickedUser) {
            showUserDetailsUi(clickedUser.getId());
        }
    };

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
