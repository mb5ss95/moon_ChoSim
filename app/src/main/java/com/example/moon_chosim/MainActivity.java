package com.example.moon_chosim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    /*
    https://salix97.tistory.com/54
    https://brunch.co.kr/@mystoryg/54

    1. SurfaceView, SurfaceHolder 정의
    2. SurfaceHolder.Callback 인터페이스 오버라이딩
    3. CameraManager.getCameraIdList(), CameraCharacteristics --카메라 정보 가져오기
    4. CameraDevice.StateCallback
    인터페이스 오버라이딩 --카메라 장치의 상태에 대한 업데이트를 수신하기위한 콜백
    카메라 열때

    5. CameraCaptureSession.StateCallback
    인터페이스 오버라이딩 --카메라 캡처 세션의 상태에 대한 업데이트를 수신하기위한 콜백
    프리뷰 가져올때

    6. CameraCaptureSession.CaptureCallback
    인터페이스 오버라이딩 --CaptureRequest 카메라 장치에 제출 된 진행 상황을 추적하기위한 콜백
     */


    ImageReader imageReader;
    ImageView img;
    SurfaceHolder surfaceHolder;
    SurfaceView surfaceView1, surfaceView2;
    CameraDevice mCamera;
    CameraManager cameraManager;
    LinearLayout drawlinear;
    drawPaint_View m;
    String TAG = "Main moon";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView1 = findViewById(R.id.sur1);
        //surfaceView2 = findViewById(R.id.sur2)

        m = new drawPaint_View(this);
        drawlinear = findViewById(R.id.ll);
        drawlinear.addView(m);
        drawlinear.bringToFront();
        /*
        drawlinear.setLeft(surfaceView1.getRight());
        drawlinear.setTop(surfaceView1.getBottom());
        drawlinear.setRight(surfaceView1.getLeft());
        drawlinear.setBottom(surfaceView1.getTop());
        */
        init_wiget();
        init_view();
    }

    private void init_wiget() {
        findViewById(R.id.btn1).setOnClickListener(view -> {

        });
        findViewById(R.id.btn2).setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            try {
                mCamera.close();
                m.set_turn(false);
                cameraManager.openCamera(String.valueOf(CameraCharacteristics.LENS_FACING_BACK), cameraDeviceCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.btn3).setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            try {
                mCamera.close();
                m.set_turn(true);
                cameraManager.openCamera(String.valueOf(CameraCharacteristics.LENS_FACING_FRONT), cameraDeviceCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        });
        img = findViewById(R.id.img);
        float[] gray = {
                0.2989f, 0.5870f, 0.1140f, 0, 0,
                0.2989f, 0.5870f, 0.1140f, 0, 0,
                0.2989f, 0.5870f, 0.1140f, 0, 0,
                0.0000F, 0.0000F, 0.0000F, 1, 0
        };

        img.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(gray)));
    }

    public void init_view() {
        surfaceHolder = surfaceView1.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                try {
                    init_camera();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
            }
        });
    }

    public void init_camera() throws CameraAccessException {
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        //String CameraID = String.valueOf(CameraCharacteristics.LENS_FACING_FRONT);
        String CameraID = cameraManager.getCameraIdList()[0];
        CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(CameraID);
        m.set_size(characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE));

        /*
        LENS_FACING_FRONT: 전면 카메라. value : 0
        LENS_FACING_BACK: 후면 카메라. value : 1
        LENS_FACING_EXTERNAL: 기타 카메라. value : 2
         */

        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size size = map.getOutputSizes(ImageFormat.JPEG)[0];
        setAspectRatioTextureView(size.getHeight(), size.getWidth());
        Log.d(TAG, size.getWidth() + " gvgfg" + size.getHeight());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //level이 0으로 설정되어 있다면 제한 모드로 작동. 1로 설정되어 있다면 풀모드로 작동
        // 카메라 시스템에서 제공할 수 있는 프레임-레이트 범위를 가지고 옴
        int level = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        Range<Integer> fps[] = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);

        Log.d(TAG, "frame rate is :" + fps[fps.length - 1] + "hardware level = " + level);
        // D/!!!here!!! :: frame rate is :[30, 30]hardware level = 0

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cameraManager.openCamera(CameraID, cameraDeviceCallback, null);

        } catch (Exception e) {
            Log.e(TAG, "camera open error : ", e);
        }
    }

    private void setAspectRatioTextureView(int height, int width) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        if (width > height) {
            int newWidth = displayMetrics.widthPixels;
            int newHeight = ((displayMetrics.widthPixels * width) / height);
            drawlinear.setLayoutParams(new FrameLayout.LayoutParams(newWidth, newHeight));
            surfaceView1.setLayoutParams(new FrameLayout.LayoutParams(newWidth, newHeight));
        } else {
            int newWidth = displayMetrics.widthPixels;
            int newHeight = ((displayMetrics.widthPixels * height) / width);
            drawlinear.setLayoutParams(new FrameLayout.LayoutParams(newWidth, newHeight));
            surfaceView1.setLayoutParams(new FrameLayout.LayoutParams(newWidth, newHeight));
        }
    }


    private CameraDevice.StateCallback cameraDeviceCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCamera = cameraDevice;

            try {
                imageReader = ImageReader.newInstance(surfaceView1.getWidth(), surfaceView1.getHeight(),
                        ImageFormat.JPEG, 2);
                imageReader.setOnImageAvailableListener(imageAvailableListener, null);

                mCamera.createCaptureSession(Arrays.asList(surfaceHolder.getSurface(), imageReader.getSurface()), cameraCaptureStateCallback, null);
            } catch (CameraAccessException e) {
                Log.e(TAG, "cameraDeviceCallback onOpened : ", e);
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCamera.close();
            mCamera = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            mCamera.close();
            mCamera = null;
            Log.e(TAG, "cameraDeviceCallback onError : e");
        }
    };

    private CameraCaptureSession.StateCallback cameraCaptureStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try {
                CaptureRequest.Builder captureRequestBuilder = session.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                //captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                captureRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CameraMetadata.STATISTICS_FACE_DETECT_MODE_FULL);
                captureRequestBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_MONO);
                captureRequestBuilder.set(CaptureRequest.JPEG_THUMBNAIL_SIZE, new Size(1080,1920));
                //captureRequestBuilder.set( CaptureRequest.SCALER_CROP_REGION, new Rect(0, 0, surfaceView1.getWidth(), surfaceView1.getHeight()));
                captureRequestBuilder.addTarget(surfaceHolder.getSurface());


                //CaptureRequest.Builder icaptureRequestBuilder = session.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                //icaptureRequestBuilder.addTarget(imageReader.getSurface());

                session.setRepeatingRequest(captureRequestBuilder.build(), captureCallback, null);
                //session.setRepeatingBurst(Arrays.asList(captureRequestBuilder.build(), icaptureRequestBuilder.build()), captureCallback, null);
            } catch (CameraAccessException e) {
                Log.e(TAG, "cameraCaptureStateCallback onConfigured : ", e);
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
            Log.e(TAG, "cameraCaptureStateCallback onConfigureFailed : ConfigureFailed");
        }
    };

    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            //Integer mode = result.get(CaptureResult.STATISTICS_FACE_DETECT_MODE);

            //Rect rect = request.get(CaptureRequest.SCALER_CROP_REGION);
            //System.out.println("ttttttttt"+rect.left+", "+rect.top+", "+ rect.right+", "+rect.bottom);
            //ttttttttt0, 0, 4032, 3024
            try {
                Face[] faces = result.get(CaptureResult.STATISTICS_FACES);
                Log.d(TAG, "captureCallback faces length : " + faces.length);
                m.set_face(faces);
            }
            catch (NullPointerException e){
                Log.e(TAG, "captureCallback : " + e);
                return;
            }
        }
    };

    private ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();

            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            image.close();
            buffer.clear();


            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Matrix m = new Matrix();
            m.postRotate(90);

            img.setImageBitmap(bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true));


            //System.out.println(bitmap.getWidth()+"ttttttttttttttttttttttt"+bitmap.getHeight());
            //1440ttttttttttttttttttttttt1080
            reader.discardFreeBuffers();
        }
    };
}