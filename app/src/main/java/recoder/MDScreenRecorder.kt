package recoder

import android.app.Activity
import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.core.app.ActivityCompat.startActivityForResult
import supebderrick.github.screenrecorder.MainActivity
import supebderrick.github.screenrecorder.R
import java.io.IOException


class MDScreenRecorder(mediaType: MediaType,
                       context: Context) : ScreenRecorder(RecorderType.MEDIAPROTECTION, context) {

    private companion object 
    {
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

    private var mMediaRecorder: MediaRecorder? = null
    private var mContext: Context? = null
    private var mProjectionManager: MediaProjectionManager? = null
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    

    init {
        construct()
    }

    override fun construct() {
        initInternal()
    }

    private fun initInternal() {
        setupContext()
    }

    private fun setupContext() {
            mContext = context
    }

    private fun getDisPlay() {
        val metrics = DisplayMetrics()
        var windowManager : WindowManager = mContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        windowManager.defaultDisplay.getMetrics(metrics)
        MainActivity.mScreenDensity = metrics.densityDpi
        mProjectionManager = mContext?.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
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

        
        //mMediaRecorder?.setOutputFile(mLatestFilepath)

        try {
            mMediaRecorder?.prepare()
        } catch (e: IOException) {
            mMediaRecorder = null
            return false
        }

        return true
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

        (mContext as MainActivity).startActivityForResult(
            mProjectionManager?.createScreenCaptureIntent(),
            MDScreenRecorder.PERMISSION_CODE
        )
    }
}