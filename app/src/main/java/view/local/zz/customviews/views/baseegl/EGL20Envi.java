package view.local.zz.customviews.views.baseegl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * 脱离GLSurfaceView控件，自己去处理环境
 */
public class EGL20Envi {
    //当前宽高是指定的Surface宽度和高度，通过构造方法依赖注入
    private int mWidth;
    private int mHeight;

    //真正的数据处理类
    private BaseFilter mFilter;

    //注入线程名字，渲染必须在同一个线程中
    private String mThreadOwner;

    //EGL环境搭建工具类
    private EGLHelper mEGLHelper;


    //构造方法
    public EGL20Envi(int mWidth, int mHeight) {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        //创建EGL环境
        mEGLHelper = new EGLHelper();
        //初始化一个画布
        mEGLHelper.eglInit(mWidth, mHeight);
    }


    /**
     * 提供依赖注入线程的名字，为容错提供判断
     *
     * @param threadOwner
     */
    public void setThreadOwner(String threadOwner) {
        this.mThreadOwner = threadOwner;
    }


    /**
     * 依赖注入关于数据处理逻辑类
     *
     * @param mFilter
     */
    public void setFilter(BaseFilter mFilter) {
        this.mFilter = mFilter;
        // 容错处理
        if (!Thread.currentThread().getName().equals(mThreadOwner)) {
            Log.e("EGL20Envi", "当前线程没有OpenGL上下文环境");
            return;
        }
        //调用创建程序代码
        mFilter.createProgram();
    }

    /**
     * 释放EGL环境
     */
    public void destroy() {
        mEGLHelper.destroy();
    }

    /**
     * 注入需要处理的图片,
     */
    public Bitmap setBitmap(Bitmap mBitmap) {

        Bitmap finalBitmap = null;
        //容错处理
        if (mFilter == null) {
            Log.e("EGL20Envi", "没有设置渲染器，无法进行图片处理");
            return finalBitmap;
        }
        if (!Thread.currentThread().getName().equals(mThreadOwner)) {
            Log.e("EGL20Envi", "当前线程没有OpenGL上下文环境");
            return finalBitmap;
        }
        //设置需要渲染的纹理Id
        mFilter.setTextureId(createTexture(mBitmap));

        //绘制渲染
        mFilter.draw();
        //创建纹理图片储存
        ByteBuffer mBuffer = ByteBuffer.allocate(mBitmap.getWidth() * mBitmap.getHeight() * 4);
        //从GPU读取数据到CPU
        mEGLHelper.mGL.glReadPixels(0, 0, mWidth, mHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBuffer);

        //拿到数据生成图片传出
        finalBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        finalBitmap.copyPixelsFromBuffer(mBuffer);

        return finalBitmap;
    }


    /**
     * 生成图片纹理
     *
     * @param bmp
     * @return
     */
    private int createTexture(Bitmap bmp) {
        int[] texture = new int[1];
        if (bmp != null && !bmp.isRecycled()) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0);
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
            //退出纹理状态，进入下一个阶段
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            return texture[0];
        }
        return 0;
    }


}
