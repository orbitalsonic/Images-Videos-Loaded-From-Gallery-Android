package com.orbitalsonic.imagesloadergallery.managers

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.orbitalsonic.imagesloadergallery.datamodel.FileItem
import com.orbitalsonic.imagesloadergallery.datamodel.GalleryModel
import com.orbitalsonic.imagesloadergallery.utils.Constants.PICTURE_CODE
import com.orbitalsonic.imagesloadergallery.utils.Constants.VIDEOS_CODE
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class LoaderManager {

    companion object {

        val handler = CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
        }

        private val PICTURE_MAP: HashMap<String, MutableList<FileItem>> = hashMapOf()
        private val VIDEO_MAP: HashMap<String, MutableList<FileItem>> = hashMapOf()

        private var videoIndex = 0
        private var pictureIndex = 0
        private var multiMapIndex = 0
        val picturesList = ArrayList<FileItem>()
        val videosList = ArrayList<FileItem>()


        fun getMediaLoader(
            activity: AppCompatActivity,
            projection: Array<String>,
            uri: Uri,
            selection: String?,
            id: Int,
            orderBy: String,
            callback: () -> Unit
        ) {

            videoIndex = 0
            pictureIndex = 0
            multiMapIndex = 0

            val loaderCallbacks = object : LoaderManager.LoaderCallbacks<Cursor> {
                override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {

                    return CursorLoader(
                        activity,
                        uri,
                        projection,
                        selection,
                        null,
                        orderBy
                    )
                }

                @SuppressLint("Range")
                override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
                    if (data == null)
                        Log.d("DEBUG_TAG", "onLoadFinished: Cursor is NULL")
                    GlobalScope.launch(Dispatchers.Main + handler) {
                        async(Dispatchers.IO + handler) {
                            val multiMap = HashMap<String, ArrayList<FileItem>>()
                            if (id == PICTURE_CODE) {
                                picturesList.clear()
                            } else {
                                videosList.clear()
                            }
                            val DISPLAY_NAME_COLUMN =
                                if (id == PICTURE_CODE) MediaStore.Images.Media.BUCKET_DISPLAY_NAME else MediaStore.Video.Media.BUCKET_DISPLAY_NAME
                            val PATH_COLUMN =
                                if (id == PICTURE_CODE) MediaStore.Images.Media.DATA else MediaStore.Video.Media.DATA
                            val ID_COLUMN =
                                if (id == PICTURE_CODE) MediaStore.Images.Media._ID else MediaStore.Video.Media._ID

                            while (data!!.moveToNext()) {
                                val bucket =
                                    data.getString(data.getColumnIndex(DISPLAY_NAME_COLUMN))
                                val image = data.getString(data.getColumnIndex(PATH_COLUMN))
                                val dataId = data.getString(data.getColumnIndex(ID_COLUMN))
                                bucket?.let {
                                    if (multiMap.containsKey(it)) {
                                        val listOfValues = multiMap[it]
                                        listOfValues!!.add(
                                            FileItem(
                                                multiMapIndex++,
                                                dataId,
                                                image,
                                                false
                                            )
                                        )
                                        multiMap.put(it, listOfValues)

                                    } else {
                                        val listOfValues = ArrayList<FileItem>()
                                        listOfValues.add(
                                            FileItem(
                                                multiMapIndex++,
                                                dataId,
                                                image,
                                                false
                                            )
                                        )
                                        multiMap.put(it, listOfValues)
                                    }
                                }
                                if (id == VIDEOS_CODE) {
                                    videosList.add(FileItem(videoIndex++, dataId, image, false))
                                } else {
                                    picturesList.add(FileItem(pictureIndex++, dataId, image, false))
                                }
                            }
                            data.close()
                            multiMap.forEach {
                                addDataToMap(id, it.key, it.value)
                            }
                        }.await()
                        callback.invoke()
                        LoaderManager.getInstance(activity).destroyLoader(id)
                    }
                }

                override fun onLoaderReset(loader: Loader<Cursor>) {

                }
            }
            activity.runOnUiThread {
                LoaderManager.getInstance(activity).initLoader(id, null, loaderCallbacks)
            }
        }

        fun getPicturesFoldersList(): MutableList<GalleryModel> {
            return PICTURE_MAP.entries
                .asSequence()
                .filter {
                    it.value.isNotEmpty()
                }
                .map {
                    it.key to it.value[0]
                }
                .map {
                    var folderSize = 0
                    val mutableList1 = PICTURE_MAP[it.first]?.size
                    mutableList1?.let { fSize ->
                        folderSize = fSize
                    }
                    GalleryModel(it.first, it.second.imagePath, folderSize)
//                    FolderItem(it.first, it.second.id, it.second.imagePath, folderSize)
                }
                .sortedBy {
                    it.fileName.lowercase()
                }
                .toMutableList()
        }

        fun getVideosFoldersList(): MutableList<GalleryModel> {
            return VIDEO_MAP.entries
                .asSequence()
                .filter {
                    it.value.isNotEmpty()
                }
                .map {
                    it.key to it.value[0]
                }
                .map {
                    var folderSize = 0
                    val mutableList1 = VIDEO_MAP[it.first]?.size
                    mutableList1?.let { fSize ->
                        folderSize = fSize
                    }
                    GalleryModel(it.first, it.second.imagePath, folderSize)
                }
                .sortedBy {
                    it.fileName.lowercase()
                }
                .toMutableList()
        }

        fun isPicturesListEmpty() = PICTURE_MAP.isEmpty()
        fun isVideosListEmpty() = VIDEO_MAP.isEmpty()

        fun getPicturesList(): MutableList<FileItem> {
            val list = mutableListOf<FileItem>()
            list.addAll(picturesList)
            return list
        }

        fun getVideosList(): MutableList<FileItem> {
            val list = mutableListOf<FileItem>()
            list.addAll(videosList)
            return list
        }

        fun getPicturesFromFolders(folderName: String): ArrayList<FileItem>? =
            if (PICTURE_MAP[folderName] != null) PICTURE_MAP[folderName] as ArrayList<FileItem> else null

        fun getVideosFromFolder(folderName: String): ArrayList<FileItem>? =
            if (VIDEO_MAP[folderName] != null) VIDEO_MAP[folderName] as ArrayList<FileItem> else null

        private fun addDataToMap(id: Int, title: String, list: MutableList<FileItem>) {
            if (id == PICTURE_CODE) {
                PICTURE_MAP[title] = list
            }
            if (id == VIDEOS_CODE) {
                VIDEO_MAP[title] = list
            }
        }

        fun clearGalleryData() {
            PICTURE_MAP.clear()
            VIDEO_MAP.clear()
            picturesList.clear()
            videosList.clear()
        }
    }

}