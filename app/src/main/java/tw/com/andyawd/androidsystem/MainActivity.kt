package tw.com.andyawd.androidsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initClickListener()
    }

    private fun initClickListener() {
        amMbResultOne.setOnClickListener {

            val intent = Intent(this, ResultOneActivity::class.java).apply {
                this.putExtra("system", "android")
            }

            resultOneLauncher.launch(intent)
        }
    }

    private val resultOneLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (RESULT_OK == activityResult.resultCode) {
                Log.d(
                    "maho",
                    "ResultOneActivity帶過來的值: ${activityResult.data?.getStringExtra("result")}"
                )
            }
        }
}

