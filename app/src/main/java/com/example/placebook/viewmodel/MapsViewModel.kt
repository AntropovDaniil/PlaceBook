package com.example.placebook.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.placebook.model.Bookmark
import com.example.placebook.repository.BookmarkRepository
import com.example.placebook.util.ImageUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place

class MapsViewModel(application: Application): AndroidViewModel(application) {

    private val TAG = "MapsViewModel"

    private var bookmarks: LiveData<List<BookmarkMarkerView>>? = null

    private var bookmarkRepository: BookmarkRepository = BookmarkRepository(getApplication())

    fun addBookmarkFromPlace(place: Place, image: Bitmap?){
        val bookmark = bookmarkRepository.createBookmark()

        bookmark.placeId = place.id
        bookmark.name = place.name.toString()
        bookmark.longitude = place.latLng?.longitude ?: 0.0
        bookmark.latitude = place.latLng?.latitude ?: 0.0
        bookmark.phone = place.phoneNumber.toString()
        bookmark.address = place.address.toString()

        val newId = bookmarkRepository.addBookmark(bookmark)

        image?.let {
            bookmark.setImage(it, getApplication())
        }

        Log.i(TAG, "New bookmark $newId added to the database.")
    }

    private fun bookmarkToMarkerView(bookmark: Bookmark): MapsViewModel.BookmarkMarkerView{
        return MapsViewModel.BookmarkMarkerView(bookmark.id,
                LatLng(bookmark.latitude, bookmark.longitude),
                bookmark.name,
                bookmark.phone)
    }

    private fun mapBookmarksToMarkerView(){
        bookmarks = Transformations.map(bookmarkRepository.allBookmark){
            repoBookmarks ->
                repoBookmarks.map { bookmark ->
                    bookmarkToMarkerView(bookmark)
                }
        }
    }

    fun getBookmarkMarkerViews(): LiveData<List<BookmarkMarkerView>>? {
        if (bookmarks == null){
            mapBookmarksToMarkerView()
        }
        return bookmarks
    }

    data class BookmarkMarkerView(
        var id: Long? = null,
        var location: LatLng = LatLng(0.0, 0.0),
        var name: String = "",
        var phone: String = ""
    ){

        fun getImage(context: Context): Bitmap? {
            id?.let {
                return  ImageUtils.loadBitmapFromFile(context,
                Bookmark.generateImageFilename(it))
            }
            return null
        }
    }
}