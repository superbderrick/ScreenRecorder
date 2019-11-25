package recoder

import android.content.Context

internal object ScreenRecoderFactory {
    fun buildRecoder(model: RecorderType ,context: Context): ScreenRecorder? {
        var screenRecorder: ScreenRecorder? = null
        when (model) {
            RecorderType.MEDIAPROTECTION -> screenRecorder = MDScreenRecorder(MediaType.VIDEOONLY,context)

            else -> {
            }
        }
        return screenRecorder
    }
}