package recoder

import java.sql.DriverManager.println

class MDScreenRecorder(mediaType: MediaType) : ScreenRecorder(RecorderType.MEDIAPROTECTION, mediaType) {
    init {
        construct()
    }

    override fun construct() {
        println("Created MDRecorder")
    }
}