package com.fictivestudios.lakoda.views.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.adapters.HomeAdapter
import com.fictivestudios.lakoda.databinding.HomeFragmentBinding
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.viewModel.HomeViewModel

class   HomeFragment : BaseFragment() {

    private lateinit var homeFragmentBinding: HomeFragmentBinding
    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setHomeTitle("HOME")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var mView =inflater.inflate(R.layout.home_fragment, container, false)

        homeFragmentBinding = HomeFragmentBinding.bind(mView)
      //  viewModel = homeFragmentBinding.v
        initRecylcerView()

            return mView
    }



    private fun initRecylcerView() {
        homeFragmentBinding.rvHome.adapter = HomeAdapter()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}