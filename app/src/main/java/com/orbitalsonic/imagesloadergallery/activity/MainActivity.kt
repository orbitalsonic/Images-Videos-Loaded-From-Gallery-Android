package com.orbitalsonic.imagesloadergallery.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.orbitalsonic.imagesloadergallery.R
import com.orbitalsonic.imagesloadergallery.adapters.AdapterMainSelected
import com.orbitalsonic.imagesloadergallery.databinding.ActivityMainBinding
import com.orbitalsonic.imagesloadergallery.datamodel.FileItem
import com.orbitalsonic.imagesloadergallery.interfaces.OnItemClickListener
import com.orbitalsonic.imagesloadergallery.utils.Constants.STORAGE_PERMISSION
import com.orbitalsonic.imagesloadergallery.utils.ConversionsUtils
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var mAdapterMainSelected: AdapterMainSelected

    private  var selectedGalleryList: ArrayList<FileItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        createLoaderRecyclerView()

        if (!checkReadWritePermission()) {
            requestStoragePermission()
        }

        binding.btnImages.setOnClickListener {
            if (checkReadWritePermission()) {
                openActivityForResult(Intent(this,ImagesLoaderActivity::class.java))
            }else{
                requestStoragePermission()
            }
        }

        binding.btnVideos.setOnClickListener {
            if (checkReadWritePermission()) {
                openActivityForResult(Intent(this,VideosLoaderActivity::class.java))
            }else{
                requestStoragePermission()
            }
        }
    }

    private fun createLoaderRecyclerView() {
        mAdapterMainSelected = AdapterMainSelected()
        binding.recyclerview.adapter = mAdapterMainSelected
        binding.recyclerview.layoutManager = GridLayoutManager(this, 4)

        mAdapterMainSelected.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                showMessage(mAdapterMainSelected.currentList[position].imagePath)
            }
        })
    }

    private fun checkReadWritePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION
        )
    }

    private fun showMessage(message:String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val mIntent: Intent? = result.data
            val mDataString = mIntent?.getStringExtra("gallery_data")

            selectedGalleryList = ConversionsUtils.stringToList(mDataString!!)
            mAdapterMainSelected.submitList(selectedGalleryList)

        }
    }

    private fun openActivityForResult(mIntent:Intent) {
        resultLauncher.launch(mIntent)
    }
}