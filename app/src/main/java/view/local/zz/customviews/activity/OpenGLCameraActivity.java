package view.local.zz.customviews.activity;


import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;
import view.local.zz.customviews.R;

public class OpenGLCameraActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new RxPermissions(this).
                request(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            setContentView(R.layout.activity_opengl_camera);
                        } else {
                            Toast.makeText(OpenGLCameraActivity.this,"没有权限",Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
}
