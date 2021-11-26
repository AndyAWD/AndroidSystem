package tw.com.andyawd.androidsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_result_contracts.*

class ResultContractsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_contracts)

        initClickListener()
    }

    private fun initClickListener() {
        arcMbSaberPage.setOnClickListener {
            val intent = Intent(this, SaberActivity::class.java).apply {
                this.putExtra(BaseConstants.NAME, "Arthur")
            }

            resultLauncher.launch(intent)
        }

        arcMbArcherPage.setOnClickListener {
            val intent = Intent(this, ArcherActivity::class.java).apply {
                this.putExtra(BaseConstants.NAME, "Emiya")
            }

            resultLauncher.launch(intent)
        }

        arcMbLancer.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(BaseConstants.NAME, "CuChulainn")
            bundle.putString(BaseConstants.GENDER, "male")

            lancerActivityResultContract.launch(bundle)
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (RESULT_OK == activityResult.resultCode) {

                val resultText = activityResult.data?.getStringExtra(BaseConstants.WEAPON)

                arcMTVLog.text = resultText
                Log.d("maho", "回傳: $resultText")
            }

            if (RESULT_CANCELED == activityResult.resultCode) {
                arcMTVLog.text = "無回傳"
            }
        }

    private val lancerActivityResultContract =
        registerForActivityResult(LancerActivityResultContract()) { weapon ->

            Log.d("maho", "回傳: $weapon")

            if (weapon.isNotEmpty()) {
                arcMTVLog.text = weapon
            } else {
                arcMTVLog.text = "無回傳"
            }
        }
}