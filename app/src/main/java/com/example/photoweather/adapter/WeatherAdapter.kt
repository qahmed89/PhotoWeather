package com.example.photoweather.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.photoweather.databinding.ItemWeatherBinding
import com.example.photoweather.db.Weather

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {
    class WeatherViewHolder(val bining: ItemWeatherBinding) : RecyclerView.ViewHolder(bining.root)


    private val diffCallBack = object : DiffUtil.ItemCallback<Weather>() {
        override fun areItemsTheSame(oldItem: Weather, newItem: Weather): Boolean {
            return oldItem == newItem

        }

        override fun areContentsTheSame(oldItem: Weather, newItem: Weather): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    private val diff = AsyncListDiffer(this, diffCallBack)
    var list: List<Weather>
        get() = diff.currentList
        set(value) = diff.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = ItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return WeatherViewHolder(
            binding
        )
    }

    private var onItemClickListener: ((Bitmap, View) -> Unit)? = null

    fun setOnItemClickListener(listener: (Bitmap, View) -> Unit) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val url = list[position]
        holder.bining.apply {
            holder.bining.shareButton.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(url.img!!, it)
                }

            }
            val displayMetrics = this.imageView2.context.resources.displayMetrics

            imageView2.setImageBitmap(
                Bitmap.createScaledBitmap(
                    url.img!!,
                    displayMetrics.widthPixels / 2,
                    displayMetrics.widthPixels / 2,
                    true
                )
            )

        }


    }

    override fun getItemCount(): Int {

        return list.size
    }


}
