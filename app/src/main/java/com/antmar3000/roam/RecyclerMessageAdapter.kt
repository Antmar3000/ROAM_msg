package com.antmar3000.roam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.antmar3000.roam.databinding.ChatListItemBinding

class RecyclerMessageAdapter : ListAdapter<AuthenticationData, RecyclerMessageAdapter.ItemHolder>(ItemCompare()) {

    class ItemHolder (private val binding: ChatListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind (user: AuthenticationData) = with (binding) {
            textMessage.text = user.message
            textUserName.text = user.name
        }

        companion object {
            fun create (parent: ViewGroup) : ItemHolder {
                return ItemHolder(ChatListItemBinding.inflate(LayoutInflater.from(parent.context),
                    parent, false))
            }
        }
    }

    class ItemCompare: DiffUtil.ItemCallback<AuthenticationData>() {
        override fun areItemsTheSame(
            oldItem: AuthenticationData,
            newItem: AuthenticationData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: AuthenticationData,
            newItem: AuthenticationData
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(getItem(position))
    }
}