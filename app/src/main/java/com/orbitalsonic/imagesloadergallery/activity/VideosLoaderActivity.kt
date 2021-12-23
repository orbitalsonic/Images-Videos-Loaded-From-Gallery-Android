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
import com.orbitalsonic.imagesloadergallery.databinding.ActivityVideosLoaderBinding
import com.orbitalsonic.imagesloadergallery.datamodel.FileItem
import com.orbitalsonic.imagesloadergallery.interfaces.OnItemClickListener
import com.orbitalsonic.imagesloadergallery.interfaces.OnSelectedItemClickListener
import com.orbitalsonic.imagesloadergallery.managers.LoaderManager
import com.orbitalsonic.imagesloadergallery.utils.ConversionsUtils
import com.orbitalsonic.imagesloadergallery.viewmodel.GalleryViewModel
import java.util.ArrayList

class VideosLoaderActivity : AppCompatActivity() {

    private lateinit var binding : ActivityVideosLoaderBinding

    private lateinit var mAdapterVideos: AdapterGalleryLoader
    private lateinit var mAdapterSelected: AdapterGallerySelected

    private  var selectedVideosList: ArrayList<FileItem> = ArrayList()

    private lateinit var galleryViewModel: GalleryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_videos_loader)

        initViewModel()
        galleryViewModel.getGalleryVideos(this)
        createLoaderRecyclerView()
        createSelectedRecyclerView()

    }


    private fun initViewModel(){
        galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        galleryViewModel.isPicturesLoaded.observe(this) {
            if (it){
                mAdapterVideos.submitList(LoaderManager.videosList)
                binding.loadingProgressBar.visibility = View.GONE
            }
        }
    }

    private fun createLoaderRecyclerView() {
        mAdapterVideos = AdapterGalleryLoader()
        binding.loaderRecyclerview.adapter = mAdapterVideos
        binding.loaderRecyclerview.layoutManager = GridLayoutManager(this, 4)

        mAdapterVideos.setOnItemClickListener(object : OnItemClickListener {
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
        mAdapterSelected.submitList(selectedVideosList)


        mAdapterSelected.setOnItemClickListener(object : OnSelectedItemClickListener {
            override fun onItemClick(position: Int) {
                showMessage(selectedVideosList[position].imagePath)
            }

            override fun onCancelClick(position: Int) {
                onCancelDataViewsChanged(position)
            }

        })

    }

    private fun onDataViewsChanged(position:Int){
        mAdapterVideos.currentList[position].isImageSelected = !mAdapterVideos.currentList[position].isImageSelected
        if (mAdapterVideos.currentList[position].isImageSelected){
            selectedVideosList.add(mAdapterVideos.currentList[position])
            mAdapterSelected.notifyItemInserted(selectedVideosList.size-1)
            binding.selectedRecyclerview.scrollToPosition(selectedVideosList.size-1)
        }else{
            val mPos = selectedVideosList.indexOf(mAdapterVideos.currentList[position])
            selectedVideosList.remove(mAdapterVideos.currentList[position])
            mAdapterSelected.notifyItemRemoved(mPos)

        }


        mAdapterVideos.notifyItemChanged(position)
    }

    private fun onCancelDataViewsChanged(position: Int){
        mAdapterVideos.currentList[selectedVideosList[position].imageIndex].isImageSelected = !mAdapterVideos.currentList[selectedVideosList[position].imageIndex].isImageSelected
        mAdapterVideos.notifyItemChanged(selectedVideosList[position].imageIndex)
        selectedVideosList.removeAt(position)
        mAdapterSelected.notifyItemRemoved(position)
    }

    private fun returnForResult(){
        val replyIntent = Intent()
        replyIntent.putExtra(
            "gallery_data",
            ConversionsUtils.listToString(selectedVideosList)
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
                if (selectedVideosList.isNotEmpty()){
                    returnForResult()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



}