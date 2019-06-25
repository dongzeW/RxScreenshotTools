package com.wwd.rsscreenshot

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.trello.rxlifecycle2.components.RxActivity
import com.wwd.screenshot.RxScreenshotDetector
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.functions.Cancellable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : RxBActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RxScreenshotDetector.start(this).compose(bindToLifecycle()).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d("www", it)
            }
        createButtonClickObservable().compose(bindToLifecycle()).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d("www", it)
                startActivity(Intent(this, MainActivityTow::class.java))
                finish()
            }
    }

    /**
     * 测试emitter cancellable触发点
     * 当activity finish的时候会回调cancellable
     */
    private fun createButtonClickObservable(): Observable<String> {
        return Observable.create(ObservableOnSubscribe<String> { emitter ->
            mSearchButton.setOnClickListener {
                Log.d("www", "onnext")
                emitter.onNext("onnext invoke")
            }
            emitter.setCancellable {
                Log.d("www", "cancel")
                mSearchButton.setOnClickListener(null)
            }
        })
    }
}
