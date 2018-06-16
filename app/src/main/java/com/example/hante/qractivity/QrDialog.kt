package com.example.hante.qractivity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Window
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_qr.*

class QrDialog(context: Context?, val onQrDetected: (List<String>) -> Unit, val parentHandler: Handler? = null) : Dialog(context) {

    constructor(context: Context?, onQrDetected: OnDetectedCallback, handler: Handler? = null) : this(context, { onQrDetected.qrDetected(it) }, handler)

    val handler = Handler(object : Handler.Callback {
        override fun handleMessage(msg: Message?): Boolean {
            var ret = false
            if (msg!!.data.containsKey("texts")) {
                onQrDetected(msg.data["texts"] as ArrayList<String>)
                ret = true
                if (parentHandler != null) {
                    val forward = Message.obtain(msg)
                    forward.target = parentHandler
                    forward.sendToTarget()
                }
            }
            dispose()
            return ret
        }

    })

    interface OnDetectedCallback {

        fun qrDetected(texts: List<String>)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_qr)
        setOnShowListener { Log.i("QrDialog", "Dialog shown") }

        qrView.startQrDetection({ onQrDetected; dispose() }, handler)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        qrView.stopQrDetection()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        dispose()
    }

    fun dispose() {
        dismiss()

    }

}