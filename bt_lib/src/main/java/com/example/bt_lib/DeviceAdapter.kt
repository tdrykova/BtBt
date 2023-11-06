package com.example.bt_lib

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bt_lib.databinding.ListItemBinding

class DeviceAdapter(private val listener: Listener) :
    ListAdapter<DeviceItem, DeviceAdapter.DeviceHolder>(Comparator()) {
    private var oldCheckBox: CheckBox? = null

    class DeviceHolder(
        view: View,
        private val adapter: DeviceAdapter,
        private val listener: Listener
    ) : RecyclerView.ViewHolder(view) {
        private val b = ListItemBinding.bind(view)
        private var device: DeviceItem? = null

        init {
            b.checkBox.setOnClickListener {
                device?.let { it1 -> listener.onClick(it1) }
                adapter.selectedCheckBox(it as CheckBox)
            }
            itemView.setOnClickListener {
                device?.let { it1 -> listener.onClick(it1) }
                adapter.selectedCheckBox(b.checkBox)
            }
        }

        fun bind(item: DeviceItem) = with(b) {
            device = item
            tvName.text = item.name
            tvMac.text = item.mac
            if (item.isChecked) adapter.selectedCheckBox(checkBox)
        }
    }

    class Comparator : DiffUtil.ItemCallback<DeviceItem>() {
        override fun areItemsTheSame(oldItem: DeviceItem, newItem: DeviceItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DeviceItem, newItem: DeviceItem): Boolean {
            return oldItem == newItem
        }

    }

    // inflate list_item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return DeviceHolder(view, this, listener)
    }

    // full list_item layout
    override fun onBindViewHolder(holder: DeviceHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun selectedCheckBox(checkBox: CheckBox) {
        oldCheckBox?.isChecked = false
        oldCheckBox = checkBox
        oldCheckBox?.isChecked = true
    }

    interface Listener {
        fun onClick(device: DeviceItem)
    }
}