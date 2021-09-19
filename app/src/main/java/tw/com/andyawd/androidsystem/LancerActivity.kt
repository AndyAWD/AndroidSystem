package tw.com.andyawd.androidsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_lancer.*

class LancerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lancer)

        val bundle = intent.extras
        val name = bundle?.getString(BaseConstants.NAME)
        val gender = bundle?.getString(BaseConstants.GENDER)

        Log.d("maho", "姓名: $name / 性別：$gender")

        initClickListener()
    }

    private fun initClickListener() {
        alMbMain.setOnClickListener {

            val intent = Intent().apply {
                this.putExtra(BaseConstants.WEAPON, "Gae Bolg")
            }

            setResult(RESULT_OK, intent)
            finish()
        }
    }
}