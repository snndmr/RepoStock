package com.snn.repostock

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import kotlinx.android.synthetic.main.bottom_sheet_persistent_list.*
import kotlinx.android.synthetic.main.fragment_scanner.*


class FragmentScanner : Fragment() {
    private var lastText: String = ""
    private val barcodes = ArrayList<String>()
    private val products = ArrayList<Product>()
    private val formats = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onResume() {
        super.onResume()

        var total = 0.0
        val integrator = IntentIntegrator.forSupportFragment(this)

        barcode_scanner.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcode_scanner.initializeFromIntent(integrator.createScanIntent())
        barcode_scanner.decodeContinuous { result ->
            if (!(result.text == null || result.text == lastText)) {
                lastText = result.text

                Firebase.database.reference.child("product").get().addOnSuccessListener {
                    if (it.hasChild(lastText)) {
                        if (barcodes.isEmpty()) {
                            val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_list)
                            bottomSheetBehavior.peekHeight =
                                ((60 * resources.displayMetrics.density).toInt())
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                            text_view_pay_button.setOnClickListener {
                                Log.e("TAG", "onResume: Pay Clicked")
                            }
                        }

                        barcodes.add(lastText)

                        val iterator = it.child(lastText).value as HashMap<*, *>
                        val product = Product(
                            iterator["name"] as String,
                            iterator["category"] as String,
                            iterator["price"] as Double,
                            iterator["stock"] as Long,
                            iterator["stocksChange"] as List<Int>,
                            it.key as String
                        )

                        products.add(product)
                        val inflater = activity?.layoutInflater
                        val item = inflater?.inflate(R.layout.product_item, null)

                        (item?.findViewById(R.id.text_view_product_name) as TextView).text =
                            product.name
                        (item.findViewById(R.id.text_view_product_stock) as TextView).text =
                            product.price.toString()
                        (item.findViewById(R.id.text_view_product_static_stock) as TextView).text =
                            "Price"

                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.setMargins(16, 16, 16, 16)
                        linear_layout_root.addView(item, layoutParams)

                        total += product.price
                        text_view_pay_total.text = total.toString()

                        text_view_pay_button.setOnClickListener {
                            Log.e("TAG", "onResume: Pay")
                            val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_list)
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                            val intent = Intent(activity, PayActivity::class.java)
                            intent.putExtra("barcodes", barcodes)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(
                            activity,
                            "There is no product with $lastText barcode!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener {
                    Log.e("firebase", "Error getting data", it)
                }
            }
        }
        barcode_scanner.setStatusText("")
        barcode_scanner.resume()
    }

    override fun onPause() {
        super.onPause()
        barcode_scanner.pause()
    }
}