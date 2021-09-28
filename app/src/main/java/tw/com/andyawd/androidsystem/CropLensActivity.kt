package tw.com.andyawd.androidsystem

import android.content.ContentUris
import android.content.ContentValues
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_crop_lens.*
import java.io.File

class CropLensActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_lens)

        aclMbCreatePackageNamePicture.setOnClickListener {

            val packageNameFile =
                File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "001.jpg")
            val uri = getPictureUri(packageNameFile)

            takePictureResultLauncher.launch(uri)
        }

        aclMbCreatePhonePicture.setOnClickListener {

            if (!isCreateFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))) {
                Log.d("maho", "資料夾建立失敗")
                return@setOnClickListener
            }

            val phoneFile = File(
                Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_PICTURES}/AndroidSystem"),
                "002.jpg"
            )

            val uri = getPictureUri(phoneFile)

            takePictureResultLauncher.launch(uri)
        }

        aclMbCreateMediaStorePicture.setOnClickListener {
            val contentValue = ContentValues().apply {
                this.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, "003.jpg")
                this.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    this.put(
                        MediaStore.Images.ImageColumns.RELATIVE_PATH,
                        "${Environment.DIRECTORY_PICTURES}/AndroidSystem"
                    )
                }
            }

            val uri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValue)

            takePictureResultLauncher.launch(uri)
        }
    }

    private fun getPictureUri(picturePath: File): Uri? {
        return FileProvider.getUriForFile(
            this,
            "$packageName${BaseConstants.DOT_FILEPROVIDER}",
            picturePath
        )
    }

    private fun isCreateFolder(folderPath: File): Boolean {
        val file = File(folderPath, "AndroidSystem")

        return if (file.exists()) {
            true
        } else file.mkdir()
    }

    private val takePictureResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isTakePicture ->

            if (!isTakePicture) {
                Log.d("maho", "拍照建立檔案失敗")
                return@registerForActivityResult
            }

            val packageNameFile =
                File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "001.jpg")

            val phoneFile = File(
                Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_PICTURES}/AndroidSystem"),
                "002.jpg"
            )

            MediaScannerConnection.scanFile(
                this,
                arrayOf(packageNameFile.toString(), phoneFile.toString()),
                null
            ) { path, uri ->
                Log.d("maho", "${Thread.currentThread().name}  path: $path / uri: $uri")

            }

            aclIvPackageNamePicture.setImageURI(getPictureUri(packageNameFile))

            aclIvPhonePicture.setImageURI(null)
            aclIvPhonePicture.setImageURI(getPictureUri(phoneFile))


            val selection = "${MediaStore.Images.ImageColumns.DISPLAY_NAME} = '003.jpg'"
            val orderBy = "${MediaStore.Images.ImageColumns.DATE_ADDED} DESC"

            val uriQuery = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                selection,
                null,
                orderBy
            ) ?: return@registerForActivityResult

            uriQuery.moveToFirst()

            val pictureId =
                uriQuery.getLong(uriQuery.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            val uri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, pictureId)

            aclIvMediaStorePicture.setImageURI(uri)
        }
}