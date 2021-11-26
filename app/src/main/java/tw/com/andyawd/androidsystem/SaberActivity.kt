package tw.com.andyawd.androidsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_saber.*

class SaberActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saber)

        val name = intent.getStringExtra(BaseConstants.NAME)

        asMTVLog.text = name
        Log.d("maho", "姓名: $name")

        initClickListener()
    }

    private fun initClickListener() {
        asMbMain.setOnClickListener {

            val intent = Intent().apply {
                this.putExtra(BaseConstants.WEAPON, "Excalibur")
            }

            setResult(RESULT_OK, intent)
            finish()
        }
    }
}