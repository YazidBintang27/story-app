package com.latihan.storyou.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.latihan.storyou.R
import com.latihan.storyou.data.remote.models.StoriesResponse
import com.latihan.storyou.databinding.ItemStoriesBinding
import com.latihan.storyou.view.pages.HomeFragmentDirections
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class StoriesAdapter: RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {

   private var data: List<StoriesResponse.Story?>? = listOf()
   private lateinit var fragment: Fragment

   class ViewHolder(
      private val binding: ItemStoriesBinding
   ): RecyclerView.ViewHolder(binding.root) {
      fun bind(data: StoriesResponse.Story?, fragment: Fragment) {
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

   override fun getItemCount(): Int {
      return data?.size ?: 0
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.bind(data?.get(position), fragment)
   }

   @SuppressLint("NotifyDataSetChanged")
   fun setData(data: List<StoriesResponse.Story?>?) {
      this.data = data
      notifyDataSetChanged()
   }

   fun setFragment(fragment: Fragment) {
      this.fragment = fragment
   }
}