package view.local.zz.customviews

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import view.local.zz.customviews.activity.AnimationButtonActivity
import view.local.zz.customviews.activity.CircleProgressActivity
import view.local.zz.customviews.activity.HorizontalProgressActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        animationBtn.setOnClickListener { startActivity(Intent(this, AnimationButtonActivity::class.java)) }

        circle_progress.setOnClickListener { startActivity(Intent(this, CircleProgressActivity::class.java)) }

        horizontal_progress.setOnClickListener { startActivity(Intent(this, HorizontalProgressActivity::class.java)) }


    }


}


