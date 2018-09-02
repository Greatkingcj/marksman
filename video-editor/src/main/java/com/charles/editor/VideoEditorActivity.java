package com.charles.editor;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.charles.editor.clip.VideoClipper;
import com.charles.editor.entry.CutView;
import com.charles.editor.entry.VideoInfo;

/**
 * @author charles
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class VideoEditorActivity extends AppCompatActivity implements View.OnClickListener{
    private Button clipButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_editor);
        initView();
    }

    private void initView() {
        clipButton = findViewById(R.id.btn_clip);
        clipButton.setOnClickListener(this);
    }


    private void doClipWithMediaCodec() {
        VideoClipper clipper = new VideoClipper();

        CutView cutView = CutView.withPath("inputPath");

        clipper.startClipVideo(cutView, new VideoClipper.ClipListener() {
            @Override
            public void onClipProgress(int progress) {

            }

            @Override
            public void onClipError(String error) {

            }

            @Override
            public void onClipDone(String videoPath) {

            }
        });
    }

    public static void launch(Context context, VideoInfo videoInfo) {
        Intent intent = new Intent(context, VideoEditorActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_clip) {
            doClipWithMediaCodec();
        }
    }
}
