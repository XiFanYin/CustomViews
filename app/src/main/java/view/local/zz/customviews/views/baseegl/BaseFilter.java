package view.local.zz.customviews.views.baseegl;


import android.opengl.GLES20;

/**
 * 对纹理进行处理的base基础类
 */
public abstract class BaseFilter {

    //生成编译GLSL程序的方法
    public abstract void createProgram();


    //生成的纹理ID
    public abstract void setTextureId(int textureId);


    //做绘制，
    public abstract void draw();

    //设置变换矩阵
    public abstract void setMatrix(float[] matrix);

    //释放
    public abstract void release();


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

    /**
     * 编译生成程序
     *
     * @return
     */
    public int createShader(int vertexShader, int fragmentShader) {
        //创建一个空的OpenGLES程序
        int mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);

        return mProgram;
    }

}
