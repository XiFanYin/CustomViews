package view.local.zz.customviews.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import view.local.zz.customviews.R;
import view.local.zz.customviews.views.imagesurface.ImageRender;
import view.local.zz.customviews.views.imagesurface.VBOImageRender;


public class OpenGLVBOImageActivity extends AppCompatActivity {

    GLSurfaceView imageSurface;
    private Bitmap t;
    private VBOImageRender reader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_surface);
        //获取控件
        imageSurface = findViewById(R.id.imageSurface);
        //获取图片
        if (t == null) {
            t = BitmapFactory.decodeResource(getResources(), R.drawable.fbb);
        }
        //设置版本
        imageSurface.setEGLContextClientVersion(2);
        //创建渲染器
        reader = new VBOImageRender();
        //设置图片
        reader.setBitmap(t);
        //设置渲染器
        imageSurface.setRenderer(reader);
        //设置需主动调用绘制
        imageSurface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        imageSurface.requestRender();
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
