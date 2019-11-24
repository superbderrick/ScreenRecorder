package supebderrick.github.screenrecorder

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Button
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var isRecording = false

    companion object {
        const val TAG = "zen8labs"
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

    private lateinit var recordingButton : Button
    private lateinit var mProjectionManager: MediaProjectionManager
    private lateinit var mMediaProjection: MediaProjection
    private lateinit var mVirtualDisplay: VirtualDisplay
    private lateinit var mMediaRecorder: MediaRecorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()

        recordingButton = findViewById(R.id.recordingButton)

        recordingButton.setOnClickListener {
            Log.d("derrick" , "Clicked")
        }
    }

    private fun initialize() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
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
        return true
    }

    private fun createVirtualDisplay(): VirtualDisplay {
        return mMediaProjection?.createVirtualDisplay(
            "MainActivity",
            DISPLAY_WIDTH,
            DISPLAY_HEIGHT,
            mScreenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mMediaRecorder?.surface, null /*Handler*/, null
        )
    }
}
