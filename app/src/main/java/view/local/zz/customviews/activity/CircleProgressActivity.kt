package view.local.zz.customviews.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_circle_progress.*

import view.local.zz.customviews.R
import java.util.*

/**
 * Created by Administrator on 2018/7/10.
 */

class CircleProgressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_progress)

        my_progress.endProgress = 100
        my_progress.start()

        my_progress.setOnClickListener {
            my_progress.endProgress =  Random().nextInt(100)
            my_progress.start()
        }
    }


}
