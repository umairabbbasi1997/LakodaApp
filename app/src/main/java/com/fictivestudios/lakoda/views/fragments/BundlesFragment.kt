package com.fictivestudios.lakoda.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.*
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
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
class BundlesFragment : BaseFragment()   {
    private var purchasetoken: String?=null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mView: View


    private var skuDetails:SkuDetails? =null

    var membershipAdapter: BundleAdapter? = null
    var currentItem:Int?= 0

    private var bundleList: ArrayList<SkuDetails>? = ArrayList<SkuDetails>()

    private var purchasesUpdatedListener : PurchasesUpdatedListener?=null


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

            setUpBilling()





           // getBlockedList()


        }

        return mView
    }

    private fun setUpBilling() {
        requireActivity().runOnUiThread {
           purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
                // To be implemented in a later section.

                if (purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(purchase)


               /*         billingClient?.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,object :PurchaseHistoryResponseListener{
                            override fun onPurchaseHistoryResponse(
                                billingResult: BillingResult,
                                purchaseHistory: MutableList<PurchaseHistoryRecord>?
                            ) {
                                Log.d("billing response",purchaseHistory.toString())

                                var bundleType:String


                            }
                        })*/
                    }
                }


            }
            billingClient = purchasesUpdatedListener?.let {
                BillingClient.newBuilder(requireContext())
                    .setListener(it)
                    .enablePendingPurchases()
                    .build()
            }


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

                Log.d("billing_id",bundleList?.get(currentItem!!)?.sku.toString())
              //  Toast.makeText(requireContext(), bundleList?.get(currentItem!!)?.sku.toString(), Toast.LENGTH_SHORT).show()

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


    private fun queryAvaliableProducts() {
        Log.d("billing","queryAvaliableProducts")
        val skuList = ArrayList<String>()

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

                    mView.btnAccept.setOnClickListener {

                        Log.d("billing",currentItem.toString())
                        currentItem?.let {
                                it1 -> skuDetailsList.get(it1) }
                            ?.let { it2 -> launchBillingFlow(it2) }
                    }
                   /* for (skuDetails in skuDetailsList) {
                        Log.v("TAG_INAPP","skuDetailsList : ${skuDetailsList}")

                    }*/


                }
            }


        }
    }


    private  fun launchBillingFlow(skuDetails:SkuDetails)
    {

        if (currentItem!=null)
        {
            skuDetails?.let {
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(it)
                    .build()
                billingClient?.launchBillingFlow(requireActivity(), billingFlowParams)?.responseCode


           }?:"failed"

            this.skuDetails = skuDetails

        }


    }



    fun handlePurchase(purchase: Purchase) {
        // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.
        val purchase: Purchase = purchase
        purchasetoken = purchase.purchaseToken

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        //consumeable
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build()
        val consumeResult = GlobalScope.launch(Dispatchers.IO) {
            billingClient?.consumePurchase(consumeParams)

            if (skuDetails!=null && purchasetoken!=null )
            {
                val string =skuDetails!!.description.substring(0,9);
                var limit   = string.filter { it.isDigit() }
                if (skuDetails!!.sku.contains("photo"))
                {

                    buyBundle(skuDetails!!.sku.toString(), purchasetoken!!,"google",null,limit.toInt())
                }
                else
                {
                    buyBundle(skuDetails!!.sku.toString(), purchasetoken!!,"google",limit.toInt(),null)
                }

            }
        }





    }

    private fun buyBundle(bundleId: String, receipt:String, source:String, videoLimit:Int?, postLimit:Int?)
    {

       // mView.pb_bundle.visibility = View.VISIBLE

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

        GlobalScope.launch(Dispatchers.IO)
        {


            apiClient.buyBundle(bundleId, receipt,source,videoLimit, postLimit).enqueue(object: retrofit2.Callback<BuyBundleResponse> {
                override fun onResponse(
                    call: Call<BuyBundleResponse>,
                    response: Response<BuyBundleResponse>
                ) {

                    try {

                        activity?.runOnUiThread(java.lang.Runnable {

                         //   mView.pb_bundle.visibility = View.GONE

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


                  //      mView.pb_bundle.visibility = View.GONE

                    })
                }
            })


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








         //none-consumeable
   /*      if (purchase.purchaseState === Purchase.PurchaseState.PURCHASED) {
             if (!purchase.isAcknowledged) {
                 val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                     .setPurchaseToken(purchase.purchaseToken)
                 val ackPurchaseResult =  GlobalScope.launch (Dispatchers.IO) {
                     billingClient?.acknowledgePurchase(acknowledgePurchaseParams.build())
                 }
             }
         }*/

    }
