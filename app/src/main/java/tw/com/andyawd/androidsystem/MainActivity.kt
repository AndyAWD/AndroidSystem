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
        amMbSaber.setOnClickListener {
            val intent = Intent(this, SaberActivity::class.java).apply {
                this.putExtra(BaseConstants.NAME, "Arthur")
            }

            resultLauncher.launch(intent)
        }

        amMbArcher.setOnClickListener {
            val intent = Intent(this, ArcherActivity::class.java).apply {
                this.putExtra(BaseConstants.NAME, "Emiya")
            }

            resultLauncher.launch(intent)
        }

        amMbCreateDocument.setOnClickListener {
            createDocumentResultLauncher.launch("saberEat.jpg")
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (RESULT_OK == activityResult.resultCode) {
                Log.d(
                    "maho",
                    "回傳: ${activityResult.data?.getStringExtra(BaseConstants.WEAPON)}"
                )
            }
        }

    private val createDocumentResultLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
            Log.d("maho", "回傳: $uri")
        }
}

