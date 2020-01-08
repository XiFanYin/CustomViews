package view.local.zz.customviews.activity

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_bezuer_line.*
import view.local.zz.customviews.R

/**
 * Created by Administrator on 2018/7/12.
 */

class BezuerLineActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bezuer_line)
        bez.start()

    }

}
