package view.local.zz.customviews.views.camerasurface;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRender implements GLSurfaceView.Renderer {


    //顶点坐标
    private float pos[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f,
    };

    //纹理坐标
    private float[] coord = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    private CameraView cameraView;

    /**
     * 顶点坐标Buffer
     */
    protected FloatBuffer mVerBuffer;

    /**
     * 纹理坐标Buffer
     */
    protected FloatBuffer mTexBuffer;

    //纹理引用
    private int texture;

    //设置给相机的预览View
    private SurfaceTexture surfaceTexture;


    //着色器程序
    private int mProgram;
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

    //相机工具类
    private KitkatCamera mCamera2;

    //相机ID
    private int cameraID = 0;

    //预览大小
    private Point dataPoint;

    //变幻的矩阵
    private float[] matrix = new float[16];


    //构造方法
    public CameraRender(CameraView cameraView) {
        this.cameraView = cameraView;
        //定点数据复制到本地内存
        ByteBuffer a = ByteBuffer.allocateDirect(pos.length * 4);
        a.order(ByteOrder.nativeOrder());
        mVerBuffer = a.asFloatBuffer();
        mVerBuffer.put(pos);
        mVerBuffer.position(0);

        ByteBuffer b = ByteBuffer.allocateDirect(coord.length * 4);
        b.order(ByteOrder.nativeOrder());
        mTexBuffer = b.asFloatBuffer();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);

        mCamera2 = new KitkatCamera();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //清除颜色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        //创建一个纹理
        texture = createTextureID();
        //创建SurfaceTexture 传入纹理对象引用
        surfaceTexture = new SurfaceTexture(texture);

        //加载程序
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

        //验证程序编译是否正确
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e("rrrrrrrrrrrrr", "程序编译出错了");
        } else {
            Log.e("rrrrrrrrrrrrr", "程序编译正确了");
        }

        //获取索引
        mHPosition = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mHCoord = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
        mHMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        mHTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");

        //打开相机设置预览，开始预览
        mCamera2.open(cameraID);
        //获取预览大小
        dataPoint = mCamera2.getPreviewSize();
        //设置预览
        mCamera2.setPreviewTexture(surfaceTexture);
        //此处调用渲染，会触发onDrawFrame执行
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                cameraView.requestRender();
            }
        });
        //开启预览
        mCamera2.preview();
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //计算矩阵
        if (dataPoint != null && width > 0 && height > 0 && dataPoint.x > 0 && dataPoint.y > 0) {
            float sWhView = (float) width / height;
            float sWhImg = (float) dataPoint.x / dataPoint.y;
            float[] projection = new float[16];
            float[] camera = new float[16];
            if (sWhImg > sWhView) {
                //宽度放不下，裁剪宽度
                Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1, 1, 1, 3);
            } else {
                //宽度能放下，以宽度为基准，高度上下留白
                Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
            }
            Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
            Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
        }

        if (cameraID == 1) {
            //左右镜像
            Matrix.scaleM(matrix, 0, -1, 1, 1);
            //旋转
            Matrix.rotateM(matrix, 0, 90, 0, 0, 1);
        } else {
            //旋转
            Matrix.rotateM(matrix, 0, 270, 0, 0, 1);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (surfaceTexture != null) {
            surfaceTexture.updateTexImage();
        }
        //清空颜色，使用之前配置好的颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //使用程序
        GLES20.glUseProgram(mProgram);
        //激活纹理---默认状态是激活0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //将激活的纹理单元传递到着色器里面，这里的0和GLES20.GL_TEXTURE0一一对应
        GLES20.glUniform1i(mHTexture, 0);
        //设置矩阵
        GLES20.glUniformMatrix4fv(mHMatrix, 1, false, matrix, 0);
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
        //释放索引
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);
    }


    private int createTextureID() {
        //可以储存多个纹理，因为我们这里只使用一个纹理，所以数组长度是1
        int[] texture = new int[1];
        //生成纹理
        GLES20.glGenTextures(1, texture, 0);
        //绑定纹理，设置纹理显示方式
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
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
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;" +
                "varying vec2 vCoordinate;" +
                "uniform samplerExternalOES vTexture;" +
                "void main() {" +
                "    gl_FragColor = texture2D( vTexture, vCoordinate );" +
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


    public void close() {
        mCamera2.close();
    }

}
