package com.fictivestudios.lakoda.views.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.adapters.BlockedListAdapter
import com.fictivestudios.lakoda.adapters.MessagesAdapter
import com.fictivestudios.lakoda.apiManager.response.*
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import kotlinx.android.synthetic.main.fragment_blocked_list.view.*
import kotlinx.android.synthetic.main.friend_profile_fragment.*
import kotlinx.android.synthetic.main.friend_profile_fragment.view.*
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
 * Use the [BlockedListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlockedListFragment : BaseFragment() , OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mView: View

    private var blockedList = ArrayList<BlockedListData>()
    private var blockedAdapter : BlockedListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack("BLOCKED USERS")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        if (!this::mView.isInitialized) {

            mView = inflater.inflate(R.layout.fragment_blocked_list, container, false)
            getBlockedList()
        }

        return mView
    }



    private fun getBlockedList()
    {

        mView.shimmer_blocked .startShimmer()
        mView.shimmer_blocked.visibility = View.VISIBLE
        mView.rv_blocked.visibility =View.GONE

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.getBlockList().enqueue(object: retrofit2.Callback<BlockedListResponse> {
                override fun onResponse(
                    call: Call<BlockedListResponse>,
                    response: Response<BlockedListResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.shimmer_blocked.stopShimmer()
                        mView.shimmer_blocked.visibility = View.GONE
                        mView.rv_blocked.visibility =View.VISIBLE
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
                            mView.shimmer_blocked.stopShimmer()
                            mView.shimmer_blocked.visibility = View.GONE
                            Toast.makeText(requireContext(), "msg: "+e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption","msg: "+e.localizedMessage)
                        })
                    }
                }

                override fun onFailure(call: Call<BlockedListResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.shimmer_blocked.stopShimmer()
                        mView.shimmer_blocked.visibility = View.GONE
                        mView.rv_blocked.visibility =View.VISIBLE
                        Toast.makeText(requireContext(), ""+t.localizedMessage, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }

    private fun setData(response: List<BlockedListData>?)
    {
        blockedList = response as ArrayList<BlockedListData>

        blockedAdapter = BlockedListAdapter(blockedList,requireContext(),this)
        mView.rv_blocked.adapter = blockedAdapter
        blockedAdapter?.notifyDataSetChanged()


    }

    private fun unblockUser(userID: Int,position: Int)
    {


        Toast.makeText(requireContext(), "Unblocking...", Toast.LENGTH_SHORT).show()

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())
        GlobalScope.launch(Dispatchers.IO)
        {



            apiClient.blockUser(userID,"unblock").enqueue(object: retrofit2.Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                )
                {


                    activity?.runOnUiThread {



                        response?.body()?.message?.let { Log.d("Response", it) }

                        try {

                            if (response?.message() == "Unauthorized"||
                                response?.body()?.message == "Unauthorized"
                                || response?.message() == "Unauthenticated.")
                            {
                                PreferenceUtils.remove(Constants.USER_OBJECT)
                                PreferenceUtils.remove(Constants.ACCESS_TOKEN)


                                activity?.runOnUiThread(java.lang.Runnable {
                                    startActivity(Intent(requireContext(), RegisterationActivity::class.java))
                                    MainActivity.getMainActivity?.finish()
                                    MainActivity.getMainActivity=null
                                    Toast.makeText(requireContext(), "Login expired please login again", Toast.LENGTH_SHORT).show()
                                })
                            }

                            if (response.body()?.status==1)
                            {

                                Toast.makeText(requireContext(), "" + response?.body()?.message, Toast.LENGTH_SHORT).show()

                                blockedList.removeAt(position)
                                blockedAdapter?.notifyItemChanged(position)
                                blockedAdapter?.notifyDataSetChanged()



                            }
                            else
                            {

                                Toast.makeText(requireContext(), "" + response?.body()?.message, Toast.LENGTH_SHORT).show()

                            }


                        }
                        catch (e:Exception)
                        {
                            //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()

                            //mView.pb_pofile.visibility=View.GONE

                            Toast.makeText(requireContext(), "msg: "+e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption","msg: "+e.localizedMessage)


                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        //mView.pb_pofile.visibility=View.GONE

                        Toast.makeText(requireContext(), ""+t.localizedMessage, Toast.LENGTH_SHORT).show()

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
         * @return A new instance of fragment BlockedListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlockedListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClick(position: Int, view: View, value: String) {

        unblockUser(blockedList[position].blocked_user_id,position)

    }
}