package view.local.zz.customviews.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import view.local.zz.customviews.R;
import view.local.zz.customviews.views.imagesurface.WaterRender;

public class OpenGlWaterImageActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;

    private Bitmap t;
    private WaterRender reader;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_image);
        glSurfaceView = findViewById(R.id.glSurfaceView);
        //获取图片
        if (t == null) {
            t = BitmapFactory.decodeResource(getResources(), R.drawable.fbb);
        }
        //设置版本
        glSurfaceView.setEGLContextClientVersion(2);
        //创建渲染器
        reader = new WaterRender(t);
        //设置渲染器
        glSurfaceView.setRenderer(reader);
        //设置需主动调用绘制
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        glSurfaceView.requestRender();

    }
}
