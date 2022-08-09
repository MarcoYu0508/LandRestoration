package com.mhy.landrestoration.ui.restoration.distance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mhy.landrestoration.enums.RestorationType
import com.mhy.landrestoration.ui.restoration.CalculateResultPageFragment
import com.mhy.landrestoration.ui.restoration.RestorationFragment

private const val TAG = "DistanceFragment"

class DistanceFragment : RestorationFragment() {
    override fun getFragments(): List<Fragment> {
        fragmentTitles.add("距離計算")
        fragmentTitles.add("成果檢視")
        return listOf(DistancePageFragment(), CalculateResultPageFragment(RestorationType.Distance))
    }
}