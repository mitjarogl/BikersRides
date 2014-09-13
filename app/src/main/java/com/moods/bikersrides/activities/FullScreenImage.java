package com.moods.bikersrides.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.moods.bikersrides.R;

//FIXME chaneg to fragment
public class FullScreenImage extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        Intent intent = getIntent();
        String bitmap = intent.getStringExtra("bitmapUri");
        ImageView imageView = (ImageView) findViewById(R.id.imageView_full_image);
        imageView.setImageURI(Uri.parse(bitmap));

    }
}
