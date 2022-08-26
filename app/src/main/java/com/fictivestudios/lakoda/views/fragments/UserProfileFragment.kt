package com.fictivestudios.lakoda.views.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.viewModel.UserProfileViewModel
import com.fictivestudios.lakoda.views.activities.MainActivity
import kotlinx.android.synthetic.main.user_profile_fragment.view.*

class UserProfileFragment : BaseFragment() {

    private lateinit var mView: View
    companion object {
        fun newInstance() = UserProfileFragment()
    }

    private lateinit var viewModel: UserProfileViewModel

    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack("USER PROFILE")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView= inflater.inflate(R.layout.user_profile_fragment, container, false)

        mView.btn_follow.setOnClickListener {
            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.friendProfileFragment)
        }
        return mView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

}