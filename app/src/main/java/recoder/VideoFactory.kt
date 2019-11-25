package recoder

internal object VideoFactory {
    fun buildRecoder(model: RecorderType): ScreenRecorder? {
        var screenRecorder: ScreenRecorder? = null
        when (model) {
            RecorderType.MEDIAPROTECTION -> screenRecorder = MDScreenRecorder(MediaType.VIDEOONLY)

            RecorderType.MIDIACODEC -> screenRecorder = MCScreenRecorder(MediaType.VIDEOONLY)

            else -> {
            }
        }
        return screenRecorder
    }
}