package com.huya.huyaijkplayer.activitys;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import com.huya.huyaijkplayer.R;
import com.huya.huyaijkplayer.camera.FrameCallback;
import com.huya.huyaijkplayer.camera.Renderer;
import com.huya.huyaijkplayer.camera.TextureController;
import com.huya.huyaijkplayer.utils.PermissionUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraAnimActivity extends AppCompatActivity implements FrameCallback {

    private static final String TAG = "CameraAnimActivity";
    /**
     * 分别使用SurfaceView和TextureView展示画面
     * 这里Textureview的onSurfaceTextureSizeChanged第一次没有调用
     * 所以需要在onSurfaceTextureAvailable调用changed
    */
    //private SurfaceView mSurfaceView;
    private TextureView mTextureView;

    private TextureController mController;
    private Renderer mRenderer;
    private int cameraId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.askPermission(this, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10, initViewRunnable);

    }

    protected void setContentView() {
        setContentView(R.layout.activity_camera_anim);
    }

    private Runnable initViewRunnable = new Runnable() {
        @Override
        public void run() {

            //设置数据源
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mRenderer = new Camera2Renderer();
            } else {
                mRenderer = new Camera1Renderer();
            }

            setContentView();

            MediaPlayer player = null;

            //mSurfaceView = findViewById(R.id.mSurface);
            mTextureView = findViewById(R.id.mTexture);
            mController = new TextureController(CameraAnimActivity.this);
            mController.setFrameCallback(720, 1280, CameraAnimActivity.this);

            /*mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    mController.surfaceCreated(holder);
                    mController.setRenderer(mRenderer);
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    mController.surfaceChanged(width, height);
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mController.surfaceDestroyed();
                }
            });*/
            mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    mController.surfaceCreated(surface);
                    mController.setRenderer(mRenderer);
                    mController.surfaceChanged(width, height);
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    mController.surfaceChanged(width, height);
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            });

        }
    };

    @Override
    public void onFrame(final byte[] bytes, long time) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(720,1280, Bitmap.Config.ARGB_8888);
                ByteBuffer b = ByteBuffer.wrap(bytes);
                Log.d(TAG, "bitmap size: " + bitmap.getByteCount());
                Log.d(TAG, "Buffer size: " + b.capacity());
                bitmap.copyPixelsFromBuffer(b);
                saveBitmap(bitmap);
                bitmap.recycle();
            }
        }).start();
    }

    public void saveBitmap(Bitmap b) {
       String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HuyaIjkPlayer/photo/";
       File folder = new File(path);
       if (!folder.exists() && !folder.mkdirs()) {
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   Toast.makeText(CameraAnimActivity.this, "无法保存照片", Toast.LENGTH_SHORT).show();
               }
           });
           return;
       }

       long dataTake = System.currentTimeMillis();
       final String jpegName = path + dataTake + ".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CameraAnimActivity.this, "保存成功->"+jpegName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClick(View view) {
        if (view.getId() == R.id.mShutter) {
            mController.takePhoto();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mController != null) {
            mController.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mController != null) {
            mController.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mController != null) {
            mController.destroy();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode == 10, grantResults, initViewRunnable,
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraAnimActivity.this, "没有获得必要的权限", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    /**
     * 数据源 使用camera api
     */
    private class Camera1Renderer implements Renderer {

        private Camera mCamera;

        @Override
        public void onDestroy() {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }

            mCamera = Camera.open(cameraId);
            mController.setImageDirection(cameraId);
            Camera.Size size = mCamera.getParameters().getPreviewSize();
            mController.setDataSize(size.width, size.height);

            try {
                mCamera.setPreviewTexture(mController.getTexture());
                mController.getTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        mController.requestRender();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

        }

        @Override
        public void onDrawFrame(GL10 gl) {

        }
    }

    /**
     * 数据源 使用camera2 api
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class Camera2Renderer implements Renderer {

        CameraDevice mDevice;
        CameraManager mCameraManager;
        private HandlerThread mThread;
        private Handler mHandler;
        private Size mPreviewSize;

        Camera2Renderer() {
            mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            mThread = new HandlerThread("camera2");
            mThread.start();
            mHandler = new Handler(mThread.getLooper());
        }

        @Override
        public void onDestroy() {
            if (mDevice != null) {
                mDevice.close();
                mDevice = null;
            }
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            try {
                if (mDevice != null) {
                    mDevice.close();
                    mDevice = null;
                }

                CameraCharacteristics c = mCameraManager.getCameraCharacteristics(cameraId + "");
                StreamConfigurationMap map = c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
                //自定义规则，选择大小
                mPreviewSize = sizes[0];
                mController.setDataSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                mCameraManager.openCamera(cameraId + "", new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice camera) {
                        mDevice = camera;

                        try {
                            Surface surface = new Surface(mController.getTexture());
                            final CaptureRequest.Builder builder = mDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                            builder.addTarget(surface);
                            mController.getTexture().setDefaultBufferSize(
                                    mPreviewSize.getWidth(), mPreviewSize.getHeight());
                            mDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                                @Override
                                public void onConfigured(@NonNull CameraCaptureSession session) {
                                    try {
                                        session.setRepeatingRequest(builder.build(), new CameraCaptureSession.CaptureCallback() {
                                            @Override
                                            public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
                                                super.onCaptureProgressed(session, request, partialResult);
                                            }

                                            @Override
                                            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                                                super.onCaptureCompleted(session, request, result);
                                                mController.requestRender();
                                            }
                                        }, mHandler);
                                    } catch (CameraAccessException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                                }
                            }, mHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        mDevice = null;
                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {

                    }
                }, mHandler);
            } catch (SecurityException | CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

        }

        @Override
        public void onDrawFrame(GL10 gl) {

        }
    }
}
