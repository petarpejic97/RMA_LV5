package com.example.soundboard

import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var mSoundPool: SoundPool
    private var mLoaded: Boolean = false
    var mSoundMap: HashMap<Int, Int> = HashMap()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.setUpUi()
        this.loadSounds()
    }
    private fun setUpUi() {
        this.ivMamic.setOnClickListener(this)
        this.ivkerum.setOnClickListener(this)
        this.ivkolinda.setOnClickListener(this)
    }
    override fun onClick(v: View) {
        if (this.mLoaded == false) return
        when (v.getId()) {
            R.id.ivMamic -> playSound(R.raw.mamic)
            R.id.ivkerum -> playSound(R.raw.kerum)
            R.id.ivkolinda -> playSound(R.raw.kolinda)
        }
    }
    private fun loadSounds() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.mSoundPool = SoundPool.Builder().setMaxStreams(10).build()
        } else {
            this.mSoundPool = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
        }
        this.mSoundPool.setOnLoadCompleteListener { _, _, _ -> mLoaded = true }
        this.mSoundMap[R.raw.mamic] = this.mSoundPool.load(this, R.raw.mamic, 1)
        this.mSoundMap[R.raw.kolinda] = this.mSoundPool.load(this, R.raw.kolinda, 1)
        this.mSoundMap[R.raw.kerum] = this.mSoundPool.load(this, R.raw.kerum, 1)
    }

    fun playSound(selectedSound: Int) {
        val soundID = this.mSoundMap[selectedSound] ?: 0
        this.mSoundPool.play(soundID, 1f, 1f, 1, 0, 1f)
    }
}