package com.fictivestudios.lakoda.liveData

import androidx.lifecycle.MutableLiveData

class LiveData {

    companion object {
        private var mapLink: MutableLiveData<String> = MutableLiveData()

        public fun setGetMapLink(name: String): MutableLiveData<String> {
            mapLink.value = name
            return mapLink
        }

        public fun getGetMapLink(): MutableLiveData<String> {
            if (mapLink == null) {
                mapLink = MutableLiveData()
            }
            return mapLink
        }
    }
}