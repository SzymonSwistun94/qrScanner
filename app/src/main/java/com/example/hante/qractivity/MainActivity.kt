package com.example.hante.qractivity

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import com.example.hante.qractivity.R.id.button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var qrDialog: QrDialog? = null
    var qrText: String? = null
    val handler = Handler(object: Handler.Callback {
        override fun handleMessage(msg: Message?): Boolean {
            var ret = false

            if (msg!!.data.containsKey("texts")) {
                ret = true
                Toast.makeText(applicationContext, (msg.data["texts"] as ArrayList<String>).joinToString(", "), Toast.LENGTH_LONG).show()
                Log.i("MainActivity", (msg.data["texts"] as ArrayList<String>).joinToString(", "))
            }

            return ret;
        }

    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 2)
        }

        button.setOnClickListener {
            qrDialog = QrDialog(this, { qrText = it.first() }, handler)
            qrDialog?.show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        qrDialog!!.dismiss()
        qrDialog!!.cancel()
        qrDialog = null
    }
}
