package recoder

import android.app.Activity
import android.content.Context
import android.media.MediaRecorder
import android.media.projection.MediaProjectionManager
import android.util.DisplayMetrics
import android.view.WindowManager
import supebderrick.github.screenrecorder.MainActivity


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
}