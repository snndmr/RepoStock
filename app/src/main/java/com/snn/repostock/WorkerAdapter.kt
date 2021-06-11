package com.snn.repostock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorkerAdapter(
    private var workers: ArrayList<Worker>,
    private val listener: RecyclerViewClickListener
) :
    RecyclerView.Adapter<WorkerAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.worker_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindItems(workers[position], position, listener)
    }

    override fun getItemCount(): Int {
        return workers.size
    }

    fun filter(instant: ArrayList<Worker>) {
        workers = arrayListOf()
        workers.addAll(instant)
        notifyDataSetChanged()
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItems(
            worker: Worker,
            position: Int,
            listener: RecyclerViewClickListener
        ) {
            val cardName = itemView.findViewById(R.id.text_view_admin_worker_name) as TextView

            cardName.text = worker.name

            itemView.setOnClickListener {
                listener.recyclerViewListClicked(position)
            }
        }
    }
}