package view.local.zz.customviews.views.imagesurface;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 当前是通过离开屏幕多纹理渲染图片
 */

public class WaterFBORender implements GLSurfaceView.Renderer {

    private Bitmap mBitmap;

    private Bitmap waterBitmap;

    //创建纹理坐标
    float[] texture = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f,

    };

    //创建定点坐标数据
    float[] vertexs = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
            //右下角纹理坐标，这里写的会变形，具体可以根据水印图片和屏幕计算出来一个何时的纹理
            0f, -1f,
            1f, -1f,
            0f, -0.75f,
            1f, -0.75f
    };


    private int[] fFrame = new int[1];

    private int[] fRender = new int[1];
    private int[] fTexture = new int[3];


    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private int mProgram;
    private int mVertexPos;
    private int mTexturePos;
    private int mTextureHandler;
    private int glHMatrix;


    private float[] matrix = new float[16];

    private int vboID;

    private ByteBuffer mBuffer;

    public WaterFBORender(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
        //数据复制到本地内存
        ByteBuffer bb = ByteBuffer.allocateDirect(vertexs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        //转换成浮点数据
        vertexBuffer = bb.asFloatBuffer();
        //把虚拟机数据复制到本地内存中
        vertexBuffer.put(vertexs);
        //位置数据从起始位置开始读取
        vertexBuffer.position(0);

        ByteBuffer tc = ByteBuffer.allocateDirect(texture.length * 4);
        tc.order(ByteOrder.nativeOrder());
        //转换成浮点数据
        textureBuffer = tc.asFloatBuffer();
        //把虚拟机数据复制到本地内存中
        textureBuffer.put(texture);
        //位置数据从起始位置开始读取
        textureBuffer.position(0);


        //创建水印文字图片
        waterBitmap = createTextImage("我是水印文字", 36, "#ff0000", "#00000000", 0);

    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景图片颜色
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

        //找到索引点
        mVertexPos = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTexturePos = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
        mTextureHandler = GLES20.glGetUniformLocation(mProgram, "uTexture");
        glHMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");

        //创建 VBO
        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);
        vboID = vbo[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboID);
        //分配 VBO需要的缓存大小
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexs.length * 4 + texture.length * 4, null, GLES20.GL_STATIC_DRAW);
        //设置顶点坐标数据的值到 VBO
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexs.length * 4, vertexBuffer);
        //设置纹理坐标数据的值到 VBO
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexs.length * 4, texture.length * 4, textureBuffer);
        //解绑 VBO，指的是离开对 VBO的配置，进入下一个状态
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        //设置文字支持透明
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //因为FBO的Y轴正方形是向上，而纹理Y轴正方向是朝下
        matrix = flip(getOriginalMatrix(), false, true);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {


    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清空颜色，使用之前配置好的颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //带有深度的纹理离开屏幕渲染
        createEnvi();
        //使用程序
        GLES20.glUseProgram(mProgram);
        //设置窗口大小
        GLES20.glViewport(0, 0, mBitmap.getWidth(), mBitmap.getHeight());

        //开始使用 VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboID);
        //启用索引
        GLES20.glEnableVertexAttribArray(mVertexPos);
        //设置着色器参数， 第二个参数表示一个顶点包含的数据数量，这里为xy，所以为2
        GLES20.glVertexAttribPointer(mVertexPos, 2, GLES20.GL_FLOAT, false, 8, 0);
        //启用索引
        GLES20.glEnableVertexAttribArray(mTexturePos);
        //设置着色器参数，
        GLES20.glVertexAttribPointer(mTexturePos, 2, GLES20.GL_FLOAT, false, 8, vertexs.length * 4);
        //退出 VBO的使用
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        //传递矩阵变换
        GLES20.glUniformMatrix4fv(glHMatrix, 1, false, matrix, 0);

        //激活指定纹理单元,系统默认激活的是0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //将激活的纹理单元传递到着色器里面，这里的0和GLES20.GL_TEXTURE0一一对应
        GLES20.glUniform1i(mTextureHandler, 0);
        //绑定第一个纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[0]);
        //绘制矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        //解绑 2D纹理，退出对纹理的使用
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);


        //开始使用 VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboID);
        //启用索引
        GLES20.glEnableVertexAttribArray(mVertexPos);
        //设置着色器参数， 第二个参数表示一个顶点包含的数据数量，这里为xy，所以为2
        GLES20.glVertexAttribPointer(mVertexPos, 2, GLES20.GL_FLOAT, false, 0, vertexs.length * 2);
        //启用索引
        GLES20.glEnableVertexAttribArray(mTexturePos);
        //设置着色器参数，
        GLES20.glVertexAttribPointer(mTexturePos, 2, GLES20.GL_FLOAT, false, 0, vertexs.length * 4);
        //退出 VBO的使用
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        //传递矩阵变换
        GLES20.glUniformMatrix4fv(glHMatrix, 1, false, matrix, 0);

        //激活指定纹理单元,系统默认激活的是0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //将激活的纹理单元传递到着色器里面，这里的0和GLES20.GL_TEXTURE0一一对应
        GLES20.glUniform1i(mTextureHandler, 0);


        //绑定第二个纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[1]);

        //绘制矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        //解绑 2D纹理，退出对纹理的使用
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        //释放索引
        GLES20.glDisableVertexAttribArray(mVertexPos);
        GLES20.glDisableVertexAttribArray(mTexturePos);
        //从GPU读取到CPU数据的唯一方式
        GLES20.glReadPixels(0, 0, mBitmap.getWidth(), mBitmap.getHeight(), GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, mBuffer);
        //渲染好的数据回调
        if (mCallback != null) {
            mCallback.onCall(mBitmap.getWidth(), mBitmap.getHeight(), mBuffer);
        }
        //释放纹理和渲染对象
        GLES20.glDeleteTextures(2, fTexture, 0);
        GLES20.glDeleteRenderbuffers(1, fRender, 0);
        GLES20.glDeleteFramebuffers(1, fFrame, 0);

    }


    public void createEnvi() {
        //生成FBO
        GLES20.glGenFramebuffers(1, fFrame, 0);
        //生成Render
        GLES20.glGenRenderbuffers(1, fRender, 0);
        //绑定Render
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, fRender[0]);
        //设置为深度的Render Buffer，并传入大小
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
                mBitmap.getWidth(), mBitmap.getHeight());
        //为FrameBuffer挂载fRender[0]来存储深度，因为是多纹理渲染
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, fRender[0]);
        //解绑
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        //生成纹理，这里正常三个纹理
        GLES20.glGenTextures(3, fTexture, 0);
        for (int i = 0; i < 3; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[i]);
            if (i == 0) {
                //生成有图纹理
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap, 0);
            } else if (i == 1) {
                //生成水印纹理
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, waterBitmap, 0);
            } else {
                //创建一个空纹理，做为数据输出
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap.getWidth(), mBitmap.getHeight(),
                        0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            }
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }
        //创建数据回调储存容器
        mBuffer = ByteBuffer.allocate(mBitmap.getWidth() * mBitmap.getHeight() * 4);
        //绑定FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
        //这里绑定的是即将写入数据的纹理对象
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, fTexture[2], 0);
        //绑定深度数据储存
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, fRender[0]);
    }


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


    public static Bitmap createTextImage(String text, int textSize, String textColor, String bgColor, int padding) {

        Paint paint = new Paint();
        paint.setColor(Color.parseColor(textColor));
        paint.setTextSize(textSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        float width = paint.measureText(text, 0, text.length());
        float top = paint.getFontMetrics().top;
        float bottom = paint.getFontMetrics().bottom;

        Bitmap bm = Bitmap.createBitmap((int) (width + padding * 2), (int) ((bottom - top) + padding * 2), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.drawColor(Color.parseColor(bgColor));
        canvas.drawText(text, padding, -top + padding, paint);
        return bm;

    }


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


    private Callback mCallback;

    public interface Callback {
        void onCall(int w, int h, ByteBuffer data);
    }


    public void setCallback(Callback callback) {
        this.mCallback = callback;

    }


}
