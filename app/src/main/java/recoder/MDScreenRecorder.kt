package recoder

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import supebderrick.github.screenrecorder.MainActivity
import supebderrick.github.screenrecorder.R
import supebderrick.github.screenrecorder.Utills
import java.io.IOException


class MDScreenRecorder(mediaType: MediaType,
                       context: Context , filePath:String) : ScreenRecorder(RecorderType.MEDIAPROTECTION, context , filePath) {

    private companion object 
    {
        const val LOG_TAG = "MDScreenRecorder"
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

    private var mMediaRecorder: MediaRecorder? = null
    private var mContext: Context? = null
    private var mFilePath: String? = null
    private var mProjectionManager: MediaProjectionManager? = null
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private lateinit var mMediaProjectionCallback: MDScreenRecorder.MediaProjectionCallback
    

    init {

        mContext = context
        mFilePath = filePath

        initInternal()
    }


    private fun initInternal() {
        var errorForWindow = setupWindowDisPlay()
    }

    private fun setupWindowDisPlay() : Int {
        var isError = 0

        val metrics = DisplayMetrics()
        val windowManager : WindowManager = mContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        windowManager?.defaultDisplay.getMetrics(metrics)

        MDScreenRecorder.mScreenDensity = metrics.densityDpi

        mProjectionManager = mContext?.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

       if(mProjectionManager != null && metrics != null) {

           isError = 0
       } else {
           isError = 1
       }

       return isError

    }

    private fun setupMediaRecoder() : Int {
        var isError = 0

        mMediaRecorder = MediaRecorder()
        mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mMediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mMediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mMediaRecorder?.setVideoEncodingBitRate(512 * 1000)
        mMediaRecorder?.setVideoFrameRate(30)
        mMediaRecorder?.setVideoSize(
            MDScreenRecorder.DISPLAY_WIDTH,
            MDScreenRecorder.DISPLAY_HEIGHT
        )

        mMediaRecorder?.setOutputFile(mFilePath)

        try {
            mMediaRecorder?.prepare()
            isError = 0
        } catch (e: IOException) {
            Log.d(LOG_TAG , "MediaRecoder failure ${e.message} " )
            mMediaRecorder = null
            isError = 1
        }

        return isError
    }



    private fun createVirtualDisplay(): VirtualDisplay? {
        return mMediaProjection?.createVirtualDisplay(
            "MainActivity",
            MDScreenRecorder.DISPLAY_WIDTH,
            MDScreenRecorder.DISPLAY_HEIGHT,
            MDScreenRecorder.mScreenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mMediaRecorder?.surface, null /*Handler*/, null
        )
    }

    private fun requestStartRecordingScreen() {

        (mContext as Activity).startActivityForResult(
            mProjectionManager?.createScreenCaptureIntent(),
            MDScreenRecorder.PERMISSION_CODE
        )
    }

    private fun initializeMediaProjection(resultCode: Int, data: Intent) {
        mMediaProjectionCallback = MediaProjectionCallback()

        mMediaProjection = mProjectionManager?.getMediaProjection(resultCode, data)
        mMediaProjection?.registerCallback(mMediaProjectionCallback, null)
    }


    override fun setupRecoder(): Int {
        var isError = 1

        if(mContext?.let {
                RecoderUtills.hasPermissions(
                    it, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO)
            }!!) {
            isError = setupMediaRecoder()
        }

        return isError
    }

    override fun startRecoder(): Int {
        var isError = 0

        requestStartRecordingScreen()

        return isError
    }

    override fun stopRecoder(): Int {
        var isError = 0

        return isError
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode != MDScreenRecorder.PERMISSION_CODE) {
            Log.e(MDScreenRecorder.LOG_TAG, "Unknown request code: $requestCode")
            return
        }

        if (resultCode != Activity.RESULT_OK) {

            return
        }

        initializeMediaProjection(resultCode, data!!)

        startRecording()

    }

    private fun startRecording() {
        mVirtualDisplay = requestVTDisplay()

        try {
            mMediaRecorder?.start()
        } catch (e: RuntimeException) {

        }
    }

    private fun requestVTDisplay(): VirtualDisplay? {
        return mMediaProjection?.createVirtualDisplay(
            "MDScreenRecorder",
            MDScreenRecorder.DISPLAY_WIDTH,
            MDScreenRecorder.DISPLAY_HEIGHT,
            MDScreenRecorder.mScreenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mMediaRecorder?.surface, null /*Handler*/, null
        )
    }


    private inner class MediaProjectionCallback : MediaProjection.Callback() {
        override fun onStop() {

        }
    }

}

