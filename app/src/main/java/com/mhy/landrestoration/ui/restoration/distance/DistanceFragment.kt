package com.mhy.landrestoration.ui.restoration.distance

import androidx.fragment.app.Fragment
import com.mhy.landrestoration.enums.RestorationType
import com.mhy.landrestoration.ui.restoration.result.CalculateResultListPageFragment
import com.mhy.landrestoration.ui.restoration.RestorationFragment

private const val TAG = "DistanceFragment"

class DistanceFragment : RestorationFragment() {
    override fun getFragments(): List<Fragment> {
        fragmentTitles.add("距離計算")
        fragmentTitles.add("成果檢視")
        return listOf(DistancePageFragment(), CalculateResultListPageFragment(RestorationType.Distance))
    }
}