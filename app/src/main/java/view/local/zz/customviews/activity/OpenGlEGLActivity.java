package view.local.zz.customviews.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Matrix;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import view.local.zz.customviews.R;
import view.local.zz.customviews.views.baseegl.EGL20Envi;
import view.local.zz.customviews.views.baseegl.MyFilter;

public class OpenGlEGLActivity extends AppCompatActivity {

    private EGL20Envi mBackEnv;
    private ImageView iamgeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gle);
        iamgeView = findViewById(R.id.iamgeView);

        //获取图片
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.fbb);
        int mBmpWidth = bmp.getWidth();
        int mBmpHeight = bmp.getHeight();
        //创建一个ES环境
        mBackEnv = new EGL20Envi(mBmpWidth, mBmpHeight);
        //设置线程标识
        mBackEnv.setThreadOwner(getMainLooper().getThread().getName());
        //创建真正的渲染类
        MyFilter filter = new MyFilter();
        //渲染类设置矩阵变换
        //生成矩阵
        float[] matrix = flip(getOriginalMatrix(), true, true);
        //设置矩阵
        filter.setMatrix(matrix);
        //注入实现渲染的类
        mBackEnv.setFilter(filter);
        //图片去变换
        Bitmap finalBitmal = mBackEnv.setBitmap(bmp);
        //释放渲染
        filter.release();
        //展示图片
        iamgeView.setImageBitmap(finalBitmal);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放ES环境
        mBackEnv.destroy();
    }

    public float[] flip(float[] m, boolean x, boolean y) {
        if (x || y) {
            Matrix.scaleM(m, 0, x ? -1 : 1, y ? -1 : 1, 1);
        }
        return m;
    }

    public float[] getOriginalMatrix() {
        return new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
    }


}
