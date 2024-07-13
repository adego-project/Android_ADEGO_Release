package com.seogaemo.android_adego.view.alarm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.seogaemo.android_adego.data.UserResponse
import com.seogaemo.android_adego.databinding.AlarmItemBinding

class AlarmAdapter(private val userList: List<UserResponse>): RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = AlarmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding).also { handler->
            binding.root.apply {
                callUser(userList[handler.adapterPosition])
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    class AlarmViewHolder(private val binding: AlarmItemBinding):
        ViewHolder(binding.root) {
        fun bind(user: UserResponse) {
            if (binding.root.context != null) {
                Glide.with(binding.root.context)
                    .load(user.profileImage)
                    .centerCrop()
                    .into(binding.imageView)
            }
            binding.nicknameText.text = user.name
        }
    }

    private fun callUser(user: UserResponse) {

    }

}