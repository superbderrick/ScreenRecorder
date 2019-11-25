package recoder

import android.content.Context


abstract class ScreenRecorder(recoderType: RecorderType,context: Context) {

    var recoderType: RecorderType? = null
    var context: Context? = null

    init {
        this.recoderType = recoderType
        this.context = context
    }

    abstract fun construct()

    override fun toString(): String {
        return "Recoder Type - $recoderType located "
    }
}
