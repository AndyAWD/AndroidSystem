package tw.com.andyawd.androidsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_result_one.*

class ResultOneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_one)

        val system = intent.getStringExtra("system")
        Log.d("maho", "MainActivity帶過來的值: $system")

        initClickListener()
    }

    private fun initClickListener() {
        aroMbMain.setOnClickListener {

            val intent = Intent().apply {
                this.putExtra("result", "one")
            }

            setResult(RESULT_OK, intent)
            finish()
        }
    }
}