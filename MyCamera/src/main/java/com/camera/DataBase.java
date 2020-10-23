package com.camera;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.camera.ViewPicture.ShowLongFileSzie;

public class DataBase extends AppCompatActivity {
    private SqlOperation sqlOperation;
    private SimpleAdapter listAdapter;
    private ArrayList<HashMap<String,String>> imageList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);

        sqlOperation=new SqlOperation(this);
        init();
        //listview
        listView=findViewById(R.id.lv_database);
        imageList=sqlOperation.getSqlImageList();
        if(imageList.size()!=0){
            listAdapter=new SimpleAdapter(DataBase.this,imageList,
                    R.layout.view_image_entry,new String[] { "id","name"}, new int[] {R.id.image_Id, R.id.image_name});
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView image_Id = view.findViewById(R.id.image_Id);
                    String imageId = image_Id.getText().toString();
                    Intent intent = new Intent(DataBase.this,EditDetalis.class);
                    SqlImage image=sqlOperation.getSqlImageById(Integer.parseInt(imageId));
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("image",image);
                    intent.putExtras(bundle);
                    intent.putExtra("way",0x002);
                    startActivityForResult(intent,0x002);
                }
            });
        }
        else{
            Toast.makeText(this, "No student!", Toast.LENGTH_SHORT).show();
        }
        //实现添加
        Button add=findViewById(R.id.btn_db_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DataBase.this,EditDetalis.class);
                SqlImage image=new SqlImage();
                Bundle bundle=new Bundle();
                bundle.putSerializable("image",image);
                intent.putExtras(bundle);
                intent.putExtra("way",0x001);
                startActivityForResult(intent,0x001);
            }
        });

        //实现删除
        final Button delete=findViewById(R.id.btn_db_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });

        //实现查找
        Button query=findViewById(R.id.btn_db_query);
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryData();
            }
        });
    }

    /**
     * 初始化数据库
     */
    private void init(){
        if(sqlOperation.isEmpty()){
            String dirPath = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getPath() + "/" + this.getResources().getString(R.string.picture_path);
            List<String> path=ViewPicture.getImagePath(dirPath);
            SqlImage image=new SqlImage();
            for(int i=0;i<path.size();i++){
                File file=new File(path.get(i));
                image.name=file.getName();
                image.type="image/"+path.get(i).substring(path.get(i).lastIndexOf(".")+1,path.get(i).length());
                image.size=ShowLongFileSzie(file.length());
                image.changTime=new Date(file.lastModified()).toString();
                image.path=path.get(i);
                sqlOperation.insert(image);
            }
        }
    }

    /**
     * 删除操作
     */
    private void deleteData(){
        if(sqlOperation.isEmpty()) Toast.makeText(DataBase.this,"当前表为空",Toast.LENGTH_SHORT).show();
        else{
            AlertDialog.Builder builder=new AlertDialog.Builder(DataBase.this);
            final EditText editText=new EditText(DataBase.this);
            editText.setHint("请输入删除项的id");
            builder.setView(editText);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int id=Integer.valueOf(editText.getText().toString());
                    sqlOperation.delete(id);
                    Toast.makeText(DataBase.this,"删除成功",Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
        }
    }

    /**
     * 查找功能
     */
    private void queryData(){
        if(sqlOperation.isEmpty()) Toast.makeText(DataBase.this,"当前表为空",Toast.LENGTH_SHORT).show();
        else{
            AlertDialog.Builder builder=new AlertDialog.Builder(DataBase.this);
            final EditText editText=new EditText(DataBase.this);
            editText.setHint("请输入查找的id");
            builder.setView(editText);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int id=Integer.valueOf(editText.getText().toString());
                    SqlImage image=sqlOperation.getSqlImageById(id);
                    Intent intent=new Intent(DataBase.this,EditDetalis.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("image",image);
                    intent.putExtras(bundle);
                    intent.putExtra("way",0x002);
                    startActivityForResult(intent,0x002);
                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle=data.getExtras();
        SqlImage image=(SqlImage)bundle.getSerializable("backImage");
        //请求操作为添加，返回操作为添加
        if(requestCode==0x001&&resultCode==0x001){
            sqlOperation.insert(image);
        }
        //请求操作为查找，返回操作为保存
        if(requestCode==0x002&&resultCode==0x002){
            sqlOperation.update(image);
        }
        //请求操作为查找，返回操作为删除
        if(requestCode==0x002&&resultCode==0x003){
            sqlOperation.delete(image.id);
        }
        //resultCode为0x004时无实际操作
        imageList.clear();
        imageList.addAll(sqlOperation.getSqlImageList());
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
    }
}
