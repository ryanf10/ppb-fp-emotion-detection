package com.example.emotiondetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class CameraActivity extends org.opencv.android.CameraActivity {
    private static String LOGTAG = "OpenCV_Log";
    private CameraBridgeViewBase cameraBridgeViewBase;
    private CascadeClassifier cascadeClassifier;
    private int cameraIndex = 1;

    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS:{
                    InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_default);
                    File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                    File cascadeFile = new File(cascadeDir, "haarcascade_frontalface_default.xml");

                    try {
                        FileOutputStream fos = new FileOutputStream(cascadeFile);
                        byte[] buffer = new byte[4096];
                        int bytesRead;

                        while((bytesRead = is.read(buffer)) != -1){
                            fos.write(buffer, 0, bytesRead);
                        }

                        is.close();
                        fos.close();

                        cascadeClassifier = new CascadeClassifier(cascadeFile.getAbsolutePath());

                        if (cascadeClassifier.empty()){
                            cascadeClassifier = null;
                        }else{
                            cascadeDir.delete();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                    Log.d(LOGTAG, "OpenCV Loaded");
                    cameraBridgeViewBase.enableView();
                    break;
                }
                default:
                    super.onManagerConnected(status);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.opencv_surface_view);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCameraIndex(this.cameraIndex);
        cameraBridgeViewBase.setCvCameraViewListener(cvCameraViewListener);
    }

    @Override
    protected List<?extends CameraBridgeViewBase> getCameraViewList(){
        return Collections.singletonList(cameraBridgeViewBase);
    }

    private CameraBridgeViewBase.CvCameraViewListener2 cvCameraViewListener = new CameraBridgeViewBase.CvCameraViewListener2() {
        @Override
        public void onCameraViewStarted(int width, int height) {

        }

        @Override
        public void onCameraViewStopped() {

        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            Mat input_rgba = inputFrame.rgba();
            Mat input_gray = inputFrame.gray();

            MatOfRect faceDetection = new MatOfRect();
            cascadeClassifier.detectMultiScale(input_gray, faceDetection, 1.3, 5);

            for (Rect rect: faceDetection.toArray()) {
                Imgproc.rectangle(input_rgba,
                        new Point(rect.x, rect.y - 50),
                        new Point(rect.x + rect.width, rect.y + rect.height + 10),
                        new Scalar(255, 0, 0),
                        2);
            }

            return input_rgba;
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOGTAG, "RESUME");
        if (!OpenCVLoader.initDebug()){
            Log.d(LOGTAG, "OpenCV not found, Initializing");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback);
        }else{
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null){
            cameraBridgeViewBase.disableView();
        }
    }

}