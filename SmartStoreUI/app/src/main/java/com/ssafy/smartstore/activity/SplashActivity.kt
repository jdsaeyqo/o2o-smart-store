package com.ssafy.smartstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.ssafy.smartstore.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val backgroundImage: ImageView = findViewById(R.id.iv_splash)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.side_slide)
        backgroundImage.startAnimation(slideAnimation)

        val backgroundTit: TextView = findViewById(R.id.tv_tit)
        val textSlideAnimation = AnimationUtils.loadAnimation(this, R.anim.text_slide)
        backgroundTit.startAnimation(textSlideAnimation)

        val backgroundcon: TextView = findViewById(R.id.tv_con)
        val textSlideAnimation1 = AnimationUtils.loadAnimation(this, R.anim.text_slide)
        backgroundcon.startAnimation(textSlideAnimation1)




        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }, DURATION)

    }
    companion object {
        private const val DURATION : Long = 5000
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }
}