package com.danielniklaus.cermatites.usersearch.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.danielniklaus.cermatites.AppExecutors
import com.danielniklaus.cermatites.R
import com.danielniklaus.cermatites.binding.DataBoundListAdapter
import com.danielniklaus.cermatites.databinding.ItemSearchBinding
import com.danielniklaus.cermatites.usersearch.vo.User
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class SearchListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val showFullName: Boolean
) : DataBoundListAdapter<User, ItemSearchBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id

        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun createBinding(parent: ViewGroup): ItemSearchBinding {
        val binding = DataBindingUtil.inflate<ItemSearchBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_search,
            parent,
            false,
            dataBindingComponent
        )
        binding.root.setOnClickListener {
            Snackbar.make(parent,"Anda sedang memilih user "+binding.users!!.login,Snackbar.LENGTH_LONG).show()
        }
        return binding
    }

    override fun bind(binding: ItemSearchBinding, item: User) {
        binding.users = item
    }
}
