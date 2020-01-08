package view.local.zz.customviews.activity

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_horizontal_progress.*
import view.local.zz.customviews.R
import java.util.*

/**
 * Created by Administrator on 2018/7/11.
 */

class HorizontalProgressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horizontal_progress)
        horizontalProgress.endProgress = 100
        horizontalProgress.start()



        horizontalProgress.setOnClickListener {
            horizontalProgress.endProgress = Random().nextInt(100)
            horizontalProgress.start()
        }
    }
}
