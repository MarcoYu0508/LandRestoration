package com.mhy.landrestoration.ui.restoration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mhy.landrestoration.adapter.FragmentPageAdapter
import com.mhy.landrestoration.databinding.FragmentRestorationBinding
import com.mhy.landrestoration.util.ShowAlert

abstract class RestorationFragment : Fragment() {

    private var binding: FragmentRestorationBinding? = null

    protected val fragmentTitles = mutableListOf<String>()

    protected val showAlert = ShowAlert()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentRestorationBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FragmentPageAdapter(this, getFragments())

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner

            pager.reduceDragSensitivity()
            pager.adapter = adapter

            TabLayoutMediator(
                tabLayout,
                pager
            ) { tab: TabLayout.Tab, position: Int ->
                tab.text = fragmentTitles[position]
            }.attach()
        }
    }

    abstract fun getFragments(): List<Fragment>

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    /**
     * Reduces drag sensitivity of [ViewPager2] widget
     */
    private fun ViewPager2.reduceDragSensitivity() {
        val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true
        val recyclerView = recyclerViewField.get(this) as RecyclerView

        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true
        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop * 4)       // "8" was obtained experimentally
    }
}