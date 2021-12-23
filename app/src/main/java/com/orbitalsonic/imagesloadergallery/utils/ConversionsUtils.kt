package com.orbitalsonic.imagesloadergallery.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orbitalsonic.imagesloadergallery.datamodel.FileItem
import java.util.ArrayList

object ConversionsUtils {

    fun listToString(galleryList: ArrayList<FileItem>): String {
        return Gson().toJson(galleryList)
    }

    fun stringToList(mString: String): ArrayList<FileItem> {
        val galleryType = object : TypeToken<ArrayList<FileItem>>() {}.type
        return Gson().fromJson(mString, galleryType)
    }
}