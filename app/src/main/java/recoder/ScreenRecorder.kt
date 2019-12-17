package recoder

import android.content.Context
import android.content.Intent


abstract class ScreenRecorder(recoderType: RecorderType,context: Context ,recordFilePath : String) {

    var recoderType: RecorderType? = null
    var context: Context? = null
    var filePath: String? = null

    init {
        this.recoderType = recoderType
        this.context = context
        this.filePath = recordFilePath
    }


    abstract fun setupRecorder(): Int

    abstract fun startRecorder(): Int

    abstract fun stopRecorder(): Int

    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)


}
