package recoder

import java.sql.DriverManager.println


class MCScreenRecorder(mediaType: MediaType) : ScreenRecorder(RecorderType.MIDIACODEC, mediaType
    ) {
    init {
        construct()
    }

    override fun construct() {
        println("Created MCScreenRecorder")
    }
}