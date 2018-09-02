package com.charles.editor.cutter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.charles.editor.R;
import com.charles.editor.common.widget.videotimeline.RangeSliderViewContainer;
import com.charles.editor.common.widget.videotimeline.VideoProgressController;
import com.charles.editor.editor.IVideoEditor;
import com.charles.editor.editor.VideoEditerActivity;
import com.charles.editor.editor.VideoEditerWrapper;
import com.charles.editor.utils.TCUtils;


/**
 * Created by hans on 2017/11/6.
 * <p>
 * 视频裁剪的Fragment
 */
public class TCCutterFragment extends Fragment {

    private static final String TAG = "TCCuterFragment";
    private TextView mTvTip;
    private VideoProgressController mActivityVideoProgressController;

    private long mVideoDuration;
    private long mCutterStartTime;
    private long mCutterEndTime;
    private long mCurrentDuration;

    private IVideoEditor mTXVideoEditer;
    private RangeSliderViewContainer mCutterRangeSliderView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cutter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        VideoEditerWrapper wrapper = VideoEditerWrapper.getInstance();
        mTXVideoEditer = wrapper.getEditer();
        mVideoDuration = mTXVideoEditer.getTXVideoInfo().duration;

        mCutterStartTime = 0;
        mCutterEndTime = mVideoDuration;

        mActivityVideoProgressController = ((VideoEditerActivity) getActivity()).getVideoProgressViewController();
        initViews(view);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        if (mCutterRangeSliderView != null) {
//            mCutterRangeSliderView.setVisibility(hidden ? View.GONE : View.VISIBLE);
//        }
    }

    private void initViews(View view) {
        mTvTip = (TextView) view.findViewById(R.id.cutter_tv_tip);

        initRangeSlider();
    }

    private void initRangeSlider() {
        mCutterRangeSliderView = new RangeSliderViewContainer(getActivity());
        mCutterRangeSliderView.init(mActivityVideoProgressController, 0, mVideoDuration, mVideoDuration);
        mCutterRangeSliderView.setDurationChangeListener(mOnDurationChangeListener);
        mActivityVideoProgressController.addRangeSliderView(mCutterRangeSliderView);
    }


    private RangeSliderViewContainer.OnDurationChangeListener mOnDurationChangeListener = new RangeSliderViewContainer.OnDurationChangeListener() {
        @Override
        public void onDurationChange(long startTime, long endTime) {
            mCutterStartTime = startTime;
            mCutterEndTime = endTime;
            mTXVideoEditer.setCutFromTime(startTime, endTime);

            mTvTip.setText(String.format("左侧 : %s, 右侧 : %s ", TCUtils.duration(startTime), TCUtils.duration(endTime)));

            VideoEditerWrapper.getInstance().setCutterStartTime(startTime, endTime);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public long getCutterStartTime() {
        return mCutterStartTime;
    }

    public long getCutterEndTime() {
        return mCutterEndTime;
    }


}
