package com.camera;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditDetalis extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_detalis);

        final Intent intent=getIntent();
        final Bundle bundle=intent.getExtras();
        final int way=bundle.getInt("way");
        final SqlImage image=(SqlImage) bundle.getSerializable("image");
        //编辑框
        final EditText name=findViewById(R.id.edit_ed_name);
        final EditText type=findViewById(R.id.edit_ed_type);
        final EditText size=findViewById(R.id.edit_ed_size);
        final EditText changTime=findViewById(R.id.edit_ed_changeTime);
        final EditText path=findViewById(R.id.edit_ed_path);

        name.setText(image.name);
        type.setText(image.type);
        size.setText(image.size);
        changTime.setText(image.changTime);
        path.setText(image.path);

        //实现添加
        Button add=findViewById(R.id.btn_ed_add);
        if(way==0x001) add.setText("添加");
        else add.setText("保存");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int resultCode;
                //添加操作
                if(way==0x001) resultCode=0x001;
                else resultCode=0x002;

                image.name=name.getText().toString();
                image.type=type.getText().toString();
                image.size=size.getText().toString();
                image.changTime=changTime.getText().toString();
                image.path=path.getText().toString();

                Intent backIntent=new Intent(EditDetalis.this,DataBase.class);
                Bundle backBundle=new Bundle();
                backBundle.putSerializable("backImage",image);
                backIntent.putExtras(backBundle);
                setResult(resultCode,backIntent);
                finish();

            }
        });

        //实现删除
        Button delete=findViewById(R.id.btn_ed_delete);
        if(way==0x001) delete.setEnabled(false);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SqlImage image=(SqlImage) bundle.getSerializable("image");

                Intent backIntent=new Intent(EditDetalis.this,DataBase.class);
                Bundle backBundle=new Bundle();
                backBundle.putSerializable("backImage",image);
                backIntent.putExtras(backBundle);
                setResult(0x003,backIntent);
                finish();
            }
        });

        //实现返回
        Button back=findViewById(R.id.btn_ed_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent=new Intent(EditDetalis.this,DataBase.class);
                SqlImage image=new SqlImage();
                Bundle backBundle=new Bundle();
                backBundle.putSerializable("backImage",image);
                backIntent.putExtras(backBundle);
                setResult(0x004,backIntent);
                finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
    }
}
