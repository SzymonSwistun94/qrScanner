package com.example.hante.qractivity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.*
import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Parcel
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.nio.ByteBuffer
import kotlin.math.roundToInt

class QrView(context: Context, attributeSet: AttributeSet) : SurfaceView(context, attributeSet) {

    var onDetectedCallback: ((List<String>) -> Unit)? = null

    val detector: BarcodeDetector = BarcodeDetector.Builder(context)
            .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.QR_CODE)
            .build()

    var cameraSource: CameraSource? = null
    var parentHandler: Handler? = null

    val texts = mutableListOf<String>()

    fun startQrDetection(onDetectedCallback: (List<String>) -> Unit, handler: Handler) {
       parentHandler = handler
        this.onDetectedCallback = onDetectedCallback
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                stopQrDetection()
            }

            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder?) {

                cameraSource = CameraSource.Builder(context, detector).setAutoFocusEnabled(true)
                        .setFacing(CameraSource.CAMERA_FACING_BACK).build()
                detector.setProcessor(object : Detector.Processor<Barcode> {
                    override fun release() {}

                    override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
                        if (p0 != null) {
                            val qrCodes = p0.detectedItems
                            if (qrCodes.size() > 0) {

                                for (i in (0 until qrCodes.size())) {
                                    this@QrView.texts.add(qrCodes.valueAt(i).rawValue)
                                }
                                val message = Message.obtain(handler)
                                val bundle = Bundle()
                                bundle.putStringArrayList("texts", ArrayList(texts))
                                message.setData(bundle)
                                message.sendToTarget()
                            }
                        }
                    }

                })
                cameraSource!!.start(holder)
            }

        })
    }

    fun byteArrayToBitmap(data: ByteArray?): Bitmap {
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
        return bitmap.copy(Bitmap.Config.ARGB_8888, false)
    }

    fun drawBitmap(bitmap: Bitmap) {
        val canvas = holder.lockCanvas()
        canvas!!.drawBitmap(bitmap, null, RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat()), null)
        holder.unlockCanvasAndPost(canvas)
    }

    fun drawByteArray(data: ByteArray?) {
        drawBitmap(byteArrayToBitmap(data))
    }

    fun grabJpegImage(data: ByteArray?) {
        grabJpegImage(byteArrayToBitmap(data!!.clone()))
    }

    fun grabJpegImage(bitmap: Bitmap) {

        Log.i("QrView", "grabJpegImage")

        if (detector.isOperational) {
            Log.i("QrView", "Detector is operational")
            val frame = Frame.Builder().setBitmap(bitmap).build()
            val qrCodes = detector.detect(frame)

            if (qrCodes.size() > 0) {
                Log.i("QrView", "Qr found: ${qrCodes[0].rawValue}")
                val texts = mutableListOf<String>()

                for (i in (0..qrCodes.size())) {
                    Log.i("QrView", "Qr found: ${qrCodes.valueAt(i).rawValue}")
                    texts.add(qrCodes[i].rawValue)
                }

                stopQrDetection()
                onDetectedCallback?.invoke(texts)
            }

            cameraSource!!.takePicture({}, this::grabJpegImage)
        }
    }

    fun stopQrDetection() {
        cameraSource?.release()
        cameraSource = null
    }

}
