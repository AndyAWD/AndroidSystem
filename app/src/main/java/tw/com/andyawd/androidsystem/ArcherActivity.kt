package tw.com.andyawd.androidsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_archer.*

class ArcherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archer)

        val name = intent.getStringExtra(BaseConstants.NAME)

        aaMTVLog.text = name
        Log.d("maho", "姓名: $name")

        initClickListener()
    }

    private fun initClickListener() {
        aaMbMain.setOnClickListener {

            val intent = Intent().apply {
                this.putExtra(BaseConstants.WEAPON, "Unlimited Blade Works")
            }

            setResult(RESULT_OK, intent)
            finish()
        }
    }
}