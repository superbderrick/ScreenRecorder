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
                       context: Context , filePath:String) : ScreenRecorder(RecorderType.MEDIAPROTECTION, context , filePath) {

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

        mContext = context

        initInternal()
    }


    private fun initInternal() {

        var errorForWindow = setupWindowDisPlay()

        var errorForMediaRecoder = setupMediaRecoder()

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

        // It has to make public variables

        try {
            mMediaRecorder?.prepare()
            isError = 0
        } catch (e: IOException) {
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

        (mContext as MainActivity).startActivityForResult(
            mProjectionManager?.createScreenCaptureIntent(),
            MDScreenRecorder.PERMISSION_CODE
        )
    }

    override fun setupRecoder(): Int {
        Log.d("Derrick" , "setupRecoder")
        return 0
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startRecoder(): Int {
        Log.d("Derrick" , "startRecoder")
        return 0
    }
}