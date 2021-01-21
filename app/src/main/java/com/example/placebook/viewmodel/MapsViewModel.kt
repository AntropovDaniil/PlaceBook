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

    private var bookmarks: LiveData<List<BookmarkView>>? = null

    private var bookmarkRepository: BookmarkRepository = BookmarkRepository(getApplication())

    fun addBookmarkFromPlace(place: Place, image: Bitmap?){
        val bookmark = bookmarkRepository.createBookmark()

        bookmark.placeId = place.id
        bookmark.name = place.name.toString()
        bookmark.longitude = place.latLng?.longitude ?: 0.0
        bookmark.latitude = place.latLng?.latitude ?: 0.0
        bookmark.phone = place.phoneNumber.toString()
        bookmark.address = place.address.toString()
        bookmark.category = getPlaceCategory(place)

        val newId = bookmarkRepository.addBookmark(bookmark)

        image?.let {
            bookmark.setImage(it, getApplication())
        }

        Log.i(TAG, "New bookmark $newId added to the database.")
    }

    private fun bookmarkToBookmarkView(bookmark: Bookmark): MapsViewModel.BookmarkView{
        return MapsViewModel.BookmarkView(bookmark.id,
                LatLng(bookmark.latitude, bookmark.longitude),
                bookmark.name,
                bookmark.phone,
                bookmarkRepository.getCategoryResourceId(bookmark.category))
    }

    private fun mapBookmarksToBookmarkView(){
        bookmarks = Transformations.map(bookmarkRepository.allBookmark){
            repoBookmarks ->
                repoBookmarks.map { bookmark ->
                    bookmarkToBookmarkView(bookmark)
                }
        }
    }

    private fun getPlaceCategory(place: Place): String{
        var category = "Other"
        val placeTypes = place.types

        placeTypes?.let { placeTypes ->
            if (placeTypes.size > 0){
                val placeType = placeTypes[0]
                category = bookmarkRepository.placeTypeToCategory(placeType)
            }
        }
        return category
    }

    fun getBookmarkViews(): LiveData<List<BookmarkView>>? {
        if (bookmarks == null){
            mapBookmarksToBookmarkView()
        }
        return bookmarks
    }

    fun addBookmark(latLng: LatLng): Long?{
        val bookmark = bookmarkRepository.createBookmark()
        bookmark.name = "Untitled"
        bookmark.longitude = latLng.longitude
        bookmark.latitude = latLng.latitude
        bookmark.category = "Other"
        return bookmarkRepository.addBookmark(bookmark)
    }

    data class BookmarkView(
        val id: Long? = null,
        val location: LatLng = LatLng(0.0, 0.0),
        val name: String = "",
        val phone: String = "",
        val categoryResourceId: Int? = null
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