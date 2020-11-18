/*This is the class to edit or delete the name of the child*/

package com.e.practicalparentlavateam.UI;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.e.practicalparentlavateam.Model.Children;
import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditChild extends AppCompatActivity {
    private EditText etName2;
    private String childEditing;
    private int childEditingIndex;
    private ChildrenManager children;
    private static final String EXTRA_CHILD_INDEX = "Extra - Child Index";
    private ImageView image;
    private Button takePhoto;

    public static Intent makeEditIntent(Context c, int childIndex) {
        Intent intent = new Intent(c, EditChild.class);
        intent.putExtra(EXTRA_CHILD_INDEX, childIndex);
        return intent;
    }

    public void extractIndexFromIntent(){
        Intent intent = getIntent();
        childEditingIndex = intent.getIntExtra(EXTRA_CHILD_INDEX, 0);
        children = ChildrenManager.getInstance();
        childEditing = children.get(childEditingIndex).getName();
        etName2.setHint(children.get(childEditingIndex).getName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        children = ChildrenManager.getInstance();
        etName2 = findViewById(R.id.etName2);

        //initalize values
        extractIndexFromIntent();

        takePhotoForChild();
        setupButtonDelete();
        setupButtonOk();
    }

    //https://www.youtube.com/watch?v=RaOyw84625w
    private void takePhotoForChild() {
        if(ContextCompat.checkSelfPermission(EditChild.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditChild.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }

        image = findViewById(R.id.child_photo);
        takePhoto = findViewById(R.id.take_image);

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

    private void setupButtonDelete() {
        Button btn = findViewById(R.id.btnDelete);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        children = ChildrenManager.getInstance();
                        children.remove(childEditingIndex);
                        saveChildDetails();
                        Intent intent = ConfigureChildren.makeIntent(EditChild.this);
                        startActivity(intent);
                        finish();
                    }
                }
        );
    }

    private void setupButtonOk() {
        Button btn = findViewById(R.id.btnSaveEdit);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Extract data from screen
                        String name = etName2.getText().toString();

                        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
                        Bitmap bitmapImage = drawable.getBitmap();
                        ContextWrapper cw = new ContextWrapper(getApplicationContext());
                        // path to /data/data/yourapp/app_data/imageDir
                        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                        // Create imageDir
                        File mypath=new File(directory,etName2.getText().toString() + ".jpg");

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
                        String path = directory.getAbsolutePath();
                        // Create new data object
                        children = ChildrenManager.getInstance();
                        children.set(childEditingIndex, new Children(name));
                        children.setPath(path);
                        saveChildDetails();
                        Intent intent = ConfigureChildren.makeIntent(EditChild.this);
                        startActivity(intent);
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