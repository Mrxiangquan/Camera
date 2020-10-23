package com.camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * 相机预览类
 * */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        camera.setParameters(setCameraParmeters(camera));                //设置相机参数
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // 不赞成设置，要求在3.0版本之前使用
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // 通知Camera预览图片放哪里，即将SurfaceView与相机绑定
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            mCamera.autoFocus(null);
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty.注意要在你的活动中释放相机预览
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // 确保在重新设置大小或者格式化之前停止预览
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        //停止预览
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        //重新设置代码编写
        //设置预览大小尺寸应该从方法getSupportPreviewSizes()方法中获取

        // start preview with new settings
        //开始预览
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * 设置相机的一些参数
     */
    private Camera.Parameters setCameraParmeters( Camera tempCamera){
        Camera.Parameters parameters = tempCamera.getParameters();    //获取相机参数
        parameters.setPictureFormat(PixelFormat.JPEG);    //指定图片为JPEG图片
        parameters.set("jpeg-quality", 80);            //设置图片的质量
        return parameters;
    }

}
