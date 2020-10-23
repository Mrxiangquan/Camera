package com.camera;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.HashMap;


public class SqlOperation {
    private SqliteDBHelper dbHelper;

    public SqlOperation(Context context){
        dbHelper=new SqliteDBHelper(context);
    }

    /**
     * 判断表是否为空
     * @return
     */
    public boolean isEmpty(){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from "+SqlImage.TABLE_Name, null);
        if(c.getCount()==0) return true;
        else return false;
    }

    /**
     * 插入操作
     */
    public int insert(SqlImage image){
        //打开连接，写入数据
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(SqlImage.KEY_name,image.name);
        values.put(SqlImage.KEY_type,image.type);
        values.put(SqlImage.KEY_size,image.size);
        values.put(SqlImage.KEY_changetime,image.changTime);
        values.put(SqlImage.KEY_path,image.path);
        //插入数据
        long student_Id=db.insert(SqlImage.TABLE_Name,null,values);
        db.close();
        return (int)student_Id;
    }

    /**
     * 删除操作
     */
    public void delete(int image_Id){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete(SqlImage.TABLE_Name,SqlImage.KEY_ID+"=?", new String[]{String.valueOf(image_Id)});
        db.close();
    }

    /**
     * 更新操作
     */
    public void update(SqlImage image){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put(SqlImage.KEY_name,image.name);
        values.put(SqlImage.KEY_type,image.type);
        values.put(SqlImage.KEY_size,image.size);
        values.put(SqlImage.KEY_changetime,image.changTime);
        values.put(SqlImage.KEY_path,image.path);

        db.update(SqlImage.TABLE_Name,values,SqlImage.KEY_ID+"=?",new String[] { String.valueOf(image.id) });
        db.close();
    }

    /**
     * 获取当前数据库中所有图片的名称以及路径
     */
    public ArrayList<HashMap<String, String>> getSqlImageList(){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String selectQuery="SELECT "+
                SqlImage.KEY_ID + "," +
                SqlImage.KEY_name +
                " FROM "+SqlImage.TABLE_Name;
        ArrayList<HashMap<String,String>> imageList=new ArrayList<HashMap<String, String>>();
        Cursor cursor=db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
                HashMap<String,String> image=new HashMap<String,String>();
                image.put("id",cursor.getString(cursor.getColumnIndex(SqlImage.KEY_ID)));
                image.put("name",cursor.getString(cursor.getColumnIndex(SqlImage.KEY_name)));
                imageList.add(image);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return imageList;
    }

    /**
     * 通过id获取一个数据库中该对象信息
     */
    public SqlImage getSqlImageById(int Id){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String selectQuery="SELECT "+
                SqlImage.KEY_ID + "," +
                SqlImage.KEY_name + "," +
                SqlImage.KEY_type + "," +
                SqlImage.KEY_size + "," +
                SqlImage.KEY_changetime + "," +
                SqlImage.KEY_path +
                " FROM " + SqlImage.TABLE_Name
                + " WHERE " +
                SqlImage.KEY_ID + "=?";
        int iCount=0;
        SqlImage image=new SqlImage();
        Cursor cursor=db.rawQuery(selectQuery,new String[]{String.valueOf(Id)});
        if(cursor.moveToFirst()){
            do{
                image.id =cursor.getInt(cursor.getColumnIndex(SqlImage.KEY_ID));
                image.name =cursor.getString(cursor.getColumnIndex(SqlImage.KEY_name));
                image.type =cursor.getString(cursor.getColumnIndex(SqlImage.KEY_type));
                image.size =cursor.getString(cursor.getColumnIndex(SqlImage.KEY_size));
                image.changTime =cursor.getString(cursor.getColumnIndex(SqlImage.KEY_changetime));
                image.path =cursor.getString(cursor.getColumnIndex(SqlImage.KEY_path));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return image;
    }

}
