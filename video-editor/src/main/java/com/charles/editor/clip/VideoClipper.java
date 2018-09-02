package com.charles.editor.clip;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.charles.base.engine.Engine;
import com.charles.editor.entry.CutView;
import com.charles.editor.entry.VideoInfo;
import com.charles.editor.utils.ExecuteTimeAnalyzeUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author charles
 * @date 2018/8/31
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class VideoClipper {

    private static final String TAG = "VideoClipper";
    private static final boolean VERBOSE = false;
    private ClipListener clipListener;
    private String inputVideoUrl;
    private String outputVideoUrl;
    private CutView cutView;
    private MediaExtractor mVideoExtractor;
    private MediaExtractor mAudioExtractor;
    private MediaMuxer mMediaMuxer;
    private MediaCodec videoDecoder;
    private MediaCodec videoEncoder;
    private MediaFormat videoFormat;
    private InputSurface inputSurface;
    private OutputSurface outputSurface;
    int muxVideoTrack = -1;
    int trackIndex;
    boolean videoFinish = false;
    boolean released = false;
    Object lock = new Object();
    boolean muxStarted = false;

    private long startPosition;


    public VideoClipper() {
        init();
    }

    private void init() {

    }

    public void startClipVideo(CutView cutView, ClipListener clipListener) {
        this.cutView = cutView;
        this.clipListener = clipListener;
        clipVideo();
    }

    private void clipVideo() {
        ExecuteTimeAnalyzeUtil.start();
        setUp();
    }

    private void setUp() {
        inputVideoUrl = cutView.path;
        startPosition = cutView.startPositon;
        mVideoExtractor = new MediaExtractor();
        try {
            mVideoExtractor.setDataSource(inputVideoUrl);

            mMediaMuxer = new MediaMuxer(outputVideoUrl, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            trackIndex = selectVideoTrack(mVideoExtractor);
            if (trackIndex < 0) {
                throw new RuntimeException("No video track found in " + inputVideoUrl);
            }
            videoFormat = mVideoExtractor.getTrackFormat(trackIndex);
            videoDecoder = MediaCodec.createDecoderByType(videoFormat.getString(MediaFormat.KEY_MIME));
            videoEncoder = MediaCodec.createEncoderByType(videoFormat.getString(MediaFormat.KEY_MIME));
            Engine.instance().getThreadPool().execute(videoCliper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable videoCliper = new Runnable() {
        @Override
        public void run() {
            mVideoExtractor.selectTrack(trackIndex);
            long firstVideoTime = mVideoExtractor.getSampleTime();
            mVideoExtractor.seekTo(firstVideoTime + startPosition, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
            initVideoCodec();
            startVideoCodec(videoDecoder, videoEncoder, mVideoExtractor,
                    inputSurface, outputSurface, firstVideoTime, startPosition, cutView.clipDur);
            videoFinish = true;
            relesae();
        }
    };

    private void initVideoCodec() {
        int encodeW = cutView.info.width;
        int encodeH = cutView.info.height;

        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", encodeW, encodeH);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 3000000);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        mediaFormat.setInteger(MediaFormat.KEY_ROTATION, 0);
        videoEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        inputSurface = new InputSurface(videoEncoder.createInputSurface());
        inputSurface.makeCurrent();
        videoEncoder.start();


        VideoInfo info = cutView.info;
        outputSurface = new OutputSurface(info);

        videoDecoder.configure(videoFormat, outputSurface.getSurface(), null, 0);
        videoDecoder.start();
    }

    private void startVideoCodec(MediaCodec decoder, MediaCodec encoder, MediaExtractor extractor,
                                 InputSurface inputSurface, OutputSurface outputSurface,
                                 long firstSampleTime, long startPosition, long duration) {
        ByteBuffer[] decoderInputBuffers = decoder.getInputBuffers();
        ByteBuffer[] encoderOutputBuffers = encoder.getOutputBuffers();
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        MediaCodec.BufferInfo outputInfo = new MediaCodec.BufferInfo();

        //用于判断整个编解码过程是否结束
        boolean done = false;
        boolean inputDone = false;
        boolean decodeDone = false;

        while (!done) {
            if (!inputDone) {
                int inputIndex = decoder.dequeueInputBuffer(0);
                Log.d(TAG, "inputIndex : " + inputIndex);
                if (inputIndex >= 0) {
                    ByteBuffer inputBuffer = decoderInputBuffers[inputIndex];
                    // clear inputBuffer, extractor will put data into it.
                    inputBuffer.clear();
                    int readSampleData = extractor.readSampleData(inputBuffer, 0);
                    long currentSampleTime = extractor.getSampleTime();
                    Log.d(TAG, "currentSampleTime : " + currentSampleTime);
                    //当前已经截取的视频长度
                    long dur = currentSampleTime - firstSampleTime - startPosition;
                    if (dur < duration && readSampleData > 0) {
                        decoder.queueInputBuffer(inputIndex, 0, readSampleData, extractor.getSampleTime(), 0);
                        extractor.advance();
                    } else {
                        decoder.queueInputBuffer(inputIndex, 0, 0, 0,MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                    }
                }
            }

            if (!decodeDone) {
                int index = decoder.dequeueOutputBuffer(info, 0);
                if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    //no output available yet
                } else if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {

                } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat newFormat = decoder.getOutputFormat();
                } else if (index < 0) {

                } else {
                    boolean doRender = (info.size != 0 && info.presentationTimeUs - firstSampleTime > startPosition);
                    decoder.releaseOutputBuffer(index, doRender);
                    if (doRender) {
                        // this waits for the image and renders it after it arrives.
                        outputSurface.awaitNewImage();
                        outputSurface.drawImage();

                        // send it to the encoder
                        inputSurface.setPresentationTime(info.presentationTimeUs * 1000);
                        inputSurface.swapBuffers();
                    }

                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        encoder.signalEndOfInputStream();
                        decodeDone = true;
                    }
                }
            }

            boolean encoderOutputAvailable = true;
            while (encoderOutputAvailable) {
                int encoderStatus = encoder.dequeueOutputBuffer(outputInfo, 0);
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    encoderOutputAvailable = false;
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    encoderOutputBuffers = encoder.getOutputBuffers();
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat newFormat = encoder.getOutputFormat();
                    startMux(newFormat, 0);
                } else if (encoderStatus < 0) {

                } else {
                    ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                    done = (outputInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
                    if (done) {
                        encoderOutputAvailable = false;
                    }

                    //write the data to the output "file".
                    if (outputInfo.presentationTimeUs == 0 && !done) {
                        continue;
                    }

                    if (outputInfo.size != 0) {
                        encodedData.position(outputInfo.offset);
                        encodedData.limit(outputInfo.offset + outputInfo.size);
                        if (!muxStarted) {
                            synchronized (lock) {
                                if (!muxStarted) {
                                    try {
                                        lock.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        mMediaMuxer.writeSampleData(muxVideoTrack, encodedData, outputInfo);
                    }

                    encoder.releaseOutputBuffer(encoderStatus, false);
                }

                if (encoderStatus != MediaCodec.INFO_TRY_AGAIN_LATER) {
                    //continue attempts to drain output
                    continue;
                }
            }
        }
    }

    private void startMux(MediaFormat mediaFormat, int flag) {
        if (flag == 0) {
            muxVideoTrack = mMediaMuxer.addTrack(mediaFormat);
        }

        synchronized (lock) {
            if (muxVideoTrack != -1 && !muxStarted) {
                mMediaMuxer.start();
                muxStarted = true;
                lock.notify();
            }
        }
    }

    private synchronized void relesae() {
        if (!videoFinish || released) {
            return;
        }
        mVideoExtractor.release();
        mMediaMuxer.stop();
        mMediaMuxer.release();
        if (outputSurface != null) {
            outputSurface.release();
        }

        if (inputSurface != null) {
            inputSurface.release();
        }
        videoDecoder.stop();
        videoDecoder.release();
        videoEncoder.stop();
        videoEncoder.release();

        released = true;

        if (clipListener != null) {
            clipListener.onClipDone(outputVideoUrl);
        }
    }


    /**
     * Selects the video track, if any.
     *
     * @return the track index, or -1 if no video track is found.
     */
    private static int selectVideoTrack(MediaExtractor extractor) {
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                if (VERBOSE) {
                    Log.d(TAG, "Extractor selected track " + i + " (" + mime + "): " + format);
                }
                return i;
            }
        }
        return -1;
    }

    public interface ClipListener {
        void onClipProgress(int progress);
        void onClipError(String error);
        void onClipDone(String videoPath);
    }
}
