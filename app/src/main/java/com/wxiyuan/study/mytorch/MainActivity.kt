package com.wxiyuan.study.mytorch

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private lateinit var handler: Handler

    private var isTorchOn = false

    private val torchCallback = object: CameraManager.TorchCallback() {
        override fun onTorchModeUnavailable(id: String) {
            if (id == cameraId) {
                Toast.makeText(this@MainActivity, "手电筒不可用", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onTorchModeChanged(id: String, enabled: Boolean) {
            if (id == cameraId) {
                isTorchOn = enabled
                btn_switch.setBackgroundResource(
                    if (isTorchOn)
                        R.drawable.torch_on_bg
                    else
                        R.drawable.torch_off_bg
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        cameraManager.unregisterTorchCallback(torchCallback)
        super.onDestroy()
    }

    private fun init() {
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        handler = Handler()
        cameraManager.registerTorchCallback(torchCallback, handler)
        val cameraIds = getCameraIds()
        if (cameraIds.isNullOrEmpty()) {
            Toast.makeText(this, "手电筒不可用", Toast.LENGTH_SHORT).show()
        } else {
            cameraId = cameraIds[0]
            btn_switch.setOnClickListener { toggleTorch() }
        }
    }

    private fun toggleTorch() {
        try {
            cameraManager.setTorchMode(cameraId, !isTorchOn)
        } catch (exception: CameraAccessException) {
            Toast.makeText(this, getErrorMessage(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCameraIds(): Array<String> {
        return try {
            cameraManager.cameraIdList
        } catch (exception: CameraAccessException) {
            arrayOf()
        }
    }

    private fun getErrorMessage(): String {
        return if (isTorchOn) "关闭手电筒失败" else "打开手电筒失败"
    }
}