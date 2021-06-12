package com.snn.repostock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class StockAdapter(
    private var products: ArrayList<Product>,
    private val listener: RecyclerViewClickListener
) :
    RecyclerView.Adapter<StockAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindItems(products[position], position, listener)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun filter(instant: ArrayList<Product>) {
        products = arrayListOf()
        products.addAll(instant)
        notifyDataSetChanged()
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItems(
            product: Product,
            position: Int,
            listener: RecyclerViewClickListener
        ) {
            val cardName = itemView.findViewById(R.id.text_view_product_name) as TextView
            val cardStock = itemView.findViewById(R.id.text_view_product_stock) as TextView

            cardName.text = product.name
            cardStock.text = product.stock.toString()

            itemView.setOnClickListener {
                listener.recyclerViewListClicked(position)
            }
        }
    }
}