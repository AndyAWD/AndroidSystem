package tw.com.andyawd.androidsystem

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

class PermissionsDeniedAlertDialog(val context: Context) {

    companion object {
        const val PACKAGE = "package"
        const val STRING_EMPTY = ""
    }

    fun show(title: String = STRING_EMPTY, message: String = STRING_EMPTY) {
        AlertDialog
            .Builder(context)
            .setTitle(title)
            .setCancelable(false)
            .setMessage(message)
            .setPositiveButton(R.string.confirm) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    val uri = Uri.fromParts(PACKAGE, context.packageName, null)
                    this.data = uri
                }

                context.startActivity(intent)
            }
            .show()
    }
}