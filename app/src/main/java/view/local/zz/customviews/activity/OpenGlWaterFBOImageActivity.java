package view.local.zz.customviews.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.nio.ByteBuffer;

import view.local.zz.customviews.R;
import view.local.zz.customviews.views.imagesurface.WaterFBORender;

public class OpenGlWaterFBOImageActivity  extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private ImageView image;

    private Bitmap t;
    private WaterFBORender reader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_image);
        glSurfaceView = findViewById(R.id.glSurfaceView);
        image = findViewById(R.id.image);
        //获取图片
        if (t == null) {
            t = BitmapFactory.decodeResource(getResources(), R.drawable.fbb);
        }
        //设置版本
        glSurfaceView.setEGLContextClientVersion(2);
        //创建渲染器
        reader = new WaterFBORender(t);
        reader.setCallback(new WaterFBORender.Callback() {
            @Override
            public void onCall(final int w, final int h, final ByteBuffer data) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(data);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                image.setImageBitmap(bitmap);
                            }
                        });

                    }
                }).start();
            }
        });
        //设置渲染器
        glSurfaceView.setRenderer(reader);
        //设置需主动调用绘制
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        glSurfaceView.requestRender();
    }
}
