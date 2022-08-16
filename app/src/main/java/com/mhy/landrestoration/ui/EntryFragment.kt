package com.mhy.landrestoration.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mhy.landrestoration.R
import com.mhy.landrestoration.databinding.FragmentEntryBinding

private const val TAG = "EntryFragment"

class EntryFragment : Fragment() {

    private var binding: FragmentEntryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentEntryBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner

            btnImport.setOnClickListener {
                findNavController().navigate(R.id.action_entryFragment_to_projectListFragment)
            }

            btnRadiationMethod.setOnClickListener {
                findNavController().navigate(R.id.action_entryFragment_to_radiationFragment)
            }

            btnDistanceCalc.setOnClickListener {
                findNavController().navigate(R.id.action_entryFragment_to_distanceFragment)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}