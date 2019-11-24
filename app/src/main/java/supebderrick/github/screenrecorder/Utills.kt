package supebderrick.github.screenrecorder

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class Utills {
    companion object {
        fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}