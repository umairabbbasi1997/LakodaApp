package com.fictivestudios.lakoda.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.*
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.adapters.BundleAdapter
import com.fictivestudios.lakoda.apiManager.response.*
import com.fictivestudios.lakoda.utils.CarouselMembership
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import kotlinx.android.synthetic.main.fragment_bundles_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BundlesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BundlesFragment : BaseFragment() , OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mView: View



    var membershipAdapter: BundleAdapter? = null


    private var bundleList: ArrayList<BundleData>? = ArrayList<BundleData>()

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
        }

    private var billingClient : BillingClient?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack("BUNDLES")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        if (!this::mView.isInitialized) {

            mView = inflater.inflate(R.layout.fragment_bundles_list, container, false)


            getBundleList()


            billingClient = BillingClient.newBuilder(requireContext())
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build()


            billingClient?.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                        // The BillingClient is ready. You can query purchases here.
                        queryAvaliableProducts()
                    }
                }
                override fun onBillingServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    Log.d("billing","billing disconnected")
                }
            })

           // getBlockedList()


        }

        return mView
    }

    private fun setData(response: List<BundleData>?) {





        bundleList = response as ArrayList<BundleData>?

        mView?.viewpager?.setClipChildren(false)
        mView?.viewpager?.setOffscreenPageLimit(bundleList!!.size)
        mView?.viewpager?.setPageTransformer(false, CarouselMembership(activity))
        membershipAdapter = BundleAdapter(requireActivity(), bundleList!!)
        mView?.viewpager?.setAdapter(membershipAdapter)
        mView?.viewpager?.currentItem = 1
        membershipAdapter!!.notifyDataSetChanged()
        mView!!.viewpager.adapter = membershipAdapter
       mView!!.indicator.setViewPager(mView!!.viewpager)
    }

    private fun getBundleList()
    {
        mView.pb_bundle.visibility = View.VISIBLE

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.bundleList().enqueue(object: retrofit2.Callback<BundleResponse> {
                override fun onResponse(
                    call: Call<BundleResponse>,
                    response: Response<BundleResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {

                        mView.pb_bundle.visibility = View.GONE

                    })

                    Log.d("Response", ""+response?.body()?.message)

                    try {


                        if (response.isSuccessful) {


                            if (response?.message() == "Unauthorized"||
                                response?.body()?.message == "Unauthorized"
                                || response?.message() == "Unauthenticated.")
                            {
                                PreferenceUtils.remove(Constants.USER_OBJECT)
                                PreferenceUtils.remove(Constants.ACCESS_TOKEN)
                                MainActivity.getMainActivity?.finish()
                                MainActivity.getMainActivity=null
                                startActivity(Intent(requireContext(), RegisterationActivity::class.java))
                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(requireContext(), "Login expired please login again", Toast.LENGTH_SHORT).show()
                                })
                            }

                            if (response.body()?.status==1)
                            {

                                if (response.body()?.data != null)
                                {
                                    Log.d("data", ""+response.body()?.data.toString())
                                    var response = response.body()?.data



                                    setData(response)

                                  //  setClick(skuList)
                                }

                            }
                            else
                            {
                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(requireContext(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                                })
                            }

                        }
                        else {
                            activity?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(requireContext(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                            })

                        }
                    }
                    catch (e:Exception)
                    {
                        activity?.runOnUiThread(java.lang.Runnable {
                            mView.pb_bundle.visibility = View.GONE
                            Toast.makeText(requireContext(), "msg: "+e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption","msg: "+e.localizedMessage)
                        })
                    }
                }

                override fun onFailure(call: Call<BundleResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {

                        mView.pb_bundle.visibility = View.GONE

                        Toast.makeText(requireContext(), ""+t.localizedMessage, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }

    private fun setClick(skuDetails: SkuDetails) {

        var currentItem:Int?=null
        mView.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                currentItem = position

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        mView.btnAccept.setOnClickListener {

            if (currentItem!=null)
            {
                skuDetails?.let {
                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(it)
                        .build()
                    billingClient?.launchBillingFlow(requireActivity(), billingFlowParams)?.responseCode
                }?:"failed"
                buyBundle(bundleList?.get(currentItem!!)!!.id,"abc","android",null,null)
            }

        }
    }

    private fun buyBundle(bundleId: Int,receipt:String,source:String,videoId:Int?,postId:Int?)
    {

        mView.pb_bundle.visibility = View.VISIBLE

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

        GlobalScope.launch(Dispatchers.IO)
        {


                apiClient.buyBundle(bundleId, receipt,source,videoId, postId).enqueue(object: retrofit2.Callback<BuyBundleResponse> {
                    override fun onResponse(
                        call: Call<BuyBundleResponse>,
                        response: Response<BuyBundleResponse>
                    ) {

                        try {

                            activity?.runOnUiThread(java.lang.Runnable {

                                mView.pb_bundle.visibility = View.GONE

                            })

                            val response: BuyBundleResponse? =response.body()
                            val statuscode= response?.status
                            if (statuscode==1) {

                                Log.d("response", response.message)

                                Toast.makeText(requireContext(), " "+response?.message, Toast.LENGTH_SHORT).show()


                            } else {


                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(requireContext(), "msg: "+response?.message, Toast.LENGTH_SHORT).show()
                                    response?.message?.let { Log.d("response", it) }

                                })
                            }
                        } catch (e:Exception) {

                            Log.d("response", "msg "+ e.localizedMessage)
                            activity?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(requireContext(), "msg "+ e.localizedMessage, Toast.LENGTH_SHORT).show()
                            })
                        }
                    }

                    override fun onFailure(call: Call<BuyBundleResponse>, t: Throwable) {

                        activity?.runOnUiThread(java.lang.Runnable {
                            Toast.makeText(requireContext(), "msg "+  t.localizedMessage, Toast.LENGTH_SHORT).show()
                            Log.d("response", "msg "+  t.localizedMessage)


                            mView.pb_bundle.visibility = View.GONE

                        })
                    }
                })


        }


    }

    private fun queryAvaliableProducts() {
        val skuList = ArrayList<String>()
        skuList.add("test.sample")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        billingClient?.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            // Process the result.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetailsList.isNullOrEmpty()) {
                for (skuDetails in skuDetailsList) {
                    Log.v("TAG_INAPP","skuDetailsList : ${skuDetailsList}")
                    //This list should contain the products added above
                 //  updateUI(skuDetails)
                    setClick(skuDetails)
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BundlesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BundlesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClick(position: Int, view: View, value: String) {

       // unblockUser(blockedList[position].blocked_user_id,position)

    }
}