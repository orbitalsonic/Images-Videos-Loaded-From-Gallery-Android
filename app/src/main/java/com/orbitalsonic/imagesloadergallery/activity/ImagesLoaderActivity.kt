package com.orbitalsonic.imagesloadergallery.activity


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.orbitalsonic.imagesloadergallery.R
import com.orbitalsonic.imagesloadergallery.adapters.AdapterGalleryLoader
import com.orbitalsonic.imagesloadergallery.adapters.AdapterGallerySelected
import com.orbitalsonic.imagesloadergallery.databinding.ActivityImagesLoaderBinding
import com.orbitalsonic.imagesloadergallery.datamodel.FileItem
import com.orbitalsonic.imagesloadergallery.interfaces.OnItemClickListener
import com.orbitalsonic.imagesloadergallery.interfaces.OnSelectedItemClickListener
import com.orbitalsonic.imagesloadergallery.managers.LoaderManager
import com.orbitalsonic.imagesloadergallery.managers.LoaderManager.Companion.picturesList
import com.orbitalsonic.imagesloadergallery.utils.ConversionsUtils
import com.orbitalsonic.imagesloadergallery.viewmodel.GalleryViewModel
import java.util.*

class ImagesLoaderActivity : AppCompatActivity() {

    private lateinit var binding : ActivityImagesLoaderBinding

    private lateinit var mAdapterImages: AdapterGalleryLoader
    private lateinit var mAdapterSelected: AdapterGallerySelected

    private  var selectedImagesList: ArrayList<FileItem> = ArrayList()

    private lateinit var galleryViewModel: GalleryViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_images_loader)

        initViewModel()
        galleryViewModel.getGalleryImages(this)
        createLoaderRecyclerView()
        createSelectedRecyclerView()

    }

    private fun initViewModel(){
        galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        galleryViewModel.isPicturesLoaded.observe(this) {
            if (it){
                mAdapterImages.submitList(picturesList)
                binding.loadingProgressBar.visibility = View.GONE
            }
        }
    }

    private fun createLoaderRecyclerView() {
        mAdapterImages = AdapterGalleryLoader()
        binding.loaderRecyclerview.adapter = mAdapterImages
        binding.loaderRecyclerview.layoutManager = GridLayoutManager(this, 4)



        mAdapterImages.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                onDataViewsChanged(position)
            }

        })


    }

    private fun createSelectedRecyclerView() {
        val mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mAdapterSelected = AdapterGallerySelected()
        binding.selectedRecyclerview.adapter = mAdapterSelected
        binding.selectedRecyclerview.layoutManager = mLayoutManager
        mAdapterSelected.submitList(selectedImagesList)


        mAdapterSelected.setOnItemClickListener(object : OnSelectedItemClickListener {
            override fun onItemClick(position: Int) {
                showMessage(selectedImagesList[position].imagePath)
            }

            override fun onCancelClick(position: Int) {
                onCancelDataViewsChanged(position)
            }

        })

    }

    private fun onDataViewsChanged(position:Int){
        mAdapterImages.currentList[position].isImageSelected = !mAdapterImages.currentList[position].isImageSelected
        if (mAdapterImages.currentList[position].isImageSelected){
            selectedImagesList.add(mAdapterImages.currentList[position])
            mAdapterSelected.notifyItemInserted(selectedImagesList.size-1)
            binding.selectedRecyclerview.scrollToPosition(selectedImagesList.size-1)
        }else{
            val mPos = selectedImagesList.indexOf(mAdapterImages.currentList[position])
            selectedImagesList.remove(mAdapterImages.currentList[position])
            mAdapterSelected.notifyItemRemoved(mPos)

        }


        mAdapterImages.notifyItemChanged(position)
    }

    private fun onCancelDataViewsChanged(position: Int){
        mAdapterImages.currentList[selectedImagesList[position].imageIndex].isImageSelected = !mAdapterImages.currentList[selectedImagesList[position].imageIndex].isImageSelected
        mAdapterImages.notifyItemChanged(selectedImagesList[position].imageIndex)
        selectedImagesList.removeAt(position)
        mAdapterSelected.notifyItemRemoved(position)

    }

    private fun returnForResult(){
        val replyIntent = Intent()
        replyIntent.putExtra(
            "gallery_data",
            ConversionsUtils.listToString(selectedImagesList)
        )
        setResult(Activity.RESULT_OK, replyIntent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        LoaderManager.clearGalleryData()
    }


    private fun showMessage(message:String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_done, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id._done -> {
                if (selectedImagesList.isNotEmpty()){
                    returnForResult()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}