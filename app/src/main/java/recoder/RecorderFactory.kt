package recoder


class RecorderFactory {
    fun buildRecoder(type: RecorderType): ScreenRecorder? {
        var screenRecorder: ScreenRecorder? = null

        val location = MediaType.AUDIOONLY

        when (location) {
            MediaType.VIDEOONLY -> screenRecorder = VideoFactory.buildRecoder(type)

            MediaType.AUDIOONLY -> screenRecorder = AudioFactory().buildRecoder(type)

            else -> screenRecorder = buildRecoder(type)
        }

        return screenRecorder

    }
}