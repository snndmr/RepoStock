package com.snn.repostock

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_persistent_add_product.*
import kotlinx.android.synthetic.main.bottom_sheet_persistent_hire.*
import kotlinx.android.synthetic.main.bottom_sheet_persistent_product.*
import kotlinx.android.synthetic.main.bottom_sheet_persistent_update_product.*
import kotlinx.android.synthetic.main.fragment_administrative.*
import kotlinx.android.synthetic.main.fragment_stock.*
import java.util.*


class FragmentStock : Fragment(), RecyclerViewClickListener {
    private lateinit var uid: String
    private lateinit var adapter: StockAdapter

    private var isUISetup = false
    private val products = arrayListOf<Product>()
    private var filteredProducts = arrayListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        uid = arguments?.getString("user").toString()
        databaseOperations()
        return inflater.inflate(R.layout.fragment_stock, container, false)
    }

    private fun databaseOperations() {
        Firebase.database.reference.child("workers").child(uid).child("isAdmin").get()
            .addOnSuccessListener {
                if (it.value as Boolean) {
                    val edit = "Edit Product"
                    text_view_product_button.text = edit
                }
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        Firebase.database.reference.child("product")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    products.clear()

                    (dataSnapshot.value as HashMap<*, *>).forEach { (key, value) ->
                        val iterator = value as HashMap<*, *>
                        if (iterator.values.size == 5)
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

                    if (!isUISetup) {
                        filteredProducts.addAll(products)
                        setupUI()
                    } else {
                        adapter.filter(products)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun setupUI() {
        adapter = StockAdapter(filteredProducts, this@FragmentStock)
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
        attachFloatingAddToBottom(bottomSheetBehavior)

        floating_add_stock.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        product_cancel_button.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        product_add_button.setOnClickListener {
            if (!(edit_text_add_product_name.text.isNullOrEmpty() ||
                        edit_text_add_product_category.text.isNullOrEmpty() ||
                        edit_text_add_product_price.text.isNullOrEmpty() ||
                        edit_text_add_product_stock.text.isNullOrEmpty())
            ) {
                val product = Product(
                    edit_text_add_product_name.text.toString(),
                    edit_text_add_product_category.text.toString(),
                    edit_text_add_product_price.text.toString().toDouble(),
                    edit_text_add_product_stock.text.toString().toLong(),
                    listOf(edit_text_add_product_stock.text.toString().toInt()),
                    edit_text_add_product_barcode.text.toString()
                )

                addProduct(product)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
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

        text_view_product_button.setOnClickListener {
            edit_text_update_product_name.setText(filteredProducts[position].name)
            edit_text_update_product_category.setText(filteredProducts[position].category)
            edit_text_update_product_stock.setText(filteredProducts[position].stock.toString())
            edit_text_update_product_price.setText(filteredProducts[position].price.toString())

            val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_update_product)
            attachFloatingAddToBottom(bottomSheetBehavior)

            product_update_cancel_button.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            product_update_update_button.setOnClickListener {
                updateProduct(filteredProducts[position])
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            product_update_delete_button.setOnClickListener {
                deleteProduct(filteredProducts[position])
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_product)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun attachFloatingAddToBottom(bottomSheetBehavior: BottomSheetBehavior<LinearLayout>) {
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


    private fun addProduct(product: Product) {
        Firebase.database.reference.child("product").child(product.barcode).push()

        Firebase.database.reference.child("product").child(product.barcode).child("name")
            .setValue(product.name)
        Firebase.database.reference.child("product").child(product.barcode).child("category")
            .setValue(product.category)
        Firebase.database.reference.child("product").child(product.barcode).child("stock")
            .setValue(product.stock)
        Firebase.database.reference.child("product").child(product.barcode).child("price")
            .setValue(product.price)
        Firebase.database.reference.child("product").child(product.barcode).child("stocksChange")
            .setValue(product.stocksChange)
    }

    private fun deleteProduct(product: Product) {
        Firebase.database.reference.child("product").child(product.barcode).removeValue()
    }

    private fun updateProduct(product: Product) {
        Firebase.database.reference.child("product").child(product.barcode).child("name")
            .setValue(edit_text_update_product_name.text.toString())
        Firebase.database.reference.child("product").child(product.barcode).child("category")
            .setValue(edit_text_update_product_category.text.toString())
        Firebase.database.reference.child("product").child(product.barcode).child("stock")
            .setValue(edit_text_update_product_stock.text.toString().toLong())
        Firebase.database.reference.child("product").child(product.barcode).child("price")
            .setValue(edit_text_update_product_price.text.toString().toDouble())
    }

}