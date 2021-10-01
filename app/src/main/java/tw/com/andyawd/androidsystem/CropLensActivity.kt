package tw.com.andyawd.androidsystem

import android.Manifest
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
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import java.io.File

class CropLensActivity : AppCompatActivity(), PermissionCallbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_lens)

        initClickListener()
    }

    private fun initClickListener() {
        aclMbCreatePackageNamePicture.setOnClickListener {

            val packageNameFile =
                File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "001.jpg")
            val uri = getPictureUri(packageNameFile)

            createPackageNameResultLauncher.launch(uri)
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

            createPhoneResultLauncher.launch(uri)
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

            createMediaStoreLauncher.launch(uri)
        }

        aclMbCreateVersionCheckPicture.setOnClickListener {
            startTakePicture()
        }
    }

    private fun startTakePicture() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {

            val permissionList = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            if (!EasyPermissions.hasPermissions(this, *permissionList)) {
                EasyPermissions.requestPermissions(
                    this,
                    "請提供讀寫檔案權限",
                    BaseConstants.READ_WRITE_PERMISSIONS,
                    *permissionList
                )
                return
            }


            if (!isCreateFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))) {
                Log.d("maho", "資料夾建立失敗")
                return
            }

            val phoneFile = File(
                Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_PICTURES}/AndroidSystem"),
                "004.jpg"
            )

            val uri = getPictureUri(phoneFile)

            createVersionCheckResultLauncher.launch(uri)

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValue = ContentValues().apply {
                this.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, "004.jpg")
                this.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg")
                this.put(
                    MediaStore.Images.ImageColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/AndroidSystem"
                )
            }

            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValue
            )

            createVersionCheckResultLauncher.launch(uri)
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

    private val createPackageNameResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isTakePicture ->
            Log.d("maho", "isTakePicture: $isTakePicture")

            if (!isTakePicture) {
                Log.d("maho", "拍照建立檔案失敗")
                return@registerForActivityResult
            }

            val packageNameFile =
                File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "001.jpg")

            MediaScannerConnection.scanFile(
                this,
                arrayOf(packageNameFile.toString()),
                null
            ) { _, _ ->

            }

            aclIvPackageNamePicture.setImageURI(getPictureUri(packageNameFile))
        }

    private val createPhoneResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isTakePicture ->
            Log.d("maho", "isTakePicture: $isTakePicture")

            if (!isTakePicture) {
                Log.d("maho", "拍照建立檔案失敗")
                return@registerForActivityResult
            }

            val phoneFile = File(
                Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_PICTURES}/AndroidSystem"),
                "002.jpg"
            )

            MediaScannerConnection.scanFile(this, arrayOf(phoneFile.toString()), null) { _, _ ->

            }

            aclIvPhonePicture.setImageURI(null)
            aclIvPhonePicture.setImageURI(getPictureUri(phoneFile))
        }

    private val createMediaStoreLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isTakePicture ->
            Log.d("maho", "isTakePicture: $isTakePicture")

            if (!isTakePicture) {
                Log.d("maho", "拍照建立檔案失敗")
                return@registerForActivityResult
            }

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

            MediaScannerConnection.scanFile(this, arrayOf(uri.toString()), null) { _, _ ->

            }

            aclIvMediaStorePicture.setImageURI(uri)
        }

    private val createVersionCheckResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isTakePicture ->
            Log.d("maho", "isTakePicture: $isTakePicture")

            if (!isTakePicture) {
                Log.d("maho", "拍照建立檔案失敗")
                return@registerForActivityResult
            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                val phoneFile = File(
                    Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_PICTURES}/AndroidSystem"),
                    "004.jpg"
                )

                MediaScannerConnection.scanFile(this, arrayOf(phoneFile.toString()), null) { _, _ ->

                }

                aclIvVersionCheckPicture.setImageURI(getPictureUri(phoneFile))
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val selection = "${MediaStore.Images.ImageColumns.DISPLAY_NAME} = '004.jpg'"
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

                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    pictureId
                )

                MediaScannerConnection.scanFile(this, arrayOf(uri.toString()), null) { _, _ ->

                }

                aclIvVersionCheckPicture.setImageURI(uri)
            }
        }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d("maho", "Granted requestCode: $requestCode / perms: $perms")

        if (BaseConstants.READ_WRITE_PERMISSIONS == requestCode) {
            startTakePicture()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        PermissionsDeniedAlertDialog(this).show(
            resources.getString(R.string.permissions_denied),
            resources.getString(
                R.string.permissions_setting_open,
                PermissionsTransformer().getPermissionsList(perms)
            )
        )

        PermissionsDeniedAlertDialog(this).show(
            "權限不足",
            "請開啟以下權限：\n${PermissionsTransformer().getPermissionsList(perms)}"
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}