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

    private var onShareClickListener: ((Bitmap) -> Unit)? = null

    fun setOnShareClickListener(listener: (Bitmap) -> Unit) {
        onShareClickListener = listener
    }
    private var onImageClickListener: ((Int ,View) -> Unit)? = null

    fun setOnImageClickListener(listener: (Int ,View) -> Unit) {
        onImageClickListener = listener
    }
    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val url = list[position]

        holder.bining.apply {
            holder.bining.shareButton.setOnClickListener {
                onShareClickListener?.let { click ->
                    click(url.img!!)
                }

            }
            imageView2.setImageBitmap(
                url.img
            )
                holder.bining.imageView2.setOnClickListener {
                    onImageClickListener?.let { click ->
                        click(url.id , holder.bining.imageView2)
                    }

                }


        }


    }

    override fun getItemCount(): Int {

        return list.size
    }


}
