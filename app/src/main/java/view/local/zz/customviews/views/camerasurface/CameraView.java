package view.local.zz.customviews.views.camerasurface;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

//相机渲染View
public class CameraView extends GLSurfaceView {

    private  CameraRender render;

    public CameraView(Context context) {
        super(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置版本
        setEGLContextClientVersion(2);
        //设置渲染器
         render = new CameraRender(this);
        setRenderer(render);
        //设置需主动调用绘制
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


    @Override
    public void onPause() {
        super.onPause();
        render.close();
    }

}
