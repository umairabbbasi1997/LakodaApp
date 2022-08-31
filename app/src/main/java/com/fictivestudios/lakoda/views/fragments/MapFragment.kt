package com.fictivestudios.lakoda.views.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.map_fragment.view.*


class MapFragment : BaseFragment() ,OnMapReadyCallback {

    companion object {
        fun newInstance() = MapFragment()

    }

    private var mLocationPermissionGranted: Boolean = false
    private var myLocation:LatLng?= null
    private var locationInterface :LocationInterface? = null
    private var isLocationLoaded = false

    private  var fusedLocationClient: FusedLocationProviderClient? = null

    private lateinit var mView: View
    var map: GoogleMap? = null
    private lateinit var viewModel: MapViewModel
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
        mView.mapView.onResume()

        mView.mapView.getMapAsync(this)



        try {
            MapsInitializer.initialize(requireContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }







        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
/*
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient?.getCurrentLocation(PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

            override fun isCancellationRequested() = false
        })
            ?.addOnSuccessListener { location: Location? ->
                if (location == null)
                    Toast.makeText(requireContext(), "Cannot get location.", Toast.LENGTH_SHORT).show()
                else {

                    myLocation = LatLng(
                        location.getLatitude(),
                        location.getLongitude()
                    )
                }

            }
*/

        updateLocationUI()




        mView.btn_send.setOnClickListener {


            if (isLocationLoaded)
            {

                val center: LatLng = map!!.getCameraPosition().target

                var mapLink ="https://www.google.com/maps/search/?api=1&query="+ center?.latitude+","+ center?.longitude


                com.fictivestudios.lakoda.liveData.LiveData.setGetMapLink(mapLink)


                MainActivity?.getMainActivity?.onBackPressed()
            }
            else{
                Toast.makeText(requireContext(), "please wait while map loading...", Toast.LENGTH_SHORT).show()
            }

        }

        return mView
    }

    fun checkPermssions()
    {
        PermissionX.init(requireActivity())
            .permissions(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {

                    mLocationPermissionGranted = true

                } else {
                    Toast.makeText(requireContext(), "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        // TODO: Use the ViewModel
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap;
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
        map!!.setMyLocationEnabled(true);


        map!!.uiSettings.isMyLocationButtonEnabled = true

        map!!.setOnMyLocationButtonClickListener {

            isLocationLoaded = true
            false
        }


    }

    @SuppressLint("MissingPermission")
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
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (mLocationPermissionGranted) {
                map!!.setMyLocationEnabled(true)
                map!!.getUiSettings().setMyLocationButtonEnabled(true)
                getDeviceLocation()
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
}

