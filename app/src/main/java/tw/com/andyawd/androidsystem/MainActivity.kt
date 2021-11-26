package tw.com.andyawd.androidsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initClickListener()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.amMainFragment).navigateUp()
    }

    private fun initClickListener() {
        amMbResultContractsPage.setOnClickListener {
            val intent = Intent(this, ResultContractsActivity::class.java)

            resultLauncher.launch(intent)
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            Log.d("maho", "StartActivityForResult 回傳: $activityResult")
        }
}
