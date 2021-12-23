package com.orbitalsonic.imagesloadergallery.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.orbitalsonic.imagesloadergallery.R
import com.orbitalsonic.imagesloadergallery.databinding.ItemGalleryLoaderBinding
import com.orbitalsonic.imagesloadergallery.datamodel.FileItem
import com.orbitalsonic.imagesloadergallery.interfaces.OnItemClickListener

class AdapterGalleryLoader : ListAdapter<FileItem, RecyclerView.ViewHolder>(DATA_COMPARATOR){

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemGalleryLoaderBinding = DataBindingUtil.inflate(layoutInflater,R.layout.item_gallery_loader,parent,false)
        viewHolder = GalleryLoaderViewHolder(binding, mListener!!)
        return viewHolder

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        val viewHolder = holder as GalleryLoaderViewHolder
        viewHolder.bind(currentItem)

    }


    class GalleryLoaderViewHolder(binding:ItemGalleryLoaderBinding, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        private val mBinding = binding
        init {

            binding.item.setOnClickListener {
                val mPosition = adapterPosition
                listener.onItemClick(mPosition)
            }

        }

        fun bind(mCurrentItem: FileItem) {
            Glide.with(mBinding.root.context)
                .load(mCurrentItem.imagePath)
                .placeholder(R.drawable.bg_glide)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .into(mBinding.galleryImage)

            mBinding.selectionBox.isChecked = mCurrentItem.isImageSelected
        }

    }

    companion object {
        private val DATA_COMPARATOR = object : DiffUtil.ItemCallback<FileItem>() {
            override fun areItemsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
                return oldItem.imagePath == newItem.imagePath
            }
        }
    }

}