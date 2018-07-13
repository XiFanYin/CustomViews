package view.local.zz.customviews.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_circle_bezier_progress.*
import view.local.zz.customviews.R
import java.util.*

/**
 * Created by Administrator on 2018/7/13.
 */

class CircleBezierProgressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_bezier_progress)
        circleBezierProgress.setProgress(100, 8000)
        circleBezierProgress.setOnClickListener {
            circleBezierProgress.setProgress(Random().nextInt(100), 3000)
        }
    }


}
