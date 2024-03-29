package view.local.zz.customviews.views.imagesurface;

import android.graphics.Bitmap;
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
 * 注意： GLES20.glVertexAttribPointer 参数有重载方法，一个是传递的数据索引，一个传递的是vbo数据偏移量，具体是什么要根据是否使用VBO去传递
 * 说明地址：https://blog.csdn.net/weixin_37459951/article/details/96433508
 * 该渲染器未使用VBO
 */
public class ImageRender implements GLSurfaceView.Renderer {

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




    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private int mProgram;
    private int mVertexPos;
    private int mTexturePos;
    private int mTextureHandler;
    private int glHMatrix;
    private Bitmap mBitmap;

    //变换矩阵
    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];

    /**
     *     https://blog.csdn.net/jeffdeen/article/details/55001797
     *   注意，程序的生成代码必须在渲染线程中处理，否则渲染不出来
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景图片颜色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
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

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        //处理图片变形问题,推导过程https://juejin.cn/post/6844903984235282445#heading-9
        int w=mBitmap.getWidth();
        int h=mBitmap.getHeight();
        float sWH=w/(float)h;
        float sWidthHeight=width/(float)height;
        //视口宽度大于高度，横屏
        if(width>height){
            if(sWH>sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight*sWH,sWidthHeight*sWH, -1,1, 3, 5);
            }else{
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight/sWH,sWidthHeight/sWH, -1,1, 3, 5);
            }
        }else{
            if(sWH>sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1/sWidthHeight*sWH, 1/sWidthHeight*sWH,3, 5);
            }else{
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH/sWidthHeight, sWH/sWidthHeight,3, 5);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清空颜色，使用之前配置好的颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        //使用程序
        GLES20.glUseProgram(mProgram);

        //找到索引点
        mVertexPos = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTexturePos = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
        mTextureHandler = GLES20.glGetUniformLocation(mProgram, "uTexture");
        glHMatrix=GLES20.glGetUniformLocation(mProgram,"vMatrix");

        //启用索引
        GLES20.glEnableVertexAttribArray(mVertexPos);
        GLES20.glEnableVertexAttribArray(mTexturePos);

        /**
         * 这里未使用VBO的参数解析
         * 参数一：就是获取到的索引
         * 参数二：每个顶点包含的分量，由于我们上边的顶点数组都是用xy去描述，所以这里传递2
         * 参数三:数据的类型，上边数组是用的float去描述，所以这里传递GLES20.GL_FLOAT
         * 参数四：我们是否希望数据被标准化(Normalize)。如果我们设置为GL_TRUE，所有数据都会被映射到0（对于有符号型signed数据是-1）到1之间。我们把它设置为GL_FALSE。
         * 参数五：步长：就是连续的顶点属性组之间的间隔。举例：x ,y，x ，y ；两个x之间相差2个身位，每个身位占4个字节，所以这里应该填写2*4=8
         * 那为什么这里参数传递是0呢？设置为0是让OpenGL决定具体步长是多少（注意：只有当数值是紧密排列时才可用）
         * 参数六：可以传递cup数据指针，如果使用VBO就传递VBO偏移量，这里没有使用VBO，所以才传递的数据指针
         *
         */
        GLES20.glVertexAttribPointer(mVertexPos, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(mTexturePos, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        //传递矩阵变换
        GLES20.glUniformMatrix4fv(glHMatrix,1,false,mMVPMatrix,0);


        //激活指定纹理单元,系统默认激活的是0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //将激活的纹理单元传递到着色器里面，这里的0和GLES20.GL_TEXTURE0一一对应
        GLES20.glUniform1i(mTextureHandler, 0);
        //可以储存多个纹理，因为我们这里只使用一个纹理，所以数组长度是1
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
        //绘制矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        //解绑 2D纹理，退出对纹理的使用
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }


    //GLSL语言写的小程序
    private String getVertexShader() {
        return "attribute vec4 aPosition;" +
                "attribute vec2 aCoordinate;" +
                "varying vec2 vCoordinate;" +
                "uniform mat4 vMatrix;"+
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


    //传递纹理资源图片
    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }


}
