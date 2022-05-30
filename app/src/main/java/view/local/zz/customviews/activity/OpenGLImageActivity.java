package view.local.zz.customviews.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import view.local.zz.customviews.R;
import view.local.zz.customviews.views.imagesurface.ImageSurfaceView;

/**
 * https://learnopengl-cn.github.io/
 * https://wuwang.blog.csdn.net/article/details/52793354
 * https://juejin.cn/post/6844903984235282445#heading-9
 *
 */
public class OpenGLImageActivity extends AppCompatActivity {

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
