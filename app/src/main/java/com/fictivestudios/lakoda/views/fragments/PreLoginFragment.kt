package com.fictivestudios.lakoda.views.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.docsvisor.apiManager.client.SessionManager
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.LoginResponse
import com.fictivestudios.lakoda.apiManager.response.SocialLoginResponse
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_agreement.*
import kotlinx.android.synthetic.main.fragment_agreement.view.*
import kotlinx.android.synthetic.main.pre_login_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.util.*

class PreLoginFragment : BaseFragment() {

    companion object {
        fun newInstance() = PreLoginFragment()
    }

    private var callbackManager: CallbackManager? = null
    private lateinit var preLoginBinding: View

    private val RC_SIGN_IN = 234
    lateinit var mGoogleSignInClient:GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    lateinit var sessionManager: SessionManager
    private var socialLoginType = ""

    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.hideTitleBar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preLoginBinding = inflater.inflate(R.layout.pre_login_fragment, container, false)

        sessionManager = SessionManager(requireContext())


         callbackManager = CallbackManager.Factory.create()

        initFaceBook()

        preLoginBinding.btn_email.setOnClickListener {


            showAgreementDialog()
        }


        preLoginBinding.btn_google.setOnClickListener {

            googleSignIn()

        }

        preLoginBinding.btn_fb_login.setOnClickListener {
                LoginManager.getInstance().logInWithReadPermissions(
                    this,
                    Arrays.asList("email","public_profile")
                );
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("832334397993-amp41iejkj7dh038fnl9abn1jl9octub.apps.googleusercontent.com")
            .requestEmail()
            .build()
        auth = FirebaseAuth.getInstance()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        return preLoginBinding
    }


    private fun showAgreementDialog() {

        var dialog = Dialog(context as Activity)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.fragment_agreement)

        resizeDialogView(dialog,70)
        dialog?.show()

        var btnAccept:Button= dialog?.findViewById<Button>(R.id.btn_accept)
        var btnDecline:Button= dialog?.findViewById<Button>(R.id.btn_reject)

        var btnTerms:CheckBox= dialog?.findViewById<CheckBox>(R.id.tv_terms)
        var btnPrivacy:CheckBox= dialog?.findViewById<CheckBox>(R.id.tv_privacy)

        var tvTerms:TextView= dialog?.findViewById<TextView>(R.id.text_term)
        var tvPrivacy:TextView= dialog?.findViewById<TextView>(R.id.text_privacy)

        btnAccept.setOnClickListener {


            if (!btnTerms.isChecked )
            {
                Toast.makeText(requireContext(), getString(R.string.please_accept_terms), Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }
            if (!btnPrivacy.isChecked)
            {
                Toast.makeText(requireContext(), getString(R.string.please_accept_terms), Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }
            else
            {
                dialog?.dismiss()
                RegisterationActivity?.getRegActivity
                    ?.navControllerReg?.navigate(R.id.loginFragment)
            }

        }

        btnDecline.setOnClickListener {
            dialog?.dismiss()
        }

        tvTerms.setOnClickListener {
            dialog?.dismiss()
            RegisterationActivity?.getRegActivity
                ?.navControllerReg?.navigate(R.id.termsAndConditionFragment)
        }

        tvPrivacy.setOnClickListener {
            dialog?.dismiss()
            RegisterationActivity?.getRegActivity
                ?.navControllerReg?.navigate(R.id.privacyAndPolicyFragment)
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




    private fun socialLogin(idToken: String, displayName: String?,loginType:String)
    {
        preLoginBinding.pb_login.visibility=View.VISIBLE


        var token = PreferenceUtils.getString(Constants.FCM,"")

        Log.d("fcmToken",token.toString())
        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)



            .addFormDataPart("social_token", idToken)
            .addFormDataPart("social_type", loginType)
            .addFormDataPart("device_type", "android")
            .addFormDataPart("device_token", token.toString())
            .addFormDataPart("name", displayName.toString())
            .build()


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.socialLogin(requestBody).enqueue(object: retrofit2.Callback<SocialLoginResponse> {
                override fun onResponse(
                    call: Call<SocialLoginResponse>,
                    response: Response<SocialLoginResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        preLoginBinding.pb_login.visibility=View.GONE






                    try {
                        preLoginBinding.pb_login.visibility=View.GONE

                        val loginResponse: SocialLoginResponse? =response.body()
                        val statuscode= loginResponse!!.status
                        Log.d("response",loginResponse.message)
                        Log.d("response",loginResponse.status.toString())



                        if (statuscode==1)
                        {
                            Log.d("response",loginResponse.data.toString())
                            val gson = Gson()
                            val json:String = gson.toJson(loginResponse.data )
                            PreferenceUtils.saveString(Constants.USER_OBJECT,json)
                            sessionManager.saveAuthToken(loginResponse.bearer_token)


                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            RegisterationActivity.getRegActivity?.finish()
                            RegisterationActivity.getRegActivity = null

                        }


                        else {


                                Toast.makeText(requireContext(), response?.body()?.message.toString(), Toast.LENGTH_SHORT).show()

                        }
                    }
                    catch (e:Exception)
                    {

                            Toast.makeText(requireContext(),"msg:"+ e.message.toString(), Toast.LENGTH_SHORT).show()
                            Log.d("exception","msg:"+e.localizedMessage.toString())

                    }
                    })  }

                override fun onFailure(call: Call<SocialLoginResponse>, t: Throwable)
                {


                    activity?.runOnUiThread(java.lang.Runnable {
                        preLoginBinding.pb_login.visibility=View.GONE

                        Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }





    private fun googleSignIn() {
        //getting the google signin intent
        val signInIntent = mGoogleSignInClient!!.signInIntent
        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode === RC_SIGN_IN) {
            // similar condition for facebook
            //Getting the GoogleSignIn Task
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //Google Sign In was successful, authenticate with Firebase
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                //authenticating with firebase
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
       // callbackManager.onActivityResult(requestCode, resultCode, data)
    }
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        AuthWithGoogle(acct)
    }
    private fun AuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()
            ) { task ->
                if (task.isSuccessful) {
                    val mUser = FirebaseAuth.getInstance().currentUser
                    mUser!!.getIdToken(true)
                        .addOnCompleteListener(object : OnCompleteListener<GetTokenResult?> {
                            override fun onComplete(task: Task<GetTokenResult?>) {
                                val idToken: String = task.getResult()!!.getToken().toString()
                                Log.e("gdg", "Token:"+idToken)
                                Log.e("gdg", "Token:"+acct.idToken)


                                socialLogin(idToken,acct.displayName,"google")
                            }
                        })
                } else {
                    Toast.makeText(requireContext(), "" + task.exception!!.message, Toast.LENGTH_SHORT).show()
                    Log.e("exception: ",""+ task.exception!!.localizedMessage)

                }
            }
    }


    private fun initFaceBook(){

        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookAccessToken(loginResult.accessToken)
            }
            override fun onCancel() {
                Toast.makeText(requireContext(), "Please Try again", Toast.LENGTH_SHORT).show()
            }
            override fun onError(error: FacebookException) {
                Toast.makeText(requireContext(), "" +error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.e("gdg", "handleFacebookAccessToken:${token.token}")
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val mUser = FirebaseAuth.getInstance().currentUser
                    mUser!!.getIdToken(true)
                        .addOnCompleteListener(object : OnCompleteListener<GetTokenResult?> {
                            override fun onComplete(task: Task<GetTokenResult?>) {
                                val idToken: String = task.getResult()!!.getToken().toString()
                                Log.e("gdg", "Token:"+idToken)
                                socialLogin(idToken,mUser.displayName,"facebook")
                            }
                        })
                } else {

                    Toast.makeText(requireContext(), "" +task.exception!!.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }
}




