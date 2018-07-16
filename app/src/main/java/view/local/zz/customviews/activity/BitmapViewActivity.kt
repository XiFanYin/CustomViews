package view.local.zz.customviews.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_bitmap_view.*
import view.local.zz.customviews.R

/**
 * Created by Administrator on 2018/7/16.
 */

class BitmapViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap_view)
        bitmapView.setOnClickListener {
            bitmapView.start()
        }

    }

}
