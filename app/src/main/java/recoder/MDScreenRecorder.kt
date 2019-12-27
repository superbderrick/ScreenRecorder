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
import java.io.IOException


class MDScreenRecorder(mediaType: MediaType,
                       context: Context , filePath:String) : ScreenRecorder(RecorderType.MEDIAPROTECTION, context , filePath) {

    private companion object 
    {
        const val LOG_TAG = "MDScreenRecorder"

    }

    private var mMediaRecorder: MediaRecorder? = null
    private var mContext: Context? = null
    private var mFilePath: String? = null
    private var mProjectionManager: MediaProjectionManager? = null
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private lateinit var mMediaProjectionCallback: MDScreenRecorder.MediaProjectionCallback

    private var mScreenDencity: Int = 0
    

    init {
        mContext = context
        mFilePath = filePath

        Log.d(LOG_TAG , "Constructor is called ")
    }


    private fun requestStartRecordingScreen() {

        (mContext as Activity).startActivityForResult(
            mProjectionManager?.createScreenCaptureIntent(),
            RecorderConfig.PERMISSION_CODE
        )
    }

    private fun initializeMediaProjection(resultCode: Int, data: Intent) {
        mMediaProjectionCallback = MediaProjectionCallback()

        mMediaProjection = mProjectionManager?.getMediaProjection(resultCode, data)
        mMediaProjection?.registerCallback(mMediaProjectionCallback, null)
    }

    private fun initInternal() : Int {
        var isError = 0

        isError = checkFilePathAndContext()

        if(isError == 1)
            return isError

        isError = setupWindowDisPlay()

        if(isError == 1)
            return isError

        isError = checkPermissions()
        if(isError == 1)

            return isError

        isError = setupMediaRecorder()

        if(isError == 1)
            return isError

        Log.d(LOG_TAG , "InitInternal done with Result :  $isError ")

        return isError
    }

    private fun checkFilePathAndContext() :Int {
        var isError = 0

        if(mFilePath == null) {
            isError = 1
        }

        if(mContext == null) {
            isError = 1
        }

        Log.d(LOG_TAG , "checkFilePathAndContext done with Result :  $isError ")

        return  isError
    }

    private fun setupWindowDisPlay() : Int {
        var isError = 0

        val metrics = DisplayMetrics()
        val windowManager : WindowManager = mContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        windowManager?.defaultDisplay.getMetrics(metrics)

        mScreenDencity = metrics.densityDpi

        mProjectionManager = mContext?.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        if(mProjectionManager != null && metrics != null) {

            isError = 0
        } else {
            isError = 1
        }

        Log.d(LOG_TAG , "setupWindowDisPlay done with Result :  $isError ")

        return isError

    }

    private fun checkPermissions() :Int {
        var isError = 0

        if(mContext?.let {
                RecoderUtills.hasPermissions(
                    it, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO)
            }!!)
        {
            isError = 0
        } else {
            isError = 1
        }

        Log.d(LOG_TAG , "checkPermissions done with Result :  $isError ")

        return isError
    }

    private fun setupMediaRecorder() : Int {
        var isError = 0

        if(mFilePath != null) {
            mMediaRecorder = MediaRecorder()
            mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mMediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)
            mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mMediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mMediaRecorder?.setVideoEncodingBitRate(512 * 1000)
            mMediaRecorder?.setVideoFrameRate(30)
            mMediaRecorder?.setVideoSize(
                RecorderConfig.DISPLAY_WIDTH,
                RecorderConfig.DISPLAY_HEIGHT
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

        } else {
            isError = 1
        }

        Log.d(LOG_TAG , "setupMediaRecorder done with Result :  $isError ")

        return isError
    }


    override fun setupRecorder(): Int {
        var isError = 0

        isError = initInternal()

        Log.d(LOG_TAG , "setupRecorder after init Internal :  $isError")

        return isError
    }

    override fun startRecorder(): Int {
        var isError = 0

        requestStartRecordingScreen()

        return isError
    }

    override fun stopRecorder(): Int {
        var isError = 0

        stopRecordingScreen()

        return isError
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode != RecorderConfig.PERMISSION_CODE) {
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
            RecorderConfig.DISPLAY_WIDTH,
            RecorderConfig.DISPLAY_HEIGHT,
            mScreenDencity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mMediaRecorder?.surface, null /*Handler*/, null
        )
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

        } finally {
            mMediaProjection = null
            mVirtualDisplay = null
        }
    }


    private inner class MediaProjectionCallback : MediaProjection.Callback() {
        override fun onStop() {

        }
    }

}

