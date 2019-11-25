package recoder

class AudioFactory {
    fun buildRecoder(model: RecorderType): ScreenRecorder? {
        var screenRecorder: ScreenRecorder? = null
        when (model) {
            RecorderType.MEDIAPROTECTION -> screenRecorder = MDScreenRecorder(MediaType.DEFAULT)

            RecorderType.MIDIACODEC -> screenRecorder = MCScreenRecorder(MediaType.AUDIOONLY)


            else -> {
            }
        }
        return screenRecorder
    }
}