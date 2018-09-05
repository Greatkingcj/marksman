package com.huya.marksman.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.charles.ijkplayer.activitys.MainPlayerActivity;
import com.huya.marksman.R;
import com.huya.marksman.ui.select.LocalVideoActivity;
import com.huya.marksman.ui.test.TestActivity;
import com.huya.marksman.ui.wallpaper.WallpaperActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @Bind(R.id.tv_puck_game)
    TextView tv_puck_game;

    @Bind(R.id.tv_wall_paper)
    TextView tv_wall_paper;

    @Bind(R.id.tv_gl_animation)
    TextView tv_gl_animation;

    @Bind(R.id.tv_entry_anim)
    TextView tv_entry_anim;

    @Bind(R.id.tv_test_something)
    TextView tv_test_sometion;

    @Bind(R.id.tv_show_player)
    TextView tv_show_player;

    @Bind(R.id.tv_select_video)
    TextView tv_select_video;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
    }

    @OnClick(R.id.tv_puck_game)
    public void gotoFirst(View view) {
        startActivity(new Intent(getActivity(), AirHockeyActivity.class));
    }


    @OnClick(R.id.tv_wall_paper)
    public void  gotoWallpaper(View view) {
        startActivity(new Intent(getActivity(), WallpaperActivity.class));
    }

    @OnClick(R.id.tv_gl_animation)
    public void  gotoThird(View view) {
        startActivity(new Intent(getActivity(), ShatterAnimActivity.class));
    }

    @OnClick(R.id.tv_entry_anim)
    public void  gotoFour(View view) {
        startActivity(new Intent(getActivity(), EntryAnimActivity.class));
    }

    @OnClick(R.id.tv_test_something)
    public void  testSomething(View view) {
        startActivity(new Intent(getActivity(), TestActivity.class));
    }

    @OnClick(R.id.tv_show_player)
    public void  showPlayer(View view) {
        startActivity(new Intent(getActivity(), MainPlayerActivity.class));
    }

    @OnClick(R.id.tv_select_video)
    public void  selectLocalVideo(View view) {
        startActivity(new Intent(getActivity(), LocalVideoActivity.class));
    }

}
