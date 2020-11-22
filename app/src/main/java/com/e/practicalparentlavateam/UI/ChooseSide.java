/*Choose side*/

package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ChooseSide extends AppCompatActivity {
    private static final String EXTRA_NAME = "com.e.practicalparentlavateam.UI - the name";

    private String choice;
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_side);

        getChild();
        headsButton();
        tailsButton();
    }

    private void tailsButton() {
        Button tails = (Button) findViewById(R.id.tails);
        tails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = "Tails";
                Intent intent = CoinFlipActivity.makeIntent3(ChooseSide.this, choice, name);
                startActivity(intent);
                finish();
            }
        });
    }

    private void headsButton() {
        Button heads = (Button) findViewById(R.id.heads);
        heads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = "Heads";
                Intent intent = CoinFlipActivity.makeIntent3(ChooseSide.this, choice, name);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getChild() {
        Intent i = getIntent();
        name = i.getStringExtra(EXTRA_NAME);
        ImageView imageView = (ImageView) findViewById(R.id.side_child_image);
        if(name != null && !name.equals("nobody")){
            try {
                File file=new File(ChildrenManager.getInstance().getPath(), name + ".jpg");
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                imageView.setImageBitmap(bitmap);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static Intent makeLaunch(Context context, String name){
        Intent intent = new Intent(context, ChooseSide.class);
        intent.putExtra(EXTRA_NAME, name);
        return intent;
    }
}