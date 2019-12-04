package supebderrick.github.screenrecorder

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import recoder.RecorderFactory
import recoder.RecorderType
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var isRecording = false

    companion object {
        const val TAG = "screenRecoder"
        const val LIST_ITEM_REQUEST_CODE = 101
        const val PERMISSION_CODE = 1
        const val DISPLAY_WIDTH = 480
        const val DISPLAY_HEIGHT = 640
        var mScreenDensity: Int = 0
        var PERMISSION_ALL = 1

        var PERMISSIONS = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO
        )
    }

    private var mLatestFilepath: String? = null

    private var mProjectionManager: MediaProjectionManager? = null

    private lateinit var recordingButton : Button
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private lateinit var mMediaProjectionCallback: MediaProjectionCallback
    private var mMediaRecorder: MediaRecorder? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()

        setupGUIComponents()

        checkPermissions()

        mLatestFilepath = Utills.getFilePath(this)

        val mpRecorder = RecorderFactory().buildRecoder(RecorderType.MEDIAPROTECTION ,this,mLatestFilepath)
        mpRecorder?.setupRecoder()
        mpRecorder?.startRecoder()

    }

    private fun setupGUIComponents() {
        recordingButton = findViewById(R.id.recordingButton)

        var stopRecording = false

        recordingButton.setOnClickListener {

            Log.d("derrick" , "real Value :  $isRecording" );
            if(isRecording) {
                stopRecordingScreen()
                stopRecording = false
            } else {
                requestStartRecordingScreen()
                stopRecording = true
            }

            changeButtonText(stopRecording)

        }

    }

    private fun changeButtonText(stopRecording:Boolean)
    {
        if(stopRecording) {
            recordingButton.setText(R.string.button_recording)
        } else {
            recordingButton.setText(R.string.button_start)
        }
    }

    private fun checkPermissions() {
        if(!Utills.hasPermissions(this,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO))
        {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    PERMISSION_ALL
            )
        }

    }

    private fun initialize() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        mScreenDensity = metrics.densityDpi
        mProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    private fun requestStartRecordingScreen() {
        val initMedia = prepareMediaRecorder()

        if (!initMedia) {
            Log.w(TAG, getString(R.string.init_error_media_recorder))
            return
        }

        if (mMediaProjection == null) {
            startActivityForResult(
                mProjectionManager?.createScreenCaptureIntent(),
                PERMISSION_CODE
            )
            return
        }

        mVirtualDisplay = createVirtualDisplay()


    }

    private fun showDialogPermission() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle(R.string.permission_denied)
        builder.setMessage(R.string.permission_denied_message)

        builder.setPositiveButton("OK") { _, _ ->
            finish()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissionsList: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_ALL -> {
                if (grantResults.isNotEmpty()) {
                    var permissionsDenied = 0
                    for (per in grantResults) {
                        if (per == PackageManager.PERMISSION_DENIED) {
                            permissionsDenied += 1
                        }
                    }

                    if (permissionsDenied > 0) {
                        showDialogPermission()
                    }
                }
                return
            }
        }

    }

    private fun prepareMediaRecorder(): Boolean {

        mMediaRecorder = MediaRecorder()
        mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mMediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mMediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mMediaRecorder?.setVideoEncodingBitRate(512 * 1000)
        mMediaRecorder?.setVideoFrameRate(30)
        mMediaRecorder?.setVideoSize(
                DISPLAY_WIDTH,
                DISPLAY_HEIGHT
        )

        mLatestFilepath = Utills.getFilePath(this)
        mMediaRecorder?.setOutputFile(mLatestFilepath)

        try {
            mMediaRecorder?.prepare()
        } catch (e: IOException) {
            Log.w(TAG, getString(R.string.record_error))
            mMediaRecorder = null
            return false
        }

        return true
    }

    private fun createVirtualDisplay(): VirtualDisplay? {
        return mMediaProjection?.createVirtualDisplay(
                "MainActivity",
                DISPLAY_WIDTH,
                DISPLAY_HEIGHT,
                mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder?.surface, null /*Handler*/, null
        )
    }


     public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != PERMISSION_CODE) {
            Log.e(TAG, "Unknown request code: $requestCode")
            return
        }
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(
                    this,
                    getString(R.string.screen_cast_denied), Toast.LENGTH_SHORT
            ).show()

            showDialogPermission()
            return
        }

        initializeMediaProjection(resultCode, data!!)

        startRecording()
    }

    private fun initializeMediaProjection(resultCode: Int, data: Intent) {
        mMediaProjectionCallback = MediaProjectionCallback()

        mMediaProjection = mProjectionManager?.getMediaProjection(resultCode, data)
        mMediaProjection?.registerCallback(mMediaProjectionCallback, null)
    }

    private fun startRecording() {
        mVirtualDisplay = createVirtualDisplay()

        try {
            mMediaRecorder?.start()
            isRecording = true
        } catch (e: RuntimeException) {
            Log.d(TAG, getString(R.string.start_record_fail))
            finish()
        }
    }
    private fun stopRecordingScreen() {
        try {
            mMediaRecorder?.stop()
        } catch (e: Exception) {

        } finally {
            mMediaRecorder?.release()
            mMediaRecorder = null
        }

        try {
            mMediaProjection?.stop()
            mVirtualDisplay?.release()
        } catch (e: java.lang.Exception) {
            Log.d(TAG, getString(R.string.stop_record_fail))
        } finally {
            mMediaProjection = null
            mVirtualDisplay = null
        }
        isRecording = false

        Log.d(TAG, getString(R.string.stop_record_success))
    }

    inner class MediaProjectionCallback : MediaProjection.Callback() {
        override fun onStop() {

            Log.i(TAG, getString(R.string.media_stopped))
        }
    }
}
