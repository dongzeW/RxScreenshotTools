package com.wwd.rsscreenshot

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.View
import com.trello.rxlifecycle2.components.RxActivity
import com.wwd.screenshot.RxScreenshotDetector
import com.wwd.screenshot.ScreenshotDetector
import com.www.utils.Util
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.functions.Cancellable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import kotlinx.android.synthetic.main.activity_main.*


class MainActivityFour : FragmentActivity() {
    var screenshotDetector: ScreenshotDetector? = null
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        screenshotDetector = ScreenshotDetector(this)
        screenshotDetector!!.start()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (Util.isForeground(this)) {
                    Log.d("www", it)
                }
            }
        mSearchButton.setOnClickListener {
            startActivity(Intent(this, MainActivityFive::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        screenshotDetector!!.unregisterObserver()
    }
}
