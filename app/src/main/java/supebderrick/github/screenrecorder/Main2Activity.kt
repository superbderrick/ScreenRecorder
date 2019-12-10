package supebderrick.github.screenrecorder

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main2.*
import recoder.RecorderFactory
import recoder.RecorderType
import recoder.ScreenRecorder

class Main2Activity : AppCompatActivity() {

    private var mLatestFilepath: String? = null
    private var mRecoder: ScreenRecorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        mLatestFilepath = Utills.getFilePath(this)

        mRecoder = RecorderFactory().buildRecoder(RecorderType.MEDIAPROTECTION ,this,mLatestFilepath)

        mRecoder?.setupRecoder()


        fab.setOnClickListener { view ->

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissionsList: Array<String>, grantResults: IntArray) {


    }


}
