package com.fictivestudios.lakoda.liveData

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng

class LiveData {

    companion object {
        private var mapLink: MutableLiveData<LatLng> = MutableLiveData()

        public fun setGetMapLink(location: LatLng): MutableLiveData<LatLng> {
            mapLink.value = location
            return mapLink
        }

        public fun getGetMapLink(): MutableLiveData<LatLng> {
            if (mapLink == null) {
                mapLink = MutableLiveData()
            }
            return mapLink
        }
    }
}