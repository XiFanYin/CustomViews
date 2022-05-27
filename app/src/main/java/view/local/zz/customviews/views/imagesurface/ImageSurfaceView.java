package view.local.zz.customviews.views.imagesurface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import view.local.zz.customviews.R;

/**
 * 入门使用openGl去绘制图片
 */
public class ImageSurfaceView extends GLSurfaceView {

    private ImageRender reader;
    private Bitmap t;

    public ImageSurfaceView(Context context) {
        super(context);
    }

    public ImageSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(t==null){
            t = BitmapFactory.decodeResource(context.getResources(), R.drawable.fbb2);
        }
        //设置版本
        setEGLContextClientVersion(2);
        //创建渲染器
        reader = new ImageRender();
        reader.setBitmap(t);
        //设置渲染器
        setRenderer(reader);
        //设置需主动调用绘制
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }



}
