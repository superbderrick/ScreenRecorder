package recoder

import android.content.Context

internal object ScreenRecoderFactory {
    fun buildRecoder(type: RecorderType ,context: Context , filePath:String): ScreenRecorder? {
        var screenRecorder: ScreenRecorder? = null
        when (type) {
            RecorderType.MEDIAPROTECTION -> screenRecorder = MDScreenRecorder(MediaType.VIDEOONLY,context,filePath)

            else -> {
            }
        }
        return screenRecorder
    }
}