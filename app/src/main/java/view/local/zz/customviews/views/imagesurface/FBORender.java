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


/**
 * 这里没有使用VBO，但是使用了FBO。FBO是为了离屏渲染，比如图片变色做的
 * https://blog.csdn.net/york2017/article/details/111500883?spm=1001.2014.3001.5502
 *
 * 需要注意，当前是FBO只与Texture绑定
 *
 */
public class FBORender implements GLSurfaceView.Renderer {
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

    private int[] fTexture = new int[2];

    private ByteBuffer mBuffer;

    private Callback mCallback;

    public FBORender() {
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
        //注意：这里不设置大小是因为要吧图片大小设置为要渲染的大小，
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清空画布，设置颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if (mBitmap != null && !mBitmap.isRecycled()) {
            GLES20.glUseProgram(mProgram);
            //传递矩阵
            GLES20.glUniformMatrix4fv(mHMatrix, 1, false, matrix, 0);
            //激活纹理单元
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glUniform1i(mHTexture, 0);

            //激活顶点索引
            GLES20.glEnableVertexAttribArray(mHPosition);
            //传递数据｛参数讲解详见ImageRender类说明｝
            GLES20.glVertexAttribPointer(mHPosition, 2, GLES20.GL_FLOAT, false, 0, mVerBuffer);
            //激活纹理索引
            GLES20.glEnableVertexAttribArray(mHCoord);
            //传递数据
            GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);

            //生成缓存FBO
            GLES20.glGenFramebuffers(1, fFrame, 0);
            //生成两个纹理，一个是图片纹理，一个是图片纹理，一个是缓存纹理
            GLES20.glGenTextures(2, fTexture, 0);
            for (int i = 0; i < 2; i++) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[i]);
                if (i == 0) {
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap, 0);
                } else {
                    //生成一个接受渲染后的数据
                    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap.getWidth(), mBitmap.getHeight(),
                            0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
                }
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            }

            //创建图片缓存容器
            mBuffer = ByteBuffer.allocate(mBitmap.getWidth() * mBitmap.getHeight() * 4);
            //绑定缓存FBO
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
            //为FrameBuffer挂载Texture[1]纹理来存储颜色
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, fTexture[1], 0);
            //这里设置视口的宽高，所以这里就不用换算其他矩阵了，只需要反转一下即可
            GLES20.glViewport(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[0]);

            //绘制巨型
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            //释放索引
            GLES20.glDisableVertexAttribArray(mHPosition);
            GLES20.glDisableVertexAttribArray(mHCoord);
            //从GPU读取到CPU数据的唯一方式
            GLES20.glReadPixels(0, 0, mBitmap.getWidth(), mBitmap.getHeight(), GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE, mBuffer);
            //渲染好的数据回调
            if (mCallback != null) {
                mCallback.onCall(mBitmap.getWidth(), mBitmap.getHeight(), mBuffer);
            }
            //释放纹理和渲染对象
            GLES20.glDeleteTextures(2, fTexture, 0);
            GLES20.glDeleteFramebuffers(1, fFrame, 0);
            //释放图片
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
                "vec4 color = texture2D(uTexture, vCoordinate);" +
                "float rgb=color.g;" +
                "vec4 c=vec4(rgb,rgb,rgb,color.a);" +
                "gl_FragColor = c;" +
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
