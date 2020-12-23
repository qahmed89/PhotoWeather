package com.example.photoweather.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.designapp.other.Status
import com.example.photoweather.R
import com.example.photoweather.databinding.FragmentAddPhotoWeatherBinding
import com.example.photoweather.db.Weather
import com.example.photoweather.other.Constants
import com.example.photoweather.other.Constants.API_KEY
import com.example.photoweather.other.Constants.REQUEST_IMAGE_CAPTURE
import com.example.photoweather.other.LocationUtility
import com.example.photoweather.other.WriteImage
import com.example.photoweather.remote.model.WeatherResponse
import com.example.photoweather.ui.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class AddPhotoWeatherFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentAddPhotoWeatherBinding? = null
    private val binding get() = _binding!!
    private val viewModels: WeatherViewModel by viewModels()
    var lat: Double? = null
    var lon: Double? = null
    var bitmap: Bitmap? = null
    lateinit var weatherItem: Weather

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddPhotoWeatherBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermission()
        subscribObserver()
        binding.fbCamera.setOnClickListener {
            if (isGPSEnabled()) {
                location(fusedLocationProviderClient)
                dispatchTakePictureIntent()
            } else {
                Snackbar.make(requireView(), " Please  enable GPS", Snackbar.LENGTH_LONG).show()
            }
        }
        binding.bRetry.setOnClickListener {
            location(fusedLocationProviderClient)
            viewModels.getDataWeather(lat.toString(), lon.toString(), API_KEY)
        }
        binding.button.setOnClickListener {
            if (weatherItem != null) {
                viewModels.inserWeather(weatherItem)
                findNavController().popBackStack(R.id.homeFragment, false)
            } else {
                Toast.makeText(
                        requireContext(),
                        " Check Your InterNet or  Enable Location",
                        Toast.LENGTH_LONG
                ).show()

            }

        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return gpsStatus
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            bitmap = data?.extras?.get("data") as Bitmap
            _binding?.imageView?.setImageBitmap(bitmap)
            viewModels.getDataWeather(lat.toString(), lon.toString(), API_KEY)


        }
    }

    fun subscribObserver() {
        viewModels.dataWeather.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled().let { result ->
                when (result?.status) {
                    Status.SUCCESS -> {
                        setDataIfSuccess(result.data!!)

                        bitmap = WriteImage.writeOnImage(
                                requireContext(), bitmap!!, "Address is ".plus(result.data.name),
                                "Temperature is ".plus(result.data.main.temp).plus("°C"),
                                "Condition is ".plus(result.data.weather[0].main)
                        )
                        weatherItem = Weather(
                                bitmap,
                                result.data.name,
                                result.data.main.temp.toString(),
                                result.data.weather[0].main
                        )

                    }
                    Status.ERROR -> {
                        setDataIfError(result.message!!)
                    }
                    Status.LOADING -> {
                        _binding?.pbWeather?.visibility = View.VISIBLE
                    }
                }

            }
        })
    }

    @SuppressLint("MissingPermission")
    fun location(fusedLocationProviderClient: FusedLocationProviderClient) {


        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            // Got last known location. In some rare situations this can be null.
            lat = location?.latitude
            lon = location?.longitude

        }


    }

    fun setDataIfSuccess(weatherResponse: WeatherResponse) {
        binding.apply {
            tvState.isVisible = false
            pbWeather.isVisible = false
            tvCon.text = weatherResponse.weather.get(0).description
            tvCity.text = weatherResponse.name
            tvTemp.text = weatherResponse.main.temp.toString()
            tvCon.isVisible = true
            tvCity.isVisible = true
            tvTemp.isVisible = true
            bRetry.isVisible = false
        }

    }

    fun setDataIfError(msg: String) {
        binding.apply {
            tvState.isVisible = true
            tvState.text = msg.plus(" Try Again")
            pbWeather.isVisible = false
            tvCon.isVisible = false
            tvCity.isVisible = false
            tvTemp.isVisible = false
            bRetry.isVisible = true
        }


    }

    private fun requestPermission() {
        if (LocationUtility.hasLocationPermission(requireContext())) {
            location(fusedLocationProviderClient)
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                    this,
                    "you need  to accept location permissions to use the app.",
                    Constants.REQUESt_Code_LOCATION_PERMISSION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION

            )
            location(fusedLocationProviderClient)

        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "you need  to accept location permissions to use the app.",
                    Constants.REQUESt_Code_LOCATION_PERMISSION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            location(fusedLocationProviderClient)

        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()

        } else {
            requestPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        location(fusedLocationProviderClient)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults,
                this
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}