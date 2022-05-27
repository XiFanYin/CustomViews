package view.local.zz.customviews.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import view.local.zz.customviews.R;
import view.local.zz.customviews.views.imagesurface.ImageSurfaceView;


public class ImageActivity extends AppCompatActivity {

    ImageSurfaceView imageSurface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_surface);
        imageSurface = findViewById(R.id.imageSurface);
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageSurface.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        imageSurface.onPause();
    }
}
