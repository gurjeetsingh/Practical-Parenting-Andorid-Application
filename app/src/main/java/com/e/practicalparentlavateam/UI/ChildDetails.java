/*This activity is to add the name of the child*/

package com.e.practicalparentlavateam.UI;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ChildDetails extends AppCompatActivity {
    private EditText etName;
    private ChildrenManager children;
    private ImageView image;
    private Button takePhoto;
    private String path;
    private final static int SELECT_PHOTO = 12345;

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
        etName = findViewById(R.id.edit_name);

        takePhotoForChild();
        setupButtonCancel();
        setupButtonOk();
        setupButtongallery();

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
        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        else if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            //https://stackoverflow.com/questions/6612263/converting-input-stream-into-bitmap
            try {
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                Bitmap captureImage = BitmapFactory.decodeStream(inputStream);
                image.setImageBitmap(captureImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }
    }

    private void setupButtonCancel() {
        Button btn = findViewById(R.id.button_cancel);
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
        Button btn = findViewById(R.id.button_save);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Extract data from screen
                        String name = etName.getText().toString();

                        //https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
                        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
                        Bitmap bitmapImage = drawable.getBitmap();
                        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
                        // path to /data/data/yourapp/app_data/imageDir
                        File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
                        // Create imageDir
                        File myPath=new File(directory,etName.getText().toString() + ".jpg");

                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream(myPath);
                            // Use the compress method on the BitMap object to write image to the OutputStream
                            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                            Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fileOutputStream.close();
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

    private void setupButtongallery() {
        Button gallerbtn=findViewById(R.id.gallery_button);
        gallerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);


            }
        });
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