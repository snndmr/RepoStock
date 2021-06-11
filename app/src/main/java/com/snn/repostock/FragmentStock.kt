package com.snn.repostock

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.bottom_sheet_persistent_add_product.*
import kotlinx.android.synthetic.main.bottom_sheet_persistent_hire.*
import kotlinx.android.synthetic.main.bottom_sheet_persistent_product.*
import kotlinx.android.synthetic.main.fragment_administrative.*
import kotlinx.android.synthetic.main.fragment_stock.*
import kotlinx.android.synthetic.main.relative_layout_product_item.*
import java.util.*


class FragmentStock : Fragment(), RecyclerViewClickListener {
    private val products = arrayListOf<Product>()
    private var filteredProducts = arrayListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Firebase.database.reference.child("product").get().addOnSuccessListener {
            (it.value as HashMap<*, *>).forEach { (key, value) ->
                val iterator = value as HashMap<*, *>

                products.add(
                    Product(
                        iterator["name"] as String,
                        iterator["category"] as String,
                        iterator["price"] as Double,
                        iterator["stock"] as Long,
                        iterator["stocksChange"] as List<Int>,
                        key as String,
                    )
                )
            }

            setup()
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

        return inflater.inflate(R.layout.fragment_stock, container, false)
    }

    private fun setup() {
        filteredProducts.addAll(products)

        val adapter = StockAdapter(filteredProducts, this@FragmentStock)
        recycler_view_stock.layoutManager = LinearLayoutManager(activity)
        recycler_view_stock.adapter = adapter

        recycler_view_stock.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && floating_add_stock.isShown) floating_add_stock.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) floating_add_stock.show()
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        search_view_product.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(qString: String): Boolean {
                filteredProducts.clear()
                for (product in products) {
                    if (product.name.lowercase().contains(qString.lowercase())) {
                        filteredProducts.add(product)
                    }
                }
                adapter.filter(filteredProducts)
                return true
            }

            override fun onQueryTextSubmit(qString: String): Boolean {
                return true
            }
        })

        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_add_product)

        floating_add_stock.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> floating_add_stock.hide()
                        BottomSheetBehavior.STATE_COLLAPSED -> floating_add_stock.show()
                        else -> floating_add_stock.show()
                    }
                }
            })
        }

        product_cancel_button.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        product_add_button.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun recyclerViewListClicked(position: Int) {
        text_view_bottom_product_name.text = filteredProducts[position].name
        text_view_bottom_product_category.text = filteredProducts[position].category
        text_view_bottom_product_stock.text = filteredProducts[position].stock.toString()
        text_view_bottom_product_price.text = filteredProducts[position].price.toString()

        aa_chart_view_daily_product_stock.aa_drawChartWithChartModel(
            AAChartModel()
                .chartType(AAChartType.Spline)
                .title("Stocks")
                .yAxisTitle("")
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name("Stocks")
                            .data(
                                products[position].stocksChange.toTypedArray()
                            )
                    )
                )
        )

        text_view_product_request.setOnClickListener {
            Log.e("TAG", "Product Requested")
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_product)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}