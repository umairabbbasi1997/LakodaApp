package com.fictivestudios.lakoda.views.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.BuildConfig.MAPS_API_KEY
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.fragments.ChatFragment.Companion.isLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.map_fragment.view.*
import java.util.*


class MapFragment : BaseFragment() ,OnMapReadyCallback {

    companion object {
        fun newInstance() = MapFragment()

    }

    private var mLocationPermissionGranted: Boolean = false
    private var mCurrentLocation: LatLng? = null


    private var myLocation:LatLng?= null
    private var locationInterface :LocationInterface? = null
    //private var isLocationLoaded = false

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private lateinit var mView: View
    var map: GoogleMap? = null
    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack("MAP")
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.map_fragment, container, false)

        checkPermssions()
        mView.mapView.onCreate(savedInstanceState);
        mView.mapView.getMapAsync(this)
        mView.mapView.onResume()


        if (!Places.isInitialized()) {
            Places.initialize(requireContext(),
                "${MAPS_API_KEY}", Locale.US);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        try {
            MapsInitializer.initialize(requireContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mView.search_bar.setOnClickListener {


            getPlacesSearch()

        }

        mView.tv_search.setOnClickListener {
            getPlacesSearch()
        }


        mView.btn_current_loc.setOnClickListener {
            getCurrentLocation()
        }





        updateLocationUI()




        mView.btn_send.setOnClickListener {


            if (mCurrentLocation != null)
            {

               val location = LatLng(mCurrentLocation!!.latitude,mCurrentLocation!!.longitude)

            //    var mapLink ="https://www.google.com/maps/search/?api=1&query="+ center?.latitude+","+ center?.longitude


                isLocation = true
                com.fictivestudios.lakoda.liveData.LiveData.setGetMapLink(location)


                MainActivity?.getMainActivity?.onBackPressed()
            }
            else{
                Toast.makeText(requireContext(), "please wait while map loading...", Toast.LENGTH_SHORT).show()
            }

        }

        return mView
    }

    private fun getPlacesSearch() {
        val fields: List<Place.Field> = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        val intent: Intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields
        ).setCountries(listOf("USA", "PAK"))
            .build(requireContext())
        startActivityForResult(intent, 99)

    }

    fun checkPermssions()
    {
        PermissionX.init(requireActivity())
            .permissions(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {

                    mLocationPermissionGranted = true
                    checkGpsEnabled()

                } else {
                    Toast.makeText(requireContext(), "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {




        map = googleMap;
        getCurrentLocation()
        map!!.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        map!!.setMyLocationEnabled(false);


        map!!.uiSettings.isMyLocationButtonEnabled = false

        map!!.setOnMyLocationButtonClickListener {

          //  isLocationLoaded = true
            false
        }



    }

/*    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {

                val locationResult: Task<Location> = fusedLocationClient?.getLastLocation()!!
                locationResult.addOnCompleteListener(object : OnCompleteListener<Location?> {


                    override fun onComplete(task: Task<Location?>) {
                        if (task.isSuccessful) {
                            // Set the map's camera position to the current location of the device.
                            val location = task.result
                             myLocation = LatLng(
                                location!!.latitude,
                                location!!.longitude
                            )
                            val update = CameraUpdateFactory.newLatLngZoom(
                                myLocation!!,
                                20f
                            )
                            map!!.moveCamera(update)
                        }
                    }
                })

        } catch (e: SecurityException) {
            e.message?.let { Log.e("Exception: %s", it) }
        }
    }*/

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (mLocationPermissionGranted) {
                map!!.setMyLocationEnabled(true)
                map!!.getUiSettings().setMyLocationButtonEnabled(false)
                getCurrentLocation()
            } else {
                map!!.setMyLocationEnabled(false)
                map!!.getUiSettings().setMyLocationButtonEnabled(false)
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message!!)
        }
    }



    override fun onResume() {
        super.onResume()
        mView.mapView.onResume()
    }
override fun onPause() {
        super.onPause();
          mView.mapView.onPause();
    }

override fun onDestroy() {
        super.onDestroy();
          mView.mapView.onDestroy();
    }

override fun onLowMemory() {
        super.onLowMemory();
          mView.mapView.onLowMemory();
    }


    interface LocationInterface {

        fun onLocationSend(value:String)
    }


    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        checkGpsEnabled()
        fusedLocationClient?.lastLocation
            ?.addOnSuccessListener { location ->
                if (location != null) {

                    mCurrentLocation = LatLng(location.latitude,location.longitude)

                    // Set the map's camera position to the current location of the device.
                    map?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                location.latitude,
                                location.longitude
                            ), 20f
                        )
                    )

                    map?.isMyLocationEnabled = true
                    map?.uiSettings?.isMyLocationButtonEnabled = false
                }
               // getAddress(location.latitude, location.longitude)

            }


    }

    @SuppressLint("LongLogTag")
    fun getAddress(latitude: Double, longitude: Double): String {
        var strAdd = ""
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.w("My Current location address", strReturnedAddress.toString())
            } else {
                Log.w("My Current location address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("My Current location address", "Can not get Address!")
        }

     //   tv_pickup.text = strAdd
        return strAdd
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 99) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)

                    val geoCoder = Geocoder(requireContext(), Locale.getDefault())

                    if (place.address != null && place.latLng != null)
                        mView.tv_search.setText(place.name)

                    mCurrentLocation = LatLng(place?.latLng?.latitude!!,place?.latLng?.longitude!!)

                    map?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                mCurrentLocation!!.latitude,
                                mCurrentLocation!!.longitude
                            ), 20f
                        )
                    )


                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Toast.makeText(requireContext(), "Error: " + status.statusMessage, Toast.LENGTH_SHORT)
                        .show()
                }
                Activity.RESULT_CANCELED -> {
                    // The myBookingUser canceled the operation.
                }
            }
        }
    }

    private fun checkGpsEnabled()
    {
        val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: java.lang.Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: java.lang.Exception) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder(context)
                .setMessage("Location is not enabled")
                .setPositiveButton("Turn On Gps",
                    DialogInterface.OnClickListener { paramDialogInterface, paramInt ->
                        requireContext().startActivity(
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        )
                    })
                .setNegativeButton("cancel", null)
                .show()
        }
    }

}

