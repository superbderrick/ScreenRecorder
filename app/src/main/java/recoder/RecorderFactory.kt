package recoder

import android.content.Context


class RecorderFactory {
    fun buildRecoder(type: RecorderType , context: Context,filePath:String?): ScreenRecorder? {
        var screenRecorder: ScreenRecorder? = null

        when (type) {
            RecorderType.MEDIAPROTECTION -> screenRecorder =
                filePath?.let { ScreenRecoderFactory.buildRecoder(type,context, it) }

        }

        return screenRecorder

    }
}