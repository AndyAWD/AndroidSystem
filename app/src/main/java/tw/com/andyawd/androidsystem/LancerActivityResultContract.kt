package tw.com.andyawd.androidsystem

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract

class LancerActivityResultContract : ActivityResultContract<Bundle, String>() {
    override fun createIntent(context: Context, input: Bundle): Intent {
        return Intent(context, LancerActivity::class.java).apply {
            putExtras(input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String {
        val weapon = intent?.getStringExtra(BaseConstants.WEAPON)

        return if (Activity.RESULT_OK == resultCode && null != weapon) {
            weapon
        } else {
            BaseConstants.STRING_EMPTY
        }
    }
}