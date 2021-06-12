package com.snn.repostock

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.bottom_sheet_persistent_auth.*
import kotlinx.android.synthetic.main.bottom_sheet_persistent_update_product.*

class AuthActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        auth = Firebase.auth
        text_view_sign_in.setOnClickListener {
            signIn(
                edit_text_auth_mail.text.toString(),
                edit_text_auth_password.text.toString()
            )
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_auth)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(this, "Wrong email and password!", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            intent = Intent(this, MainActivity::class.java)
            intent.putExtra("user", user.uid)
            startActivity(intent)
        }
    }
}