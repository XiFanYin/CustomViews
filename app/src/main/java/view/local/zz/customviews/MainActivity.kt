package view.local.zz.customviews

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import view.local.zz.customviews.activity.AnimationButtonActivity
import view.local.zz.customviews.activity.CircleProgressActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        animationBtn.setOnClickListener { startActivity(Intent(this, AnimationButtonActivity::class.java)) }

        circle_progress.setOnClickListener { startActivity(Intent(this, CircleProgressActivity::class.java)) }
    }
}
