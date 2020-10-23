package com.camera;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends AppCompatActivity {
    private Camera camera;
    private CameraPreview cameraPreview;
    private boolean IfPreview=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //隐藏action bar
        getSupportActionBar().hide();

        boolean ifcamera=checkCameraHardware(MainActivity.this);
        // Create an instance of Camera
        if(ifcamera) {
            camera = getCameraInstance();
            IfPreview=true;
        }

        camera.setDisplayOrientation(90);

        // 创建cameraPreview并加载
        cameraPreview = new CameraPreview(this, camera);
        FrameLayout preview = findViewById(R.id.framelayout);
        preview.addView(cameraPreview);

        LinearLayout linearLayout=findViewById(R.id.linearLayout);
        ImageButton takepicture=findViewById(R.id.btn_takepicture);
        ImageButton view=findViewById(R.id.btn_view);
        ImageButton database=findViewById(R.id.btn_database);
        //将按钮位于Framelayout最上方
        linearLayout.bringToFront();

        //对照相按钮实现监听器
        takepicture.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        camera.takePicture(null, null, mPicture);
                    }
                });


        //对查看图片按钮实现监听器
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ViewPicture.class);
                startActivity(intent);
            }
        });

        //对数据库按钮实现监听器
        database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,DataBase.class);
                startActivity(intent);
            }
        });


    }

    /**
     * 实现相片的回调函数
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if(camera!=null){
                camera.stopPreview();
                IfPreview=false;
            }
            final Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null){//对象为空，则文件不存在，直接返回
                Log.d("camera", "Error creating media file, check storage permissions: " );
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);  //将图片内容压缩为JPEG格式输出到输出流对象中
                fos.flush();                                                //将缓冲区中的数据全部写出到输出流中
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("camera", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("camera", "Error accessing file: " + e.getMessage());
            }

            Toast.makeText(MainActivity.this, "照片保存至：" + pictureFile, Toast.LENGTH_LONG).show();
            resetCamera(); //调用重新预览resetCamera()方法
        }


    };

    /**
     * 文件存储：创建一个文件保存照片，下面实现两种方法
     */
    private  Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }

    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        //在使用之前最好先检查sd卡是否挂载
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES),this.getResources().getString(R.string.picture_path));
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            //如果创建失败，返回null
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        //根据当前时间创建文件名
        String fileName = System.currentTimeMillis() + ".jpg"; //将获取当前系统时间设置为照片名称
        File mediaFile=new File(mediaStorageDir.getPath(), fileName);  //创建文件对象
        return mediaFile;
    }

    /**
     * 检查是否有相机
     * */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 安全获取一个照相机的实例
     * */
    public static Camera getCameraInstance(){
        Camera camera = null;
        try {
            camera = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return camera;
    }

    /**
     * 当Activity暂停时释放相机资源
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {        //如果相机不为空
            camera.setPreviewCallback(null);
            camera.stopPreview();    //停止预览
          //  camera.release();        //释放资源
        //    camera=null;
            IfPreview=false;
        }
    }

    /**
     * 当Activity重新执行时恢复相机资源
     */
    /*@Override
    protected void onResume(){
        super.onResume();
        if(camera==null){
            camera = getCameraInstance();
            resetCamera();
        }
    }*/

    /**
     * 创建resetCamera()方法，实现重新预览功能
     */
    private void resetCamera() {
        if (!IfPreview) {            //如果为非预览模式
            camera.startPreview();    //开启预览
            IfPreview = true;
        }
    }
}



