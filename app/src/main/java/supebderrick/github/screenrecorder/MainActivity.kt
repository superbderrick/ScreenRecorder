package supebderrick.github.screenrecorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {

    lateinit var recordingButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recordingButton = findViewById(R.id.recordingButton)

        recordingButton.setOnClickListener {
            Log.d("derrick" , "Clicked")
        }

    }
}


