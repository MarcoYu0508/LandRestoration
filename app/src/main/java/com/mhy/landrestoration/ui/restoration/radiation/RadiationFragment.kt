package com.mhy.landrestoration.ui.restoration.radiation

import androidx.fragment.app.Fragment
import com.mhy.landrestoration.enums.RestorationType
import com.mhy.landrestoration.ui.restoration.RestorationFragment
import com.mhy.landrestoration.ui.restoration.result.CalculateResultListPageFragment

class RadiationFragment : RestorationFragment() {
    override fun getFragments(): List<Fragment> {
        fragmentTitles.add("光線法")
        fragmentTitles.add("成果檢視")
        return listOf(
            RadiationPageFragment(),
            CalculateResultListPageFragment(RestorationType.Radiation, true)
        )
    }
}