package com.snn.repostock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setup()
    }

    private fun setup() {
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, FragmentProfile())
                        .commit()
                    true
                }
                R.id.page_qr_scan -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, FragmentScanner())
                        .commit()
                    true
                }
                R.id.page_analysis -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, FragmentStock())
                        .commit()
                    true
                }
                R.id.page_admin -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, FragmentAdministrative())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}