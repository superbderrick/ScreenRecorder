package recoder

import android.app.Activity
import android.content.Context


class MDScreenRecorder(mediaType: MediaType,
                       context: Context) : ScreenRecorder(RecorderType.MEDIAPROTECTION, context) {
    init {
        construct()
    }

    override fun construct() {

    }

    private fun testValue() {

    }
}