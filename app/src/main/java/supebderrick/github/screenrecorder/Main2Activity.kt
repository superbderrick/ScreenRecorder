package supebderrick.github.screenrecorder

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import kotlinx.android.synthetic.main.activity_main2.*
import recoder.RecorderFactory
import recoder.RecorderType
import recoder.ScreenRecorder

class Main2Activity : AppCompatActivity() {

    val LOG_TAG = "Main2Activity"

    private lateinit var recordingButton : Button
    private lateinit var stopButton : Button


    private var mLatestFilepath: String? = null
    private var mRecoder: ScreenRecorder? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        setGUIComponents()

        mLatestFilepath = Utills.getFilePath(this)

        checkPermissions()

        mRecoder = RecorderFactory().buildRecoder(RecorderType.MEDIAPROTECTION ,this,mLatestFilepath)

        var setupResultValue:Int = mRecoder!!.setupRecoder()



    }

    private fun checkPermissions() {
        if(!Utills.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO))
        {
            ActivityCompat.requestPermissions(
                this,
                MainActivity.PERMISSIONS,
                MainActivity.PERMISSION_ALL
            )
        }

    }

    private fun setGUIComponents() {
        recordingButton = findViewById(R.id.recordingButton)
        stopButton = findViewById(R.id.stopButton)

        recordingButton.setOnClickListener {
            var startResultValue:Int = mRecoder!!.startRecoder()
        }

        stopButton.setOnClickListener {
            var stopResultValue:Int = mRecoder!!.stopRecoder()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissionsList: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissionsList, grantResults)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        mRecoder!!.onActivityResult(requestCode,resultCode,data)

    }

}
