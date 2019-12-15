package supebderrick.github.screenrecorder

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import kotlinx.android.synthetic.main.activity_main2.*
import recoder.RecorderFactory
import recoder.RecorderType
import recoder.ScreenRecorder

class Main2Activity : AppCompatActivity() {

     val LOGTAG = "Main2Activity"

    private var mLatestFilepath: String? = null
    private var mRecoder: ScreenRecorder? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        mLatestFilepath = Utills.getFilePath(this)

        checkPermissions()

        mRecoder = RecorderFactory().buildRecoder(RecorderType.MEDIAPROTECTION ,this,mLatestFilepath)

        var setupResultValue:Int = mRecoder!!.setupRecoder()
        var startResultValue:Int = mRecoder!!.startRecoder()

        Log.d(LOGTAG , "resultValue : $setupResultValue")
        Log.d(LOGTAG , "startValue : $startResultValue")

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

    override fun onRequestPermissionsResult(requestCode: Int, permissionsList: Array<String>, grantResults: IntArray) {
        Log.d(LOGTAG , "onRequestPermissionsResult")
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        mRecoder!!.onActivityResult(requestCode,resultCode,data)

        Log.d(LOGTAG , "onRequestPermissionsResult")
    }

}
