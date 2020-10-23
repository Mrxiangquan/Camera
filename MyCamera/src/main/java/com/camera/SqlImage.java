package com.camera;

import java.io.Serializable;

/**
 * 用于SQL存储的数据类
 */
public class SqlImage implements Serializable {
    //表名
    public static final String TABLE_Name="image_table";

    //表的各域名
    public static final String KEY_ID="id";
    public static final String KEY_name="name";
    public static final String KEY_type="type";
    public static final String KEY_size="size";
    public static final String KEY_changetime="changetime";
    public static final String KEY_path="path";

    //属性
    public int id;
    public String name;
    public String type;
    public String size;
    public String changTime;
    public String path;

}
