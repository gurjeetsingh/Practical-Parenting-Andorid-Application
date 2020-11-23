/*This is the class to edit or delete the name of the child
* and edit the portrait of the chils*/

package com.e.practicalparentlavateam.UI;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
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
import java.io.InputStream;

public class EditChild extends AppCompatActivity {
    private EditText editName2;
    private String childEditing;
    private int childEditingIndex;
    private ChildrenManager children;
    private static final String EXTRA_CHILD_INDEX = "Extra - Child Index";
    private ImageView image;
    private Button takePhoto;
    Context context = this;
    private final static int SELECT_FROM_GALLERY = 007;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child);
        Toolbar toolbar = findViewById(R.id.edit_child_toolbar);
        setSupportActionBar(toolbar);

        children = ChildrenManager.getInstance();
        editName2 = findViewById(R.id.edit_name2);
        image = findViewById(R.id.child_photo);

        //initalize ui values
        extractIndexFromIntent();

        takePhotoForChild();
        setupButtonDelete();
        setupButtonGallery();
        setupButtonOk();
    }

    public void extractIndexFromIntent(){
        Intent intent = getIntent();
        childEditingIndex = intent.getIntExtra(EXTRA_CHILD_INDEX, 0);
        children = ChildrenManager.getInstance();
        childEditing = children.get(childEditingIndex).getName();
        //sets the current text displayed as the name of the current child
        editName2.setText(children.get(childEditingIndex).getName());
        //sets the picture as the picture of the current child
        try {
            File file = new File(children.getPath(), childEditing + ".jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            image.setImageBitmap(bitmap);
        }
        catch (FileNotFoundException exception)
        {
            exception.printStackTrace();
        }
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
        if(data != null){
            if (requestCode == 100) {
                Bitmap captureImage = (Bitmap) data.getExtras().get("data");
                image.setImageBitmap(captureImage);
            }

            //If the resultcode is result_OK, and we send in the request code select from gallery
            //which was predetermined, then we understand that the data send is an image from the gallery.
            else if (requestCode == SELECT_FROM_GALLERY
                    && resultCode == RESULT_OK && data != null) {
                //Now, we create an inputstream from the URI data we obtained. Then we decade it, and set on image.
                //https://stackoverflow.com/questions/6612263/converting-input-stream-into-bitmap
                try {
                    InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                    Bitmap captureImage = BitmapFactory.decodeStream(inputStream);
                    image.setImageBitmap(captureImage);
                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private void setupButtonDelete() {
        Button button = findViewById(R.id.button_delete);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context,
                                R.style.MyDialogThemeEditChild);
                        builder.setTitle(R.string.do_you_want_to_delete_child);
                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        children = ChildrenManager.getInstance();
                                        children.remove(childEditingIndex);
                                        saveChildDetails();
                                        Intent intent = ConfigureChildren.makeIntent(EditChild.this);
                                        startActivity(intent);
                                        finish();
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog ad = builder.create();
                        ad.show();

                    }
                }
        );
    }

    /*
    This button allows us to setup a button to access the gallery, while selecting a picture.
     */
    private void setupButtonGallery() {
        Button galleryButton=findViewById(R.id.galler_button);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This allows us to sent a particular type of intent i.e. pick from the gallery.
                //This customizes the intent's data we are sending in.
                //We also send in the particular request code, so that we can select from the gallery.
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_FROM_GALLERY);


            }
        });
    }

    private void setupButtonOk() {
        Button button = findViewById(R.id.button_save_edit);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Extract data from screen
                        String name = editName2.getText().toString();
                        if(name.equals("")){
                            Toast.makeText(EditChild.this,R.string.hint_for_name,Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }

                        //https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps
                        // -images-from-internal-memory-in-android
                        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
                        Bitmap bitmapImage = drawable.getBitmap();
                        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
                        // path to /data/data/yourapp/app_data/imageDir
                        File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
                        // Create imageDir
                        File myPath = new File(directory, editName2.getText().toString() + ".jpg");

                        FileOutputStream fileOutputStreams = null;
                        try {
                            fileOutputStreams = new FileOutputStream(myPath);
                            // Use the compress method on the BitMap object to write image to the OutputStream
                            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStreams);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        } finally {
                            try {
                                fileOutputStreams.close();
                            } catch (IOException exception) {
                                exception.printStackTrace();
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

    @Override
    public void onBackPressed() {
        //clear the old stack
        //Resource used to understand concept:
        // https://stackoverflow.com/questions/5794506/android-clear-the-back-stack
        Intent mainIntent = ConfigureChildren.makeIntent(EditChild.this);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        EditChild.this.finish();
    }

    public static Intent makeEditIntent(Context context, int childIndex) {
        Intent intent = new Intent(context, EditChild.class);
        intent.putExtra(EXTRA_CHILD_INDEX, childIndex);
        return intent;
    }
}