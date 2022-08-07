package com.mhy.landrestoration.ui.restoration.distance

import androidx.fragment.app.Fragment
import com.mhy.landrestoration.ui.restoration.RestorationFragment

class DistanceFragment: RestorationFragment() {
    override fun getFragments(): List<Fragment> {
        fragmentTitles.add("距離計算")
        return listOf(DistancePageFragment())
    }
}