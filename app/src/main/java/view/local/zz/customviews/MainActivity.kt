package view.local.zz.customviews

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import view.local.zz.customviews.activity.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        animationBtn.setOnClickListener { startActivity(Intent(this, AnimationButtonActivity::class.java)) }

        circle_progress.setOnClickListener { startActivity(Intent(this, CircleProgressActivity::class.java)) }

        horizontal_progress.setOnClickListener { startActivity(Intent(this, HorizontalProgressActivity::class.java)) }

        bezbtn.setOnClickListener { startActivity(Intent(this, BezuerLineActivity::class.java)) }

        circle_bezier.setOnClickListener { startActivity(Intent(this, CircleBezierProgressActivity::class.java)) }

        bitmap_view.setOnClickListener { startActivity(Intent(this, BitmapViewActivity::class.java)) }

        view_group1.setOnClickListener { startActivity(Intent(this, MyViewGroup1Activity::class.java)) }

        image_surfaceView.setOnClickListener { startActivity(Intent(this, OpenGLImageActivity::class.java)) }

        image_vbo.setOnClickListener { startActivity(Intent(this, OpenGLVBOImageActivity::class.java))   }

        Image_FBO.setOnClickListener { startActivity(Intent(this, OpenGLFBOImageActivity::class.java))  }

        Image_water.setOnClickListener { startActivity(Intent(this, OpenGlWaterImageActivity::class.java))  }

        camera_surfaceView.setOnClickListener { startActivity(Intent(this, OpenGLCameraActivity::class.java)) }

        video_surfaceView.setOnClickListener { startActivity(Intent(this, OpenGLVideoActivity::class.java)) }
    }


}


