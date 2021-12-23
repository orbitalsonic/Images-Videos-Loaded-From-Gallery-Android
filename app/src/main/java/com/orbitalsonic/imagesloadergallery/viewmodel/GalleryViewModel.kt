package com.orbitalsonic.imagesloadergallery.viewmodel

import android.app.Activity
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.orbitalsonic.imagesloadergallery.managers.LoaderManager
import com.orbitalsonic.imagesloadergallery.utils.Constants.PICTURE_CODE
import com.orbitalsonic.imagesloadergallery.utils.Constants.VIDEOS_CODE

class GalleryViewModel : ViewModel() {

    private val _picLoad = MutableLiveData<Boolean>()
    var isPicturesLoaded: LiveData<Boolean> = Transformations.map(_picLoad) { it }

    fun setPicsLoaded(mPicLoad: Boolean) {
        _picLoad.value = mPicLoad
    }

    fun getGalleryImages(context: Activity) {
        if (LoaderManager.isPicturesListEmpty()) {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
            )
            val selection = MediaStore.Images.Media.MIME_TYPE + " ='image/png' OR " +
                    MediaStore.Images.Media.MIME_TYPE + " ='image/jpg' OR " +
                    MediaStore.Images.Media.MIME_TYPE + " ='image/jpeg'"

            LoaderManager.getMediaLoader(context as AppCompatActivity,
                projection,
                uri,
                selection,
                PICTURE_CODE,
                MediaStore.Images.Media.DATE_MODIFIED + " DESC") {

                Log.i("GalleryTesting","${LoaderManager.picturesList.size}")
                setPicsLoaded(true)
            }
        } else {
             setPicsLoaded(true)
        }
    }

    fun getGalleryVideos(context: Activity) {
        if (LoaderManager.isVideosListEmpty()) {
            val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media._ID
            )
            val selection = MediaStore.Video.Media.MIME_TYPE + " ='video/mp4' OR " +
                    MediaStore.Video.Media.MIME_TYPE + " ='video/WMV' OR " +
                    MediaStore.Video.Media.MIME_TYPE + " ='video/MKV' OR " +
                    MediaStore.Video.Media.MIME_TYPE + " ='video/AVI' OR " +
                    MediaStore.Video.Media.MIME_TYPE + " ='video/MOV'"

            LoaderManager.getMediaLoader(context as AppCompatActivity,
                projection,
                uri,
                selection,
                VIDEOS_CODE,
                MediaStore.Images.Media.DATE_MODIFIED + " DESC") {
                setPicsLoaded(true)

            }
        } else {
        setPicsLoaded(true)
        }
    }

}