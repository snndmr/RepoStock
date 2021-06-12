package com.snn.repostock

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_pay.*

class PayActivity : AppCompatActivity() {
    private var barcodes = ArrayList<String>()

    private var nfcAdapter: NfcAdapter? = null
    private var nfcPendingIntent: PendingIntent? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)

        barcodes = intent.getStringArrayListExtra("barcodes") as ArrayList<String>

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            0
        )
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        text_view_success.text = "Success"
        airbnb_success.visibility = View.VISIBLE
        airbnb_get.visibility = View.INVISIBLE

        barcodes.forEach { barcode ->
            Firebase.database.reference.child("product").get().addOnSuccessListener {
                if (it.hasChild(barcode)) {
                    val iterator = it.child(barcode).value as HashMap<*, *>

                    var stock = iterator["stock"] as Long
                    stock -= 1

                    Firebase.database.reference.child("product").child(barcode).child("stock")
                        .setValue(stock)
                }
            }
        }

        Snackbar.make(
            findViewById(android.R.id.content),
            "Success!",
            Snackbar.LENGTH_LONG
        ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                finish()
            }
        }).show()
    }
}