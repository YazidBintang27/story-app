package com.latihan.storyou.view.adapter

import android.annotation.SuppressLint
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.latihan.storyou.R
import com.latihan.storyou.data.local.room.StoryEntity
import com.latihan.storyou.databinding.ItemStoriesBinding
import com.latihan.storyou.view.pages.HomeFragmentDirections
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class StoriesAdapter : PagingDataAdapter<StoryEntity, StoriesAdapter.ViewHolder>(DIFF_CALLBACK) {

   private lateinit var fragment: Fragment

   class ViewHolder(
      private val binding: ItemStoriesBinding
   ) : RecyclerView.ViewHolder(binding.root) {
      @SuppressLint("SetTextI18n")
      fun bind(data: StoryEntity?, fragment: Fragment) {
         val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
         }
         val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
         val parsedDate = data?.createdAt?.let { inputFormat.parse(it) }
         val formattedDate = parsedDate?.let { outputFormat.format(it) } ?: "Unknown Date"
         binding.apply {
            Glide.with(ivItemPhoto)
               .load(data?.photoUrl)
               .error(R.drawable.noimage)
               .into(ivItemPhoto)
            tvItemName.text = data?.name
            tvItemDescription.text = data?.description
            tvItemDate.text = formattedDate
            val geoCoder = Geocoder(itemView.context, Locale.getDefault())
            try {
               if (data?.lat != null && data.lon != null) {
                  val addresses = geoCoder.getFromLocation(data.lat, data.lon, 1)
                  if (!addresses.isNullOrEmpty()) {
                     val cityName = addresses[0].subAdminArea
                     val countryName = addresses[0].countryName
                     tvItemLocation.text = "$cityName, $countryName"
                  }
               } else {
                  tvItemLocation.text = itemView.context.getString(R.string.no_location)
               }
            } catch (e: IOException) {
               Log.e("GeoCoderError", "${e.message}")
            }
         }
         itemView.setOnClickListener {
            val toDetailFragment = HomeFragmentDirections.actionHomeFragmentToDetailFragment(data?.id ?: "")
            fragment.findNavController().navigate(toDetailFragment)
         }
      }
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val layoutInflater = LayoutInflater.from(parent.context)
      val binding = ItemStoriesBinding.inflate(layoutInflater, parent, false)
      return ViewHolder(binding)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val story = getItem(position)
      holder.bind(story, fragment)
   }

   fun setFragment(fragment: Fragment) {
      this.fragment = fragment
   }

   companion object {
      val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
         override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
            return oldItem.id == newItem.id
         }

         override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
            return oldItem == newItem
         }
      }
   }
}
