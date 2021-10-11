package tw.com.andyawd.androidsystem

import android.Manifest
import android.content.ClipData
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.edit
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

        aclMbCropPicture.setOnClickListener {
            startTakePictureWithCrop()
        }
    }

    private fun startTakePicture() {

        var uri: Uri? = null

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
                return
            }

            val phoneFile = File(
                Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_PICTURES}/AndroidSystem"),
                "004.jpg"
            )

            uri = getPictureUri(phoneFile)
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

            uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValue
            )
        }

        createVersionCheckResultLauncher.launch(uri)
    }

    private fun startTakePictureWithCrop() {

        var uri: Uri? = null
        val pictureName = "005_${System.currentTimeMillis()}.jpg"

        val sharedPreferences = getSharedPreferences(BaseConstants.ANDROID_SYSTEM, MODE_PRIVATE)
        sharedPreferences.edit {
            this.putString(BaseConstants.PICTURE_NAME, pictureName)
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            val permissionList = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            if (!EasyPermissions.hasPermissions(this, *permissionList)) {
                EasyPermissions.requestPermissions(
                    this,
                    "請提供讀寫檔案權限",
                    BaseConstants.CROP_PERMISSIONS,
                    *permissionList
                )
                return
            }

            if (!isCreateFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))) {

                return
            }

            val phoneFile = File(
                Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_PICTURES}/AndroidSystem"),
                pictureName
            )

            uri = getPictureUri(phoneFile)

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValue = ContentValues().apply {
                this.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, pictureName)
                this.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg")
                this.put(
                    MediaStore.Images.ImageColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/AndroidSystem"
                )
            }

            uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValue
            )
        }


        takePictureWithCropResultLauncher.launch(uri)
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

            if (!isTakePicture) {

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

            aclIvPicturePreview.setImageURI(getPictureUri(packageNameFile))
        }

    private val createPhoneResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isTakePicture ->

            if (!isTakePicture) {
                return@registerForActivityResult
            }

            val phoneFile = File(
                Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_PICTURES}/AndroidSystem"),
                "002.jpg"
            )

            MediaScannerConnection.scanFile(this, arrayOf(phoneFile.toString()), null) { _, _ ->

            }

            aclIvPicturePreview.setImageURI(getPictureUri(phoneFile))
        }

    private val createMediaStoreLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isTakePicture ->

            if (!isTakePicture) {

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

            aclIvPicturePreview.setImageURI(uri)
        }

    private val createVersionCheckResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isTakePicture ->

            if (!isTakePicture) {

                return@registerForActivityResult
            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                val phoneFile = File(
                    Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_PICTURES}/AndroidSystem"),
                    "004.jpg"
                )

                MediaScannerConnection.scanFile(this, arrayOf(phoneFile.toString()), null) { _, _ ->

                }



                aclIvPicturePreview.setImageURI(getPictureUri(phoneFile))
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

                aclIvPicturePreview.setImageURI(uri)
            }
        }

    private val takePictureWithCropResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isTakePicture ->

            if (!isTakePicture) {
                return@registerForActivityResult
            }

            val sharedPreferences = getSharedPreferences(BaseConstants.ANDROID_SYSTEM, MODE_PRIVATE)
            val pictureName = sharedPreferences.getString(BaseConstants.PICTURE_NAME, "") ?: ""

            val intent = Intent("com.android.camera.action.CROP").apply {
                this.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                this.putExtra("crop", true)
                this.putExtra("return-data", false)
                this.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
                this.putExtra("scale", true)
            }

            val cropPictureName = "005_crop_${System.currentTimeMillis()}.jpg"

            val phoneFile = File(
                Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_PICTURES}/AndroidSystem"),
                pictureName
            )

            val cropPhoneFile = File(
                Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_PICTURES}/AndroidSystem"),
                cropPictureName
            )

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                val pictureUri = Uri.fromFile(phoneFile)
                val cropUri = Uri.fromFile(cropPhoneFile)

                intent.setDataAndType(pictureUri, "image/*")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri)

                cropPictureResultLauncher.launch(intent)

                return@registerForActivityResult
            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                val pictureUri = getPictureUri(phoneFile)
                val cropUri = getPictureUri(cropPhoneFile)

                intent.setDataAndType(pictureUri, "image/*")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri)
                intent.clipData = ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, cropUri)

                cropPictureResultLauncher.launch(intent)

                return@registerForActivityResult
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val selection = "${MediaStore.Images.ImageColumns.DISPLAY_NAME} = '$pictureName'"
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

                val pictureUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    pictureId
                )

                val contentValue = ContentValues().apply {
                    this.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, cropPictureName)
                    this.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg")
                    this.put(
                        MediaStore.Images.ImageColumns.RELATIVE_PATH,
                        "${Environment.DIRECTORY_PICTURES}/AndroidSystem"
                    )
                }

                val cropUri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValue
                )

                intent.setDataAndType(pictureUri, "image/*")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri)

                cropPictureResultLauncher.launch(intent)

                return@registerForActivityResult
            }
        }

    private val cropPictureResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
//            MediaScannerConnection.scanFile(this, arrayOf(activityResult.toString()), null) { _, _ ->
//
//            }

            //aclIvPicturePreview.setImageURI(activityResult)
        }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {


        if (BaseConstants.READ_WRITE_PERMISSIONS == requestCode) {
            startTakePicture()
            return
        }

        if (BaseConstants.CROP_PERMISSIONS == requestCode) {
            startTakePictureWithCrop()
            return
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