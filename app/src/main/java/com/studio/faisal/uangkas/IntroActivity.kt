package com.studio.faisal.uangkas

import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.studio.faisal.uangkas.fragment.Intro1
import com.studio.faisal.uangkas.fragment.Intro2
import com.studio.faisal.uangkas.fragment.Intro3

class IntroActivity : AppIntro() {
    var intro1 = Intro1()
    var intro2 = Intro2()
    var intro3 = Intro3()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSlide(AppIntroFragment.newInstance("SELAMAT DATANG", "Saya siap menjadi Android Developer!", R.drawable.android,
                resources.getColor(R.color.colorPrimary)))
        addSlide(AppIntroFragment.newInstance("UANGKAS v.2", "Belajar Membuat Aplikasi Manajemen Keuangan Dengan Android Studio, PHP & MySQL", R.drawable.calc,
                resources.getColor(R.color.colorPrimary)))
        addSlide(AppIntroFragment.newInstance("Lanjutkan", "Selamat Mencoba!", R.drawable.coder_uniska,
                resources.getColor(R.color.colorPrimary)))
        setBarColor(resources.getColor(R.color.colorPrimary))
        setSeparatorColor(resources.getColor(R.color.circle_background))
        // Hide Skip/Done button.
        showSkipButton(true)
        isProgressButtonEnabled = true
        // Turn vibration on and set intensity.
// NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true)
        setVibrateIntensity(30)
    }

    override fun onSkipPressed(currentFragment: Fragment) {
        super.onSkipPressed(currentFragment)
        // Do something when users tap on Skip button.
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)
        // Do something when users tap on Done button.
        finish()
    }

    override fun onSlideChanged(oldFragment: Fragment?,
                                newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)
        // Do something when the slide changes.
    }
}