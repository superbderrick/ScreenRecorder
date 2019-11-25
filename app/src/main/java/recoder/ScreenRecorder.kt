package recoder


abstract class ScreenRecorder(model: RecorderType, mediaType: MediaType) {

    var model: RecorderType? = null
    var mediaType: MediaType? = null

    init {
        this.model = model
        this.mediaType = mediaType
    }

    abstract fun construct()

    override fun toString(): String {
        return "CarModel - $model located in $mediaType"
    }
}
