package com.fictivestudios.lakoda.adapters

import androidx.viewpager.widget.PagerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.android.billingclient.api.SkuDetails
import com.fictivestudios.lakoda.R
import java.util.*
import kotlin.collections.ArrayList

class BundleAdapter(private val mContext: Context, val feedsModels: ArrayList<SkuDetails>) :
    PagerAdapter() {
        var title: AppCompatTextView? = null
        var desc: AppCompatTextView? = null
        var price: AppCompatTextView? = null

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val model = feedsModels[position]
            val inflater = LayoutInflater.from(mContext)
            val layout = inflater.inflate(R.layout.item_bundle, collection, false) as ViewGroup
            title = layout.findViewById(R.id.package_name)
            desc = layout.findViewById(R.id.package_des)
            price = layout.findViewById(R.id.package_price)
            title!!.text = model.title
            desc!!.text = model.description
            price!!.text = model.price .toString()
            collection.addView(layout)
            return layout

        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)

        }

        override fun getCount(): Int {
            return feedsModels.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        @SuppressLint("ResourceType")
        override fun getPageTitle(position: Int): CharSequence {
            val customPagerEnum = feedsModels[position]

            return mContext.getString(1)
        }
}