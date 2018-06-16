package com.example.hante.qractivity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_qr.*

class QrDialog(context: Context?, val onQrDetected: (List<String>) -> Unit) : Dialog(context) {

    constructor(context: Context?, onQrDetected: OnDetectedCallback) : this(context, {onQrDetected.qrDetected(it)})

    interface OnDetectedCallback {

        fun qrDetected(texts: List<String>)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_qr)

        setOnShowListener { Log.i("QrDialog", "Dialog shown") }

        qrView.startQrDetection({onQrDetected; this.onBackPressed()})
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        qrView.stopQrDetection()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        dismiss()
        cancel()
    }

}