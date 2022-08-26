package com.fictivestudios.lakoda.views.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.map_fragment.view.*


class MapFragment : BaseFragment() ,OnMapReadyCallback {

    companion object {
        fun newInstance() = MapFragment()
    }

    private var myLocation:LatLng?= null

    private lateinit var mView: View
    var map: GoogleMap? = null
    private lateinit var viewModel: MapViewModel
    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack("MAP")
    }

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

        mView.btn_send.setOnClickListener {

            MainActivity?.getMainActivity?.onBackPressed()
        }

        return mView
    }

    fun checkPermssions()
    {
        PermissionX.init(requireActivity())
            .permissions(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {


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

       // centerMapOnMyLocation()
    }


    private fun centerMapOnMyLocation() {
        map!!.isMyLocationEnabled = true
       var location = map!!.myLocation
        if (location != null) {
            myLocation = LatLng(
                location.getLatitude(),
                location.getLongitude()
            )
        }
        map!!.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                myLocation!!,
                14f
            )
        )
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
}

