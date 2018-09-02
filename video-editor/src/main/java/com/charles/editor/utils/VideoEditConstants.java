package com.charles.editor.utils;

import android.graphics.Bitmap;
import android.widget.FrameLayout;

/**
 * Created by hch on 2018/8/21.
 */

public class VideoEditConstants {
    public static final int PREVIEW_RENDER_MODE_FILL_SCREEN = 1;
    public static final int PREVIEW_RENDER_MODE_FILL_EDGE = 2;
    public static final int GENERATE_RESULT_OK = 0;
    public static final int GENERATE_RESULT_FAILED = -1;
    public static final int JOIN_RESULT_OK = 0;
    public static final int JOIN_RESULT_FAILED = -1;
    public static final int VIDEO_COMPRESSED_360P = 0;
    public static final int VIDEO_COMPRESSED_480P = 1;
    public static final int VIDEO_COMPRESSED_540P = 2;
    public static final int VIDEO_COMPRESSED_720P = 3;
    public static final int SPEED_LEVEL_SLOWEST = 0;
    public static final int SPEED_LEVEL_SLOW = 1;
    public static final int SPEED_LEVEL_NORMAL = 2;
    public static final int SPEED_LEVEL_FAST = 3;
    public static final int SPEED_LEVEL_FASTEST = 4;
    public static final int ERR_UNSUPPORT_VIDEO_FORMAT = -1001;
    public static final int ERR_UNSUPPORT_LARGE_RESOLUTION = -1002;
    public static final int ERR_UNFOUND_FILEINFO = -1003;
    public static final int ERR_UNSUPPORT_AUDIO_FORMAT = -1004;
    public static final int TXEffectType_SOUL_OUT = 0;
    public static final int TXEffectType_SPLIT_SCREEN = 1;
    public static final int TXEffectType_DARK_DRAEM = 2;
    public static final int TXEffectType_ROCK_LIGHT = 3;

    public VideoEditConstants() {
    }

    public static final class TXRepeat {
        public long startTime;
        public long endTime;
        public int repeatTimes;

        public TXRepeat() {
        }
    }

    public static final class Thumbnail {
        public int count;
        public int width;
        public int height;

        public Thumbnail() {
        }
    }

    public static final class TXRect {
        public float x;
        public float y;
        public float width;

        public TXRect() {
        }
    }

    public static final class TXSpeed {
        public int speedLevel;
        public long startTime;
        public long endTime;

        public TXSpeed() {
        }
    }

    public static final class TXAnimatedPaster {
        public String animatedPasterPathFolder;
        public VideoEditConstants.TXRect frame;
        public long startTime;
        public long endTime;
        public float rotation;

        public TXAnimatedPaster() {
        }
    }

    public static final class TXPaster {
        public Bitmap pasterImage;
        public VideoEditConstants.TXRect frame;
        public long startTime;
        public long endTime;

        public TXPaster() {
        }
    }

    public static final class TXSubtitle {
        public Bitmap titleImage;
        public VideoEditConstants.TXRect frame;
        public long startTime;
        public long endTime;

        public TXSubtitle() {
        }
    }

    public static final class TXJoinerResult {
        public int retCode;
        public String descMsg;

        public TXJoinerResult() {
        }
    }

    public static final class GenerateResult {
        public int retCode;
        public String descMsg;
        public Object data;

        public GenerateResult() {
        }
    }

    public static final class PreviewParam {
        public FrameLayout videoView;
        public int renderMode;

        public PreviewParam() {
        }
    }

    public static final class ThumbGenerate{
        public ThumbGenerate(int index , long frameAtMs , Bitmap bitmap){
            this.index = index;
            this.frameAtMs = frameAtMs;
            this.bitmap = bitmap;
        }
        public int index;
        public long frameAtMs;
        public Bitmap bitmap;
    }
}
