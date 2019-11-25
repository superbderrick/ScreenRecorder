package recoder

import android.content.Context


class RecorderFactory {
    fun buildRecoder(type: RecorderType , context: Context): ScreenRecorder? {
        var screenRecorder: ScreenRecorder? = null

        when (type) {
            RecorderType.MEDIAPROTECTION -> screenRecorder = ScreenRecoderFactory.buildRecoder(type,context)

        }

        return screenRecorder

    }
}