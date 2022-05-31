package view.local.zz.customviews.views.videosurface;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class VideoSurfaceView extends GLSurfaceView {

    private VideoRender reader;

    public VideoSurfaceView(Context context) {
        super(context);
    }

    public VideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置版本
        setEGLContextClientVersion(2);
        //创建渲染器
        reader = new VideoRender(context, this);
        //设置渲染器
        setRenderer(reader);
        //设置需主动调用绘制
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);


    }


}
