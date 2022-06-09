package view.local.zz.customviews.views.baseegl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public class MyFilter extends BaseFilter {

    //创建纹理坐标
    private float[] texture = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };

    //创建定点坐标数据
    private float[] vertexs = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    /**
     * 程序句柄
     */
    private int mProgram;


    /**
     * 顶点坐标Buffer
     */
    protected FloatBuffer mVerBuffer;

    /**
     * 纹理坐标Buffer
     */
    protected FloatBuffer mTexBuffer;


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

    private int textureId;

    private float[] matrix = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };


    //构造方法
    public MyFilter() {
        //定点数据复制到本地内存
        ByteBuffer a = ByteBuffer.allocateDirect(vertexs.length * 4);
        a.order(ByteOrder.nativeOrder());
        mVerBuffer = a.asFloatBuffer();
        mVerBuffer.put(vertexs);
        mVerBuffer.position(0);

        ByteBuffer b = ByteBuffer.allocateDirect(texture.length * 4);
        b.order(ByteOrder.nativeOrder());
        mTexBuffer = b.asFloatBuffer();
        mTexBuffer.put(texture);
        mTexBuffer.position(0);
    }

    @Override
    public void createProgram() {
        //编译程序
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, getVertexShader());
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShader());
        //生成程序
        mProgram = createShader(vertexShader, fragmentShader);

        //获取索引
        mHPosition = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mHCoord = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
        mHMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        mHTexture = GLES20.glGetUniformLocation(mProgram, "uTexture");
        //删除shader
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

    }

    @Override
    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    @Override
    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }

    @Override
    public void draw() {
        //清空背景
        onClear();
        //使用程序
        onUseProgram();
        //设置变换矩阵，调用子类,子类又调用父类
        onSetExpandData();
        //完全调用子类
        onBindTexture();
        //绘制
        onDraw();
    }


    @Override
    public void release() {
        //释放索引
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);
        GLES20.glDeleteProgram(mProgram);
    }

    private void onDraw() {
        //激活顶点索引
        GLES20.glEnableVertexAttribArray(mHPosition);
        //传递数据
        GLES20.glVertexAttribPointer(mHPosition, 2, GLES20.GL_FLOAT, false, 0, mVerBuffer);
        //激活纹理索引
        GLES20.glEnableVertexAttribArray(mHCoord);
        //传递数据
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);
        //绘制巨型
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    private void onBindTexture() {
        //激活纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(mHTexture, 0);
        //绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
    }

    private void onSetExpandData() {
        GLES20.glUniformMatrix4fv(mHMatrix, 1, false, matrix, 0);
    }

    private void onUseProgram() {
        GLES20.glUseProgram(mProgram);
    }

    private void onClear() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
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
                "vec4 color = texture2D(uTexture, vCoordinate);" +
                "float rgb=color.g;" +
                "vec4 c=vec4(rgb,rgb,rgb,color.a);" +
                "gl_FragColor = c;" +
                "}";
    }


}
