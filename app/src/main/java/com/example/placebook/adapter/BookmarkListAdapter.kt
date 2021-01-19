package com.example.placebook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.placebook.R
import com.example.placebook.databinding.BookmarkItemBinding
import com.example.placebook.model.Bookmark
import com.example.placebook.ui.MapsActivity
import com.example.placebook.viewmodel.MapsViewModel

class BookmarkListAdapter(
        private var bookmarkData: List<MapsViewModel.BookmarkView>?,
        private val mapsActivity: MapsActivity): RecyclerView.Adapter<BookmarkListAdapter.ViewHolder>() {


            class ViewHolder(v: View, private val mapsActivity: MapsActivity): RecyclerView.ViewHolder(v){

                init {
                    v.setOnClickListener {
                        val bookmarkView = itemView.tag as MapsViewModel.BookmarkView
                        mapsActivity.moveToBookmark(bookmarkView)
                    }
                }

                val nameTextView: TextView = v.findViewById(R.id.bookmarkNameTextView)
                val categoryImageView: ImageView = v.findViewById(R.id.bookmarkIcon)
            }

    fun setBookmarkData(bookmarks: List<MapsViewModel.BookmarkView>){
        this.bookmarkData = bookmarks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.bookmark_item, parent, false), mapsActivity)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookmarkData = bookmarkData ?: return
        val bookmarkViewData = bookmarkData[position]

        holder.itemView.tag = bookmarkViewData
        holder.nameTextView.text = bookmarkViewData.name
        holder.categoryImageView.setImageResource(R.drawable.ic_other)
    }

    override fun getItemCount(): Int {
        return bookmarkData?.size ?: 0
    }
}