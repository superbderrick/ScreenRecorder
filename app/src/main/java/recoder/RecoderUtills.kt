package recoder

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class RecoderUtills {

    companion object {
        fun hasPermissions(context: Context?, vararg permissions: String): Boolean = permissions.all {
            context?.let { it1 -> ActivityCompat.checkSelfPermission(it1, it) } == PackageManager.PERMISSION_GRANTED
        }
    }

}
