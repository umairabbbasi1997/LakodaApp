package com.fictivestudios.lakoda.views.fragments

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
import com.fictivestudios.lakoda.utils.Titlebar
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
    private var purchasetoken: String?=null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mView: View



    var membershipAdapter: BundleAdapter? = null
    var currentItem:Int?=null

    private var bundleList: ArrayList<SkuDetails>? = ArrayList<SkuDetails>()

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


         //   getBundleList()

            requireActivity().runOnUiThread {

                billingClient = BillingClient.newBuilder(requireContext())
                    .setListener(purchasesUpdatedListener)
                    .enablePendingPurchases()
                    .build()


                billingClient?.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                            Log.d("billing","billing ok")
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
            }



           // getBlockedList()


        }

        return mView
    }

    private fun setData(response: MutableList<SkuDetails>?) {





        bundleList = response as ArrayList<SkuDetails>

        mView?.viewpager?.setClipChildren(false)
        mView?.viewpager?.setOffscreenPageLimit(bundleList!!.size)
        mView?.viewpager?.setPageTransformer(false, CarouselMembership(activity))
        membershipAdapter = BundleAdapter(requireActivity(), bundleList!!)
        mView?.viewpager?.setAdapter(membershipAdapter)
        mView?.viewpager?.currentItem = 1

        mView!!.viewpager.adapter = membershipAdapter
       mView!!.indicator.setViewPager(mView!!.viewpager)
        membershipAdapter!!.notifyDataSetChanged()

        changeListener()
    }

  /*  private fun getBundleList()
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


    }*/

    private fun changeListener()
    {


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
/*        mView.btnAccept.setOnClickListener {

            if (currentItem!=null)
            {
                skuDetails?.let {
                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(it)
                        .build()
                    billingClient?.launchBillingFlow(requireActivity(), billingFlowParams)?.responseCode
                }?:"failed"

                buyBundle(*//*bundleList?.get(currentItem!!)!!.id*//*1,"abc","android",null,null)
            }

        }*/
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
        Log.d("billing","queryAvaliableProducts")
        val skuList = ArrayList<String>()


//        skuList.add("com.andriod.test")

     /*   skuList.add("com.fictivestudios.lakoda_photo_sb1")
        skuList.add("com.fictivestudios.lakoda_photo_sb1")
        skuList.add("com.fictivestudios.lakoda_photo_sb2")
        skuList.add("com.fictivestudios.lakoda_photo_sb3")
        skuList.add("com.fictivestudios.lakoda_photo_sb4")
        skuList.add("com.fictivestudios.lakoda_photo_sb5")

        skuList.add("com.fictivestudios.lakoda_photopost_sb1")
        skuList.add("com.fictivestudios.lakoda_photopost_sb2")
        skuList.add("com.fictivestudios.lakoda_photopost_sb3")

        skuList.add("com.fictivestudios.lakoda_video_sb1")
        skuList.add("com.fictivestudios.lakoda_video_sb2")
        skuList.add("com.fictivestudios.lakoda_video_sb3")
        skuList.add("com.fictivestudios.lakoda_video_sb4")
        skuList.add("com.fictivestudios.lakoda_video_sb5")
        skuList.add("com.fictivestudios.lakoda_video_sb6")
        skuList.add("com.fictivestudios.lakoda_video_sb7")
        skuList.add("com.fictivestudios.lakoda_video_sb8")*/


        skuList.add("photo_bundle_1")
        skuList.add("photo_bundle_2")
        skuList.add("photo_bundle_3")

        skuList.add("video_bundle_1")
        skuList.add("video_bundle_2")
        skuList.add("video_bundle_3")


        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        billingClient?.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            // Process the result.


            requireActivity().runOnUiThread {

                Log.d("billing",billingResult.responseCode.toString())
                Log.d("billing","skuList:"+skuDetailsList.toString())
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetailsList.isNullOrEmpty()) {

                    setData(skuDetailsList)
                    for (skuDetails in skuDetailsList) {
                        Log.v("TAG_INAPP","skuDetailsList : ${skuDetailsList}")




                        //This list should contain the products added above
                        //  updateUI(skuDetails)

                        /* skuDetails?.let {
                             val billingFlowParams = BillingFlowParams.newBuilder()
                                 .setSkuDetails(it)
                                 .build()
                             billingClient?.launchBillingFlow(requireActivity(), billingFlowParams)?.responseCode
                         }?:"failed"*/
                    }

                    mView.btnAccept.setOnClickListener {

                        Log.d("billing",currentItem.toString())
                        currentItem?.let {
                                it1 -> skuDetailsList.get(it1) }
                            ?.let { it2 -> launchBillingFlow(it2) }
                    }
                }
            }


        }
    }


    private fun launchBillingFlow(skuDetails:SkuDetails)
    {

        if (currentItem!=null)
        {
            skuDetails?.let {
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(it)
                    .build()
                billingClient?.launchBillingFlow(requireActivity(), billingFlowParams)?.responseCode
            }?:"failed"

            buyBundle(/*bundleList?.get(currentItem!!)!!.sku.toString()*/1 ,"abc","android",null,null)
        }
    }



/*    private fun billingsetup() {
        billingClient = BillingClient.newBuilder(requireActivity()).setListener(object :
            PurchasesUpdatedListener {

            override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
                // i//f (p1 != null)
                if (p0.responseCode == BillingClient.BillingResponseCode.OK && p1 != null) {

                    for (purchase in p1!!) {
                        Log.d("TAG", "onPurchasesUpdated: ")

                        if (!purchase.isAcknowledged) {
                            purchasetoken = purchase.purchaseToken
                            val consumeParams = ConsumeParams
                                .newBuilder()
                                .setPurchaseToken(purchase!!.purchaseToken!!)
                                .build()
                            billingClient?.consumeAsync(consumeParams,
                                ConsumeResponseListener { billingResult, s ->
                                    if (billingResult.responseCode === BillingClient.BillingResponseCode.OK) {
                                        Log.i("TAG", "onPurchasesUpdated: ")
                                    }
                                })


              *//*              if(HomeActivity.home != null){

                                if(coin_status!!){
                                    coin_status = false
                                    HomeActivity.home.callapi(purchasetoken,"", getToken(),
                                        getCurrentUser()!!.userId.toString(),pacakagenameinapp)
                                }

                            }*//*
                           // break

//                            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
//                                .setPurchaseToken(purchase.purchaseToken)
////                                .setDeveloperPayload(purchase.developerPayload)
//                                .build()
//                            mBillingClient!!.acknowledgePurchase(acknowledgePurchaseParams, AcknowledgePurchaseResponseListener { billingResult ->
//                                if (billingResult.responseCode === BillingClient.BillingResponseCode.OK) {
//                                    Toast.makeText(getHomeActivity(), "Purchase Acknowledged", Toast.LENGTH_SHORT).show()
//                                }
                            //                  })
                        }

                    }






                    //When every a new purchase is made
                } else if (p0.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                } else {
                    // Handle any other error codes.
                }

            }
        }) .enablePendingPurchases().build()



        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(p0: BillingResult) {
                Log.d("TAG", "onBillingSetupFinished: ")
                if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("TAG", "onBillingSetupFinished: ")
                   // loadproduct()
//                    if (mainActivity!!.preferenceManager!!.getfaulttype() == "user") {
//                        loadsubscription()
//                    } else {
//                        loadproduct()
//                    }
                    // The billing client is ready. You can query purchases here.
                }

            }

            override fun onBillingServiceDisconnected() {
                Log.d("TAG", "onBillingSetupFinished: ")

                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }*/



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