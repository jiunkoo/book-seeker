package com.example.bookseeker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginBtn: Button = findViewById(R.id.login_btn_login)
        loginBtn.setOnClickListener {
            val nextIntent = Intent(this, SearchActivity::class.java)
            startActivity(nextIntent)
        }
    }
}