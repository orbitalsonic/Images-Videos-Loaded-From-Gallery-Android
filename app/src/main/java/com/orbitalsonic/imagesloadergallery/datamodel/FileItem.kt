package com.orbitalsonic.imagesloadergallery.datamodel

data class FileItem(
    var imageIndex:Int,
    var imageId:String,
    var imagePath: String,
    var isImageSelected: Boolean
)