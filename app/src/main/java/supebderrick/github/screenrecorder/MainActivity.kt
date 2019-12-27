package supebderrick.github.screenrecorder

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import recoder.RecorderFactory
import recoder.RecorderType
import recoder.ScreenRecorder

class MainActivity : AppCompatActivity() {

    private companion object {
        val LOG_TAG = "MainActivity"
        var PERMISSION_ALL = 1
        var PERMISSIONS = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO
        )
        var errorCode : Int? = null
    }

    private lateinit var recordingButton : Button
    private lateinit var stopButton : Button


    private var mLatestFilepath: String? = null
    private var mRecorder: ScreenRecorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setGUIComponents()

        mLatestFilepath = Utills.getFilePath(this)

        checkPermissions()

        mRecorder = RecorderFactory().buildRecoder(RecorderType.MEDIAPROTECTION ,this,mLatestFilepath)

        errorCode = mRecorder?.setupRecorder()


        Log.d(LOG_TAG , "onCreate is called tried to setup a recorder with result :  $errorCode ")

    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(LOG_TAG , "onActivityResultt requestCode :  $requestCode ")
        Log.d(LOG_TAG , "onActivityResultt resultCode :  $resultCode ")

        if(requestCode == 100) {
            if (resultCode != RESULT_OK) {

                // User didn't allow to accept a permission
                // Later need to exception code,

                return;
            }

            mRecorder?.onActivityResult(requestCode,resultCode,data)
        }



    }

    public override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.d(LOG_TAG , "onRequestPermissionsResult :   ")
    }

    private fun setGUIComponents() {
        recordingButton = findViewById(R.id.recordingButton)
        stopButton = findViewById(R.id.stopButton)

        recordingButton.setOnClickListener {
            var startResultValue:Int? = mRecorder?.startRecorder()
        }

        stopButton.setOnClickListener {
            var stopResultValue:Int? = mRecorder?.stopRecorder()
        }

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
}
