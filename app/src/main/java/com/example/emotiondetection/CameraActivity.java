package com.example.emotiondetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.example.emotiondetection.ml.Model;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class CameraActivity extends org.opencv.android.CameraActivity {
    private static String LOGTAG = "OpenCV_Log";
    private CameraBridgeViewBase cameraBridgeViewBase;
    private CascadeClassifier cascadeClassifier;

    private static final int FRONT_CAMERA_INDEX = 1;
    private static final int REAR_CAMERA_INDEX = 0;
    private int cameraIndex = FRONT_CAMERA_INDEX;

    private ArrayList<String> results;

    private boolean isShowCapture = false, isPrepareCapture = false;
    private Mat capture;

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

        this.results = new ArrayList<>();

        cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.opencv_surface_view);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCameraIndex(this.cameraIndex);
        cameraBridgeViewBase.setCvCameraViewListener(cvCameraViewListener);
    }

    @Override
    protected List<?extends CameraBridgeViewBase> getCameraViewList(){
        return Collections.singletonList(cameraBridgeViewBase);
    }

    private Prediction mapPrediction(float[] confidences){
        String[] labels = {"Angry", "Disgusted", "Fearful", "Happy", "Neutral", "Sad", "Surprised"};

        int maxPos = -1;
        float max = -1.0f;
        for (int i = 0; i < confidences.length; i++){
            if(confidences[i] > max){
                maxPos = i;
                max = confidences[i];
            }
        }

        return new Prediction(labels[maxPos], max * 100);
    }

    private ByteBuffer imagePreprocess(Mat input_gray, Rect rect){
        //crop image
        Mat roi = new Mat(input_gray, rect);
        Mat resize = new Mat();
        Size size = new Size(48,48);
        Imgproc.resize(roi, resize, size);
        byte[] bytes = new byte[resize.rows() * resize.cols() * resize.channels()];
        resize.get(0,0, bytes);

        Bitmap bitmap = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(resize, bitmap);

        // Creates inputs for reference.
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 48 * 48 * 1);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[48 * 48];
        bitmap.getPixels(intValues, 0, 48, 0, 0, 48, 48);

        int pixel = 0;
        for(int i = 0; i < 48; i++){
            for(int j = 0; j < 48; j++){
                int val = intValues[pixel++];
                byteBuffer.putFloat(val & 0xFF);
            }
        }

        return byteBuffer;
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
            if(isShowCapture){
                if(results.size() == 0){
                    CameraActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Coba ulangi lagi", Toast.LENGTH_SHORT).show();
                        }
                    });

                    isShowCapture = false;
                }else{
                    CameraActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String text = "Terima Kasih\n";
                            for (String result: results){
                                text += result + "\n";
                            }
                            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                        }
                    });

                    try {
                        Thread.sleep(3000);
                        isShowCapture = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    return capture;
                }
            }
            Mat input_rgba = inputFrame.rgba();
            Mat input_gray = inputFrame.gray();

            MatOfRect faceDetection = new MatOfRect();
            cascadeClassifier.detectMultiScale(input_gray, faceDetection, 1.3, 5);

            results = new ArrayList<>();

            for (Rect rect: faceDetection.toArray()) {
                Imgproc.rectangle(input_rgba,
                        new Point(rect.x, rect.y - 50),
                        new Point(rect.x + rect.width, rect.y + rect.height + 10),
                        new Scalar(255, 0, 0),
                        2);

                try {
                    Model model = Model.newInstance(getApplicationContext());

                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 48, 48, 1}, DataType.FLOAT32);

                    // Preprocess image and load to buffer
                    inputFeature0.loadBuffer(imagePreprocess(input_gray, rect));

                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    float[] confidences = outputFeature0.getFloatArray();
                    Prediction prediction = mapPrediction(confidences);
                    Imgproc.putText(input_rgba, String.format("%s (%.2f%%)", prediction.getLabel(), prediction.getProbability()), new Point(rect.x + 20, rect.y - 60), Imgproc.FONT_HERSHEY_SIMPLEX, 2,  new Scalar(0, 255, 0), 2, Imgproc.LINE_AA);

                    results.add(prediction.getLabel());

                    // Releases model resources if no longer used.
                    model.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(isPrepareCapture) {
                capture = input_rgba;
                isPrepareCapture = false;
                isShowCapture = true;
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

    public void switchCamera(View v){
        if (this.cameraIndex == REAR_CAMERA_INDEX){
            this.cameraIndex = FRONT_CAMERA_INDEX;
        }else{
            this.cameraIndex = REAR_CAMERA_INDEX;

        }
        cameraBridgeViewBase.disableView();
        cameraBridgeViewBase.setCameraIndex(cameraIndex);
        cameraBridgeViewBase.enableView();
    }

    public void capture(View v){
        if(!isShowCapture) {
            this.isPrepareCapture = true;
        }else{
            this.isShowCapture = false;
            results = new ArrayList<>();
        }
    }

}