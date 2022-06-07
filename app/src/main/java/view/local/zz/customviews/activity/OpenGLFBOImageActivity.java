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
import view.local.zz.customviews.views.imagesurface.FBORenderTwo;


public class OpenGLFBOImageActivity extends AppCompatActivity {

    GLSurfaceView imageSurface;
    ImageView image;
    private Bitmap t;
    private FBORenderTwo reader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_surface);
        //获取控件
        imageSurface = findViewById(R.id.imageSurface);
        image = findViewById(R.id.image);
        //获取图片
        if (t == null) {
            t = BitmapFactory.decodeResource(getResources(), R.drawable.fbb);
        }
        //设置版本
        imageSurface.setEGLContextClientVersion(2);
        //创建渲染器
        reader = new FBORenderTwo();
        //设置图片
        reader.setBitmap(t);
        //设置回调，离屏渲染之后
        reader.setCallback(new FBORenderTwo.Callback() {
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
