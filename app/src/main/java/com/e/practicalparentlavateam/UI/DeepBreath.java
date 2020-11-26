package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.e.practicalparentlavateam.R;

public class DeepBreath extends AppCompatActivity {
    private ImageView circle;
    private Button enlarge;
    private Button shrink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_breath);

        //https://commons.wikimedia.org/wiki/File:Small-dark-green-circle.svg
        circle = findViewById(R.id.circle);
        enlarge = findViewById(R.id.enlarge);
        shrink = findViewById(R.id.shrink);

        enlargeCircle();
        shrinkCircle();
    }

    private void enlargeCircle() {
        enlarge.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
                circle.startAnimation(animation);
                return false;
            }
        });
    }

    private void shrinkCircle() {
        shrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
                circle.startAnimation(animation);
            }
        });
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, DeepBreath.class);
    }
}