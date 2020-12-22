package com.example.photoweather.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoweather.R
import com.example.photoweather.adapter.WeatherAdapter
import com.example.photoweather.databinding.FragmentHomeBinding
import com.example.photoweather.db.Weather
import com.example.photoweather.other.Constants.REQUESt_Code_LOCATION_PERMISSION
import com.example.photoweather.other.LocationUtility
import com.example.photoweather.other.WriteImage
import com.example.photoweather.ui.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    lateinit var weatherAdapter: WeatherAdapter
    private val viewModels: WeatherViewModel by viewModels()

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        subscribObserver()
        setupRecyclerView()




            binding.fabAddPhotot.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_weatherDetailsFragment)

        }

        weatherAdapter.setOnItemClickListener { bitmap, view ->
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, getBitmapFromView(bitmap))
            startActivity(Intent.createChooser(intent, "Share Image"))
        }
    }

    private fun subscribObserver() {
        viewModels.WeatherHistory.observe(viewLifecycleOwner, {
            weatherAdapter.list = it
        })
    }

    private val itemTouchCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val item = weatherAdapter.list[pos]
                viewModels.deleteWeather(item)
                Snackbar.make(requireView(), "Succeeful delete item", Snackbar.LENGTH_LONG).apply {
                    setAction("undo") {
                        viewModels.inserWeather(item)
                    }
                    show()
                }
            }
        }



    fun getBitmapFromView(bmp: Bitmap?): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(requireContext().externalCacheDir, System.currentTimeMillis().toString() + ".jpg")

            val out = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()
            bmpUri = Uri.fromFile(file)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }
    
fun setupRecyclerView ( ){
    weatherAdapter = WeatherAdapter()
    binding.rvHistoryWeather.apply {
        adapter = weatherAdapter
        layoutManager = LinearLayoutManager(requireContext())
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
    }
}

}