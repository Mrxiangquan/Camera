package com.camera;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewPicture extends AppCompatActivity implements View.OnClickListener {
    //图片路径
    String dirPath;
    List<String> imageName;
    List<String> imagePath;
    List<Bitmap> viewImage;
    ImageSwitcher imageSwitcher;
    //图片Index
    private int imageIndex=0;
    //左右滑动时手指按下的X坐标
    private float touchDownX;
    //左右滑动时手指松开的X坐标
    private float touchUpX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);
        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏action bar
        getSupportActionBar().hide();
        //默认的图片文件夹
        dirPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath() + "/" + this.getResources().getString(R.string.picture_path);
        FrameLayout frameLayout=findViewById(R.id.viewFrameLayout);
        //读取文件夹下所有图片
        imagePath=getImagePath(dirPath);
        imageName=getImageName(imagePath);
        if(imagePath!=null) viewImage=getViewBitmap(imagePath);
        else {
            Toast.makeText(this, "当前文件夹目录下无图片", Toast.LENGTH_SHORT).show();
            finish();
        }

        enableButListenr();

    }

    /**
     * 为每个按钮实现监听器
     */
    private void enableButListenr(){
        //返回
        ImageButton back=findViewById(R.id.btn_back);
        back.bringToFront();
        back.setOnClickListener(this);

        //加载图片到ImageSwitcher
        imageSwitcher=findViewById(R.id.imageswitcher);
        setImageSwitch();

        //分享图片按钮
        ImageButton sharePicture=findViewById(R.id.btn_share_image);
        sharePicture.setOnClickListener(this);

        //重命名按钮
        ImageButton rename=findViewById(R.id.btn_rename);
        rename.setOnClickListener(this);

        //详情按钮
        ImageButton detail=findViewById(R.id.btn_detail);
        detail.bringToFront();
        detail.setOnClickListener(this);

        //删除按钮
        ImageButton deleteP=findViewById(R.id.btn_delete_picture);
        deleteP.setOnClickListener(this);

        //显示处理图片按钮
        ImageButton imageProcess=findViewById(R.id.btn_process);
        imageProcess.setOnClickListener(this);

        //转灰度图
        ImageButton toGray=findViewById(R.id.view_btn_togray);
        toGray.setOnClickListener(this);

        //添加椒盐噪声
        ImageButton addnoise=findViewById(R.id.view_btn_addnoise);
        addnoise.setOnClickListener(this);

        //灰度反转
        ImageButton turngray=findViewById(R.id.view_btn_turngray);
        turngray.setOnClickListener(this);

        //均值滤波器
        ImageButton average=findViewById(R.id.view_btn_average);
        average.setOnClickListener(this);

        //中值滤波器
        ImageButton median=findViewById(R.id.view_btn_median);
        median.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //返回
            case R.id.btn_back:{
                finish();
                break;
            }
            //分享图片按钮
            case R.id.btn_share_image:{
                File shareFile = new File(imagePath.get(imageIndex));
                Uri fileUri = FileProvider.getUriForFile(ViewPicture.this, BuildConfig.APPLICATION_ID+".fileprovider",shareFile);
                shareImage("分享",fileUri);
                break;
            }
            //重命名按钮
            case R.id.btn_rename:{
                rename(imagePath.get(imageIndex),imageIndex);
                break;
            }
            //详情按钮
            case(R.id.btn_detail):{
                showDetail(imagePath.get(imageIndex),imageIndex);
                break;
            }
            //删除按钮
            case(R.id.btn_delete_picture):{
                Toast.makeText(ViewPicture.this,deletePicture(imagePath.get(imageIndex)),Toast.LENGTH_SHORT).show();
                break;
            }
            //显示处理图片按钮
            case(R.id.btn_process):{
                LinearLayout lineProcess=findViewById(R.id.line_image_process);
                if(lineProcess.getVisibility()==View.VISIBLE)
                    lineProcess.setVisibility(View.GONE);
                else {
                    lineProcess.setVisibility(View.VISIBLE);
                    lineProcess.bringToFront();
                }
                break;
            }
            //图像处理按钮
            //转灰度图
            case(R.id.view_btn_togray):{
                Bitmap processImage=ImageProcess.RgbToGray(viewImage.get(imageIndex));
                choose_processimage(processImage);
                break;
            }
            //添加椒盐噪声
            case(R.id.view_btn_addnoise):{
                Bitmap processImage=ImageProcess.AddSaltNoise(viewImage.get(imageIndex),10000);
                choose_processimage(processImage);
                break;
            }
            //灰度反转
            case(R.id.view_btn_turngray):{
                Bitmap processImage=ImageProcess.TurnGray(viewImage.get(imageIndex));
                choose_processimage(processImage);
                break;
            }
            //均值滤波器
            case(R.id.view_btn_average):{
                Bitmap processImage=ImageProcess.AverageFilter(viewImage.get(imageIndex));
                choose_processimage(processImage);
                break;
            }
            //中值滤波器
            case(R.id.view_btn_median):{
                Bitmap processImage=ImageProcess.MedianFilter(viewImage.get(imageIndex));
                choose_processimage(processImage);
                break;
            }
        }
    }

    /**
     * 加载图片
     */
    private void setImageSwitch(){
        imageSwitcher.reset();
        //为ImageSwicher设置Factory，用来为ImageSwicher制造ImageView
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(ViewPicture.this); // 实例化一个ImageView类的对象
                ImageSwitcher.LayoutParams params=(ImageSwitcher.LayoutParams)imageSwitcher.getLayoutParams();
                params.gravity=Gravity.CENTER;
                imageView.setLayoutParams(params);
                imageView.setImageBitmap(viewImage.get(imageIndex));
                return imageView; // 返回imageView对象
            }
        });


        imageSwitcher.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //取得左右滑动时手指按下的X坐标
                    touchDownX = event.getX();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //取得左右滑动时手指松开的X坐标
                    touchUpX = event.getX();
                    //从左往右，看下一张
                    if (touchUpX - touchDownX > 100) {
                        //取得当前要看的图片的index
                        imageIndex = imageIndex == 0 ? viewImage.size() - 1 : imageIndex - 1;
                        //设置图片切换的动画
                        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(ViewPicture.this, R.anim.slide_in_left));
                        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(ViewPicture.this, R.anim.slide_out_right));
                        //设置当前要看的图片
                        imageSwitcher.setImageDrawable(new BitmapDrawable(viewImage.get(imageIndex)));
                        //从右往左，看上一张
                    } else if (touchDownX - touchUpX > 100) {
                        //取得当前要看的图片index
                        imageIndex = imageIndex == viewImage.size() - 1 ? 0 : imageIndex + 1;
                        //设置切换动画
                        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(ViewPicture.this, R.anim.slide_out_left));
                        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(ViewPicture.this, R.anim.slide_in_right));
                        //设置要看的图片
                        imageSwitcher.setImageDrawable(new BitmapDrawable(viewImage.get(imageIndex)));
                    }
                    return true;
                }
                return false;
            }
        });

    }

    /**
     * 获取给定文件夹下所有图片的路径
     * @param dirPath：文件夹路径
     * @return 文件夹下所有图片的路径
     */
    public static List<String> getImagePath(String dirPath) {
        File dir = new File(dirPath);
        String[] fileList = dir.list();
        List<String> imagePath=new ArrayList<String>();

        if (fileList.length == 0) return null;

        //获取所有图片路径
        for(int i=0;i<fileList.length;i++) {
            File file = new File(fileList[i]);
            if (!file.isDirectory() && isImage(file.getName())) {
                imagePath.add(dir.toString()+"/"+file.getName());
            }
        }
        return imagePath;
    }

    /**
     * 获取已获得的图片的名称
     */
    private List<String> getImageName(final List<String> imagepath){
        List<String> imagename=new ArrayList<String>();
        String path;
        for(int i=0;i<imagepath.size();i++){
            path=imagepath.get(i);
            imagename.add(path.substring(path.lastIndexOf("/")+1,path.length()));
        }
        return imagename;
    }

    /**
     * 判断是否是图片
     * @param fileName:用于判断的文件名
     * @return 根据是否是图片返回对应值
     */
    private static boolean isImage(String fileName){
        String fileEnd=fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
        if(fileEnd.equalsIgnoreCase("jpg")||
            fileEnd.equalsIgnoreCase("png")||
            fileEnd.equalsIgnoreCase("bmp")) {
            return true;
        }
        else return false;
    }

    /**
     * 将给定的图片路径数组转换为对应的位图
     * @param imagePath:图片路径数组
     * @return 位图数组
     */
    private List<Bitmap> getViewBitmap(List<String> imagePath) {
        List<Bitmap> viewBitmap=new ArrayList<Bitmap>();
        for(int i=0;i<imagePath.size();i++){
            //转换为位图
            viewBitmap.add(BitmapFactory.decodeFile(imagePath.get(i)));
        }

        return viewBitmap;
    }

    /**
     *分享图片
     */
    private void shareImage(String dlgTitle, Uri uri) {
        if (uri == null) return;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        // 设置弹出框标题
        if (dlgTitle != null && !"".equals(dlgTitle)) { // 自定义标题
            startActivity(Intent.createChooser(intent, dlgTitle));
        } else { // 系统默认标题
            startActivity(intent);
        }
    }

    /**
     * 重命名功能
     */
    private void rename(final String filePath,final int index){
        String oldName=filePath.substring(filePath.lastIndexOf("/")+1,filePath.length());
        //首先弹出一个输入新名称的对话框
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("重命名");
        final EditText editText=new EditText(this);
        editText.setText(oldName);
        builder.setView(editText);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName=editText.getText().toString();

                File file = new File(filePath);
                //重命名操作
                String path = filePath.substring(0, filePath.lastIndexOf("/")+1)+newName+filePath.substring(
                        filePath.lastIndexOf("."), filePath.length());
                File newFile = new File(path);
                file.renameTo(newFile);
                //修改保存的路径
                imagePath.set(index,path);
            }
        });
        builder.setNegativeButton("取消",null);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 详情
     */
    private void showDetail(final String filePath,final int index){
        File file=new File(filePath);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("详情");

        View dlgview = View.inflate(this, R.layout.detail, null);
        TextView textView1=dlgview.findViewById(R.id.text_detail_name);
        TextView textView2=dlgview.findViewById(R.id.text_detail_type);
        TextView textView3=dlgview.findViewById(R.id.text_detail_size);
        TextView textView4=dlgview.findViewById(R.id.text_detail_resolution);
        TextView textView5=dlgview.findViewById(R.id.text_detail_changeTime);
        TextView textView6=dlgview.findViewById(R.id.text_detail_path);

        textView1.append(file.getName());
        textView2.append("image/"+filePath.substring(filePath.lastIndexOf(".")+1,filePath.length()));
        textView3.append(ShowLongFileSzie(file.length()));
        textView4.append(viewImage.get(index).getWidth()+"×"+viewImage.get(index).getHeight());
        textView5.append(new Date(file.lastModified()).toString());
        textView6.append(filePath);

        builder.setView(dlgview);

        builder.setNegativeButton("确定",null);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    /**
     * 计算文件大小
     */
    public static String ShowLongFileSzie(Long length) {
        if (length >= 1048576) {
            return String.format("%.2f", (length / 1048576.0))+ "MB";
        } else if (length >= 1024) {
            return String.format("%.2f", (length / 1024.0)) + "KB";
        } else if (length < 1024) {
            return length + "B";
        } else {
            return "0KB";
        }
    }

    /**
     * 删除功能实现
     */
    private String deletePicture(final String filePath){
        //删除文件
        File file=new File(filePath);
        String Name=file.getName();
        file.delete();

        //删除后先显示下一张图片
        imageIndex = imageIndex == viewImage.size() - 1 ? 0 : imageIndex + 1;
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(ViewPicture.this, R.anim.slide_in_left));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(ViewPicture.this, R.anim.slide_out_right));
        imageSwitcher.setImageDrawable(new BitmapDrawable(viewImage.get(imageIndex)));

        //删除内存中此图片
        imagePath.remove(imageIndex);
        imageName.remove(imageIndex);
        viewImage.remove(imageIndex);
        return Name;
    }

    /**
     * 预览修改后的图片并选择是否保存
     */
    private void choose_processimage(final Bitmap image){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        ImageView imageView=new ImageView(this);
        imageView.setImageBitmap(image);
        builder.setView(imageView);
        builder.setTitle("预览");
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //给新保存的图片设置名称,要求不重复
                String name=imageName.get(imageIndex).substring(0,imageName.get(imageIndex).lastIndexOf("."))+"(1).jpg";
                int index=1;
                while(true){
                    int j=0;
                    for(;j<imageName.size();j++) {
                        if(name.equals(imageName.get(j))) {
                            name=name.substring(0,imageName.get(imageIndex).lastIndexOf("."))+"("+String.valueOf(++index)+").jpg";
                            break;
                        }
                    }
                    if(j==imageName.size()) break;
                }
                File pictureFile=new File(dirPath, name);  //创建文件对象
                //写入内存
                try {
                    FileOutputStream fos  = new FileOutputStream(pictureFile);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, fos);  //将图片内容压缩为JPEG格式输出到输出流对象中
                    fos.flush();                                                //将缓冲区中的数据全部写出到输出流中
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //插入图像切换器
                imagePath.add(imageIndex+1,dirPath+"/"+name);
                imageName.add(imageIndex+1,name);
                viewImage.add(imageIndex+1,image);
            }
        });
        builder.setNegativeButton("取消",null);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

}

