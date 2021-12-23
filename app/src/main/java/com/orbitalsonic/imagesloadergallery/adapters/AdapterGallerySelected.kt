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
import com.orbitalsonic.imagesloadergallery.databinding.ItemGallerySelectedBinding
import com.orbitalsonic.imagesloadergallery.datamodel.FileItem
import com.orbitalsonic.imagesloadergallery.interfaces.OnSelectedItemClickListener

class AdapterGallerySelected :
    ListAdapter<FileItem, AdapterGallerySelected.GallerySelectedViewHolder>(DATA_COMPARATOR) {

    private var mListener: OnSelectedItemClickListener? = null

    fun setOnItemClickListener(listener: OnSelectedItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GallerySelectedViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding:ItemGallerySelectedBinding = DataBindingUtil.inflate(layoutInflater,
            R.layout.item_gallery_selected,parent,false)
        return GallerySelectedViewHolder(binding, mListener!!)

    }

    override fun onBindViewHolder(holder: GallerySelectedViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class GallerySelectedViewHolder(binding:ItemGallerySelectedBinding, listener: OnSelectedItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        private val mBinding = binding
        init {

            binding.selectedImage.setOnClickListener {
                val mPosition = adapterPosition
                listener.onItemClick(mPosition)
            }
            binding.cancelImage.setOnClickListener {
                val mPosition = adapterPosition
                listener.onCancelClick(mPosition)
            }

        }

        fun bind(mCurrentItem: FileItem) {
            Glide.with(mBinding.root.context)
                .load(mCurrentItem.imagePath)
                .placeholder(R.drawable.bg_glide)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .into(mBinding.selectedImage)
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