package com.camera;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.Random;

import static java.lang.Math.random;

/**
 * 图像处理类
 */
public class ImageProcess {

    /**
     * 将一个彩色图转为灰度图
     */
    public static Bitmap RgbToGray(final Bitmap oldImage){
        int width=oldImage.getWidth();//图像宽
        int height=oldImage.getHeight();//图像高
        Bitmap newimage=oldImage.copy(oldImage.getConfig(),true);
        int gray;

        for (int x=0;x<width;x++)
            for (int y=0;y<height;y++){
                gray=(Color.red(oldImage.getPixel(x,y))*30+Color.blue(oldImage.getPixel(x,y))*59+Color.green(oldImage.getPixel(x,y))*11)/100;
                newimage.setPixel(x,y, Color.rgb(gray,gray,gray));
            }

        return newimage;
    }

    /**
     *添加椒盐噪声
     */
    public static Bitmap AddSaltNoise(final Bitmap oldImage,final int n){
        int x,y,r;
        int height=oldImage.getHeight();
        int width=oldImage.getWidth();
        Bitmap newImage=oldImage.copy(oldImage.getConfig(),true);
        Random rand=new Random();


        for(int i=0;i<n;i++){
            x=rand.nextInt(width);
            y=rand.nextInt(height);
            r=rand.nextInt(2);
            if(r==0) newImage.setPixel(x,y,Color.rgb(0,0,0));
            else if(r==1) newImage.setPixel(x,y,Color.rgb(255,255,255));
        }


        return newImage;
    }

    /**
     * 灰度反转
     */
    public static Bitmap TurnGray(Bitmap oldImage){
        Bitmap newImage=oldImage.copy(oldImage.getConfig(),true);
        int oldColor;

        for(int x = 0; x<newImage.getWidth(); x++){
            for(int y = 0; y<newImage.getHeight(); y++){
                oldColor = oldImage.getPixel(x,y);
                newImage.setPixel(x,y,Color.rgb(255-Color.red(oldColor),255-Color.green(oldColor),255-Color.blue(oldColor)));
            }
        }

        return newImage;
    }

    /**
     *计算均值
     */
    public static int CalAverColor(final int x,final int y,final Bitmap image){
        int gray=0;
        for(int i=-1;i<2;i++)
            for(int j=-1;j<2;j++){
                gray+=Color.red(image.getPixel(x+i,y+i));
            }
        gray/=9;

        return  gray;
    }

    /**
     * 均值滤波器
     */
    public static Bitmap AverageFilter(final Bitmap oldImage){
        Bitmap newImage=oldImage.copy(oldImage.getConfig(),true);
        int oldColor;
        int gray;

        int height=newImage.getHeight();
        int width=newImage.getWidth();

        for(int x=0;x<width;x++)
            for(int y=0;y<height;y++){
                oldColor = oldImage.getPixel(x,y);
                if(x>=1&&y>=1&&x<width-1&&y<height-1){
                    gray=CalAverColor(x,y,oldImage);
                    newImage.setPixel(x,y,Color.rgb(gray,gray,gray));
                }
                else newImage.setPixel(x,y,Color.rgb(Color.red(oldColor),Color.green(oldColor),Color.blue(oldColor)));
            }

        return newImage;
    }

    /**
     *计算中值
     */
    public static int Median(final int x,final int y,final Bitmap image){
        int[] gray=new int[9];
        int m=0,i,j;
        for(i=-1;i<2;i++){
            for(j=-1;j<2;j++){
                gray[m]=Color.red(image.getPixel(x+i,y+j));
                m++;
            }
        }

        for(i=1;i<9;i++){
            int temp = gray[i];
            for(j=i-1;j>=0 && gray[j]>temp;j--){
                gray[j+1] = gray[j];
            }
            gray[j+1] = temp;
        }

        return gray[4];

    }

    /**
     *均值滤波器
     */
    public static Bitmap MedianFilter(final Bitmap oldImage){
        Bitmap newImage=oldImage.copy(oldImage.getConfig(),true);
        int oldColor;
        int gray;

        int height=newImage.getHeight();
        int width=newImage.getWidth();

        for(int x=0;x<width;x++)
            for(int y=0;y<height;y++){
                oldColor = oldImage.getPixel(x,y);
                if(x>=1&&y>=1&&x<width-1&&y<height-1){
                    gray=Median(x,y,oldImage);
                    newImage.setPixel(x,y,Color.rgb(gray,gray,gray));
                }
                else newImage.setPixel(x,y,Color.rgb(Color.red(oldColor),Color.green(oldColor),Color.red(oldColor)));
            }

        return newImage;
    }

}
