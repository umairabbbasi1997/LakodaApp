package com.fictivestudios.lakoda.views.fragments

import android.app.Activity
import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.viewModel.PreLoginViewModel
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import kotlinx.android.synthetic.main.fragment_agreement.*
import kotlinx.android.synthetic.main.fragment_agreement.view.*
import kotlinx.android.synthetic.main.pre_login_fragment.view.*

class PreLoginFragment : BaseFragment() {

    companion object {
        fun newInstance() = PreLoginFragment()
    }

    private lateinit var preLoginBinding: View
    private lateinit var viewModel: PreLoginViewModel
    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.hideTitleBar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preLoginBinding = inflater.inflate(R.layout.pre_login_fragment, container, false)

        preLoginBinding.btn_email.setOnClickListener {


/*            RegisterationActivity.getRegActivity
                ?.navControllerReg?.navigate(R.id.agreementFragment)*/
            showAgreementDialog()


        //    RegisterationActivity.getRegActivity?.replaceFragment(AgreementFragment(),AgreementFragment::javaClass.name,true,false)

        }

        return preLoginBinding
    }


    private fun showAgreementDialog() {

        var dialog = Dialog(context as Activity)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(true)
        dialog!!.setContentView(R.layout.fragment_agreement)

        resizeDialogView(dialog!!,70)
        dialog!!.show()

        var btnAccept:Button= dialog!!.findViewById<Button>(R.id.btn_accept)
        var btnDecline:Button= dialog!!.findViewById<Button>(R.id.btn_reject)

        var btnTerms:CheckBox= dialog!!.findViewById<CheckBox>(R.id.tv_terms)
        var btnPrivacy:CheckBox= dialog!!.findViewById<CheckBox>(R.id.tv_privacy)

        btnAccept.setOnClickListener {


            if (!btnTerms.isChecked )
            {
                Toast.makeText(context, getString(R.string.please_accept_terms), Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }
            if (!btnPrivacy.isChecked)
            {
                Toast.makeText(context, getString(R.string.please_accept_terms), Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }
            else
            {
                dialog!!.dismiss()
                RegisterationActivity?.getRegActivity
                    ?.navControllerReg?.navigate(R.id.loginFragment)
            }

        }

        btnDecline.setOnClickListener {
            dialog!!.dismiss()
        }




    }
    private fun resizeDialogView(dialog: Dialog, percent: Int) {
        val displayMetrics = DisplayMetrics()

        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)

        val dialogWidth = screenWidth * 95 / 100
        val dialogHeight = screenHeight * percent / 100

        layoutParams.width = dialogWidth
        layoutParams.height = dialogHeight

        dialog.window?.attributes = layoutParams
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PreLoginViewModel::class.java)
      }

}