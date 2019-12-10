package recoder

import android.content.Context


abstract class ScreenRecorder(recoderType: RecorderType,context: Context ,recordFilePath : String) {

    var recoderType: RecorderType? = null
    var context: Context? = null
    var filePath: String? = null

    init {
        this.recoderType = recoderType
        this.context = context
        this.filePath = recordFilePath
    }



    abstract fun setupRecoder(): Int

    abstract fun startRecoder(): Int




}
