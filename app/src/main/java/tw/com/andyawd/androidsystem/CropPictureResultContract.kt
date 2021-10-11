package tw.com.andyawd.androidsystem

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract

class CropPictureResultContract : ActivityResultContract<Intent, Uri>() {
    override fun createIntent(context: Context, input: Intent): Intent {
        val intent = Intent("com.android.camera.action.CROP").apply {
//
//            this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            this.setDataAndType(input.pictureUri, input.type)
//            this.putExtra(MediaStore.EXTRA_OUTPUT, input.cropUri)
//            this.putExtra("crop", input.crop)
//            this.putExtra("return-data", false)
//            this.putExtra("outputFormat", input.outputFormat)
//            this.putExtra("scale", true)
        }

        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        Log.d("maho", "resultCode: $resultCode / intent: $intent")
        return intent?.data
    }
}