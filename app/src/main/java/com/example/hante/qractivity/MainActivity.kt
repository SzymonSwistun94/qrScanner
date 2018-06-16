package com.example.hante.qractivity

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.example.hante.qractivity.R.id.button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var qrDialog: QrDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 2)
        }

        button.setOnClickListener {
            qrDialog = QrDialog(this, { Toast.makeText(applicationContext, it.joinToString(", "), Toast.LENGTH_LONG).show()})
            qrDialog?.show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        qrDialog?.dismiss()
        qrDialog?.cancel()
        qrDialog = null
    }
}
