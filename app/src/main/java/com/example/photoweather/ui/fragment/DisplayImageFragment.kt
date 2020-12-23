package com.example.photoweather.ui.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs


import com.example.photoweather.R
import com.example.photoweather.databinding.FragmentDisplayImageBinding
import com.example.photoweather.ui.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class DisplayImageFragment : Fragment(R.layout.fragment_display_image) {
    val args: DisplayImageFragmentArgs by navArgs()
    private var _binding: FragmentDisplayImageBinding? = null
    private val binding get() = _binding!!
    var id: Int? = null
    var bitmap: Bitmap? = null
    private val viewModels: WeatherViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDisplayImageBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
                android.R.transition.explode
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
        id = args.id
        viewModels.getImageById(id!!).observe(viewLifecycleOwner, {

            binding.ivBigImage.setImageBitmap(it)
            bitmap = it

        })
        binding.fbShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, getBitmapFromView(bitmap!!))
            startActivity(Intent.createChooser(intent, "Share Image"))
        }

    }

    fun getBitmapFromView(bmp: Bitmap?): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(
                    requireContext().externalCacheDir,
                    System.currentTimeMillis().toString() + ".jpg"
            )

            val out = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()
            bmpUri = Uri.fromFile(file)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}