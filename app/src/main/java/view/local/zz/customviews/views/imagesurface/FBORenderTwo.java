package view.local.zz.customviews.views.imagesurface;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FBORenderTwo implements GLSurfaceView.Renderer {
    //创建纹理坐标
    float[] texture = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };

    //创建定点坐标数据
    float[] vertexs = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };


    /**
     * 顶点坐标Buffer
     */
    protected FloatBuffer mVerBuffer;

    /**
     * 纹理坐标Buffer
     */
    protected FloatBuffer mTexBuffer;

    /**
     * 程序句柄
     */
    protected int mProgram;
    /**
     * 顶点坐标句柄
     */
    protected int mHPosition;
    /**
     * 纹理坐标句柄
     */
    protected int mHCoord;
    /**
     * 总变换矩阵句柄
     */
    protected int mHMatrix;

    /**
     * 默认纹理贴图句柄
     */
    protected int mHTexture;

    private float[] matrix = Arrays.copyOf(getOriginalMatrix(), 16);

    private Bitmap mBitmap;

    private int[] fFrame = new int[1];

    private int[] fTexture = new int[1];

    private ByteBuffer mBuffer;

    private Callback mCallback;

    public FBORenderTwo() {
        //定点数据复制到本地内存
        ByteBuffer a = ByteBuffer.allocateDirect(32);
        a.order(ByteOrder.nativeOrder());
        mVerBuffer = a.asFloatBuffer();
        mVerBuffer.put(vertexs);
        mVerBuffer.position(0);

        ByteBuffer b = ByteBuffer.allocateDirect(32);
        b.order(ByteOrder.nativeOrder());
        mTexBuffer = b.asFloatBuffer();
        mTexBuffer.put(texture);
        mTexBuffer.position(0);


    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, getVertexShader());
        //调用父类方法编译源码，传递类型为片段
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShader());
        //创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);
        //获取索引
        mHPosition = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mHCoord = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
        mHMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        mHTexture = GLES20.glGetUniformLocation(mProgram, "uTexture");
        //这里的矩阵是因为FBO坐标系和纹理坐标系Y轴相反
        matrix = flip(getOriginalMatrix(), false, true);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清空画布，设置颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if (mBitmap != null && !mBitmap.isRecycled()) {
            GLES20.glUseProgram(mProgram);

            GLES20.glUniformMatrix4fv(mHMatrix, 1, false, matrix, 0);
            //激活纹理单元
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glUniform1i(mHTexture, 0);

            //激活顶点索引
            GLES20.glEnableVertexAttribArray(mHPosition);
            //传递数据
            GLES20.glVertexAttribPointer(mHPosition, 2, GLES20.GL_FLOAT, false, 0, mVerBuffer);
            //激活纹理索引
            GLES20.glEnableVertexAttribArray(mHCoord);
            //传递数据
            GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);

            //生成缓存FBO
            GLES20.glGenFramebuffers(1, fFrame, 0);
            //生成纹理
            GLES20.glGenTextures(1, fTexture, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap, 0);

            mBuffer = ByteBuffer.allocate(mBitmap.getWidth() * mBitmap.getHeight() * 4);
            //绑定缓存FBO
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
            //纹理和缓存绑定FBO
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, fTexture[0], 0);
            //这里设置图片的宽高,注意
            GLES20.glViewport(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
            //绘制巨型
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            //释放索引
            GLES20.glDisableVertexAttribArray(mHPosition);
            GLES20.glDisableVertexAttribArray(mHCoord);
            //从GPU读取到CPU数据的唯一方式
            GLES20.glReadPixels(0, 0, mBitmap.getWidth(), mBitmap.getHeight(), GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE, mBuffer);
            if (mCallback != null) {
                mCallback.onCall(mBitmap.getWidth(), mBitmap.getHeight(), mBuffer);
            }

            GLES20.glDeleteTextures(1, fTexture, 0);
            GLES20.glDeleteFramebuffers(1, fFrame, 0);
            mBitmap.recycle();
        }


    }

    //=================================================================


    //GLSL语言写的小程序
    private String getVertexShader() {
        return "attribute vec4 aPosition;" +
                "attribute vec2 aCoordinate;" +
                "varying vec2 vCoordinate;" +
                "uniform mat4 vMatrix;" +
                "void main() {" +
                "  gl_Position = vMatrix*aPosition;" +
                "  vCoordinate = aCoordinate;" +
                "}";
    }


    //GLSL语言写的小程序
    private String getFragmentShader() {
        return "precision mediump float;" +
                "uniform sampler2D uTexture;" +
                "varying vec2 vCoordinate;" +
                "void main() {" +
                "  vec4 color = texture2D(uTexture, vCoordinate);" +
                "  gl_FragColor = color;" +
                "}";
    }


    //=========================处理回调======================================
    public interface Callback {
        void onCall(int w, int h, ByteBuffer data);
    }


    public void setCallback(Callback callback) {
        this.mCallback = callback;

    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }


//============================矩阵相关的====================================


    public static float[] flip(float[] m, boolean x, boolean y) {
        if (x || y) {
            Matrix.scaleM(m, 0, x ? -1 : 1, y ? -1 : 1, 1);
        }
        return m;
    }

    public static float[] getOriginalMatrix() {
        return new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
    }


    //===============================================创建程序==========================

    /**
     * 加载编译程序
     *
     * @param type
     * @param shaderCode
     * @return
     */
    public int loadShader(int type, String shaderCode) {
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

}
