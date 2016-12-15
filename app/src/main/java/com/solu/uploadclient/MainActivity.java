package com.solu.uploadclient;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    String TAG;
    ImageView img;
    File file;
    String filename;
    InputStream is;
    static MainActivity mainActivity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG=this.getClass().getName();
        mainActivity=this;

        setContentView(R.layout.activity_main);

        img=(ImageView)findViewById(R.id.img);

    }

    //다른 앱의 정보를 가져온다!! 그 앱이 제공하는 컨텐트 프로바이더를
    //통해 정보를 가져올 수 있다..
    public void preview(View view){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        //해당 액티비티로부터 어떤 결과를 가져와야 하므로...
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode ==1){
            if(resultCode == RESULT_OK){
                //유저가 선택한 이미지를 이미지뷰에 적용시키자!!
                Uri uri=data.getData();
                try {
                    Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    img.setImageBitmap(bitmap);

                    //파일명을 추출하자!!
                    //컨텐츠 프로바이더가 제공하는 데이터의 컬럼정보 추출!!
                    String[] filePathColumn={MediaStore.Images.Media.DATA};

                    Cursor cursor=getContentResolver().query(uri, filePathColumn,null,null,null);
                    cursor.moveToFirst(); //레코드의 위치를 처음으로 두자!..
                    int index=cursor.getColumnIndex(filePathColumn[0]);
                    filename=cursor.getString(index);
                    cursor.close();

                    file = new File(filename);

                    Log.d(TAG,file.getName());

                    //스트림도 얻자!!
                    is=getContentResolver().openInputStream(uri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //업로드 실행 메서드!
    public void upload(View view){
        UploadAsync uploadAsync = new UploadAsync(is);
        uploadAsync.execute("http://192.168.0.8:9090/upload", "zino", file.getName());
}

}








