/*This activity is to add the name of the child*/

package com.e.practicalparentlavateam.UI;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.e.practicalparentlavateam.Model.Children;
import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.R;

import com.google.gson.Gson;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChildDetails extends AppCompatActivity {
    private EditText etName;
    private ChildrenManager children;
    private ImageView image;
    private Button takePhoto;
    private String path;

    public static Intent makeIntentForAdd(Context context) {
        return new Intent(context, ChildDetails.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_details3);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        children = ChildrenManager.getInstance();
        etName = findViewById(R.id.etName);

        takePhotoForChild();
        setupButtonCancel();
        setupButtonOk();

    }

    //https://www.youtube.com/watch?v=RaOyw84625w
    private void takePhotoForChild() {
        if(ContextCompat.checkSelfPermission(ChildDetails.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChildDetails.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }

        image = findViewById(R.id.child_image);
        takePhoto = findViewById(R.id.take_photo);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(captureImage);
        }
    }

    private void setupButtonCancel() {
        Button btn = findViewById(R.id.btnCancel);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ChildDetails.this.finish();
                    }
                }
        );
    }

    private void setupButtonOk() {
        Button btn = findViewById(R.id.btnSave);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Extract data from screen
                        String name = etName.getText().toString();

                        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
                        Bitmap bitmapImage = drawable.getBitmap();
                        ContextWrapper cw = new ContextWrapper(getApplicationContext());
                        // path to /data/data/yourapp/app_data/imageDir
                        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                        // Create imageDir
                        File mypath=new File(directory,etName.getText().toString() + ".jpg");

                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(mypath);
                            // Use the compress method on the BitMap object to write image to the OutputStream
                            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        path = directory.getAbsolutePath();

                        // Create new data object
                        children = ChildrenManager.getInstance();
                        children.add(new Children(name));
                        children.setPath(path);
                        saveChildDetails();
                        finish();
                    }
                }
        );
    }

    public void saveChildDetails(){
        SharedPreferences prefs = this.getSharedPreferences("childPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(children);
        editor.putString("childPrefs", json);
        System.out.println(json);
        editor.commit();     // This line is IMPORTANT !!!
    }
}