package com.snn.repostock

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user: String = intent.getStringExtra("user").toString()

        checkPermission()
        setup(user)
    }

    private fun setup(user: String) {
        Firebase.database.reference.child("workers").child(user).child("isAdmin").get()
            .addOnSuccessListener {
                if (it.value as Boolean) {
                    bottom_navigation.menu[3].isVisible = true
                }
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        val bundle = Bundle()
        bundle.putString("user", user)

        val profile = FragmentProfile()
        profile.arguments = bundle

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view, profile)
            .commit()

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, profile)
                        .commit()
                    true
                }
                R.id.page_qr_scan -> {
                    val scanner = FragmentScanner()
                    scanner.arguments = bundle

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, scanner)
                        .commit()
                    true
                }
                R.id.page_analysis -> {
                    val stock = FragmentStock()
                    stock.arguments = bundle

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, stock)
                        .commit()
                    true
                }
                R.id.page_admin -> {
                    val administrative = FragmentAdministrative()
                    administrative.arguments = bundle

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, administrative)
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun checkPermission() {
        val permission = Manifest.permission.CAMERA

        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(permission),
                CAMERA_PERMISSION_CODE
            )
        } else {
            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Camera Permission Granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this@MainActivity, "Camera Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}