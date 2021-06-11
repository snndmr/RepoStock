package com.snn.repostock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user: String = intent.getStringExtra("user").toString()

        setup(user)
    }

    private fun setup(user: String) {
        val bundle = Bundle()
        bundle.putString("user", user)

        val profile = FragmentProfile()
        profile.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, profile)
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
                    val stock = FragmentScanner()
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
}