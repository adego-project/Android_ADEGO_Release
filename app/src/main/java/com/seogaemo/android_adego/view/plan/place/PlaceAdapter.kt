package com.seogaemo.android_adego.view.plan.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.seogaemo.android_adego.data.Address
import com.seogaemo.android_adego.databinding.PlaceItemBinding
import com.seogaemo.android_adego.util.Util.keyboardDown
import com.seogaemo.android_adego.view.plan.PlanActivity
import com.seogaemo.android_adego.view.plan.PlanFinishFragment


class PlaceAdapter(private val addressList: List<Address>): RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding).also { handler->
            binding.root.setOnClickListener {
                val activity = (binding.root.context as PlanActivity)
                val item = addressList[handler.adapterPosition]

                if (item.roadAddressName.isNotBlank() || item.addressName.isNotBlank()) {
                    val address = item.roadAddressName.ifBlank { item.addressName }
                    binding.locationDetailsName.text = address
                    activity.apply {
                        keyboardDown(this)
                        this.planPlace = addressList[handler.adapterPosition].placeName
                        this.planAddress = address
                        this.addFragment(PlanFinishFragment())
                    }
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(addressList[position])
    }

    class PlaceViewHolder(private val binding: PlaceItemBinding):
        ViewHolder(binding.root) {
        fun bind(address: Address) {
            binding.locationName.text = address.placeName

            if (address.roadAddressName.isNotBlank() || address.addressName.isNotBlank()) {
                val textToShow = address.roadAddressName.ifBlank {
                    address.addressName
                }
                binding.locationDetailsName.text = textToShow
            } else {
                binding.locationDetailsName.visibility = View.GONE
            }
        }
    }

}