package tw.com.andyawd.androidsystem

import android.Manifest
import android.content.*
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_crop_lens.*
import kotlinx.android.synthetic.main.fragment_lens_control.*
import kotlinx.coroutines.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import kotlin.coroutines.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LensControlFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LensControlFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lens_control, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponent()
        initClickListener()
    }

    private fun initComponent() {
        sharedPreferences = requireActivity().getSharedPreferences(
            BaseConstants.ANDROID_SYSTEM,
            AppCompatActivity.MODE_PRIVATE
        )
    }

    private fun initClickListener() {
        flcMbCreatePackageNamePicture.setOnClickListener {

            val pictureName = getPictureFilename(PACKAGE_NAME_PICTURE)

            sharedPreferences.edit {
                this.putString(BaseConstants.PICTURE_NAME, pictureName)
            }

            val uri = getPictureUri(getPackageNamePictureFile(pictureName))
            createPackageNameResultLauncher.launch(uri)
        }

        flcMbCreatePhonePicture.setOnClickListener {
            startCreatePhonePicture()
        }

        flcMbCropPackageNamePicture.setOnClickListener {
            val pictureName = getPictureFilename(CROP_PACKAGE_NAME_PICTURE_BEFORE)

            sharedPreferences.edit {
                this.putString(BaseConstants.PICTURE_NAME, pictureName)
            }

            val uri = getPictureUri(getPackageNamePictureFile(pictureName))
            createCropPackageNameResultLauncher.launch(uri)
        }

        flcMbCropPhonePicture.setOnClickListener {
            startCropCreatePhonePicture()
        }
    }

    private fun refreshAlbum(uri: String) {
        MediaScannerConnection.scanFile(requireActivity(), arrayOf(uri), null) { _, _ ->

        }
    }

    private fun getPackageNamePictureFile(name: String) =
        File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), name)

    private fun getPhonePictureFile(name: String) =
        File(
            Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_PICTURES}/AndroidSystem"),
            name
        )

    private fun getPictureFilename(name: String) = "${name}_${System.currentTimeMillis()}.jpg"

    private fun isCreateFolder(folderPath: File): Boolean {
        val file = File(folderPath, "AndroidSystem")

        return if (file.exists()) {
            true
        } else file.mkdir()
    }

    private fun getPictureUri(picturePath: File): Uri? {
        return FileProvider.getUriForFile(
            requireActivity(),
            "${requireActivity().packageName}${BaseConstants.DOT_FILEPROVIDER}",
            picturePath
        )
    }

    private fun startCreatePhonePicture() {

        val pictureName = getPictureFilename(PHONE_PICTURE)

        sharedPreferences.edit {
            this.putString(BaseConstants.PICTURE_NAME, pictureName)
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            val permissionList = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            if (!EasyPermissions.hasPermissions(requireActivity(), *permissionList)) {
                EasyPermissions.requestPermissions(
                    this,
                    "請提供讀寫檔案權限",
                    BaseConstants.CREATE_PHONE_PICTURE_PERMISSIONS,
                    *permissionList
                )
                return
            }

            if (!isCreateFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))) {
                return
            }

            val uri = getPictureUri(getPhonePictureFile(pictureName))

            flcIvPicturePreview.setImageURI(uri)
            createPhoneResultLauncher.launch(uri)
            return
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

            val uri = requireActivity().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValue
            )

            flcIvPicturePreview.setImageURI(uri)
            createPhoneResultLauncher.launch(uri)
            return
        }
    }

    private fun startCropCreatePhonePicture() {
        val pictureName = getPictureFilename(CROP_PHONE_PICTURE_BEFORE)

        sharedPreferences.edit {
            this.putString(BaseConstants.PICTURE_NAME, pictureName)
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            val permissionList = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            if (!EasyPermissions.hasPermissions(requireActivity(), *permissionList)) {
                EasyPermissions.requestPermissions(
                    this,
                    "請提供讀寫檔案權限",
                    BaseConstants.CREATE_CROP_PHONE_PICTURE_PERMISSIONS,
                    *permissionList
                )
                return
            }

            if (!isCreateFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))) {
                return
            }

            val uri = getPictureUri(getPhonePictureFile(pictureName))
            flcIvPicturePreview.setImageURI(uri)
            createCropPhoneResultLauncher.launch(uri)
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

            val uri = requireActivity().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValue
            )
            flcIvPicturePreview.setImageURI(uri)
            createCropPhoneResultLauncher.launch(uri)
        }
    }

    private val createCropPackageNameResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isTakePicture ->
            if (!isTakePicture) {
                return@registerForActivityResult
            }

            val intent = Intent("com.android.camera.action.CROP").apply {
                this.putExtra("crop", true)
                this.putExtra("return-data", false)
                this.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
                this.putExtra("scale", true)
            }

            val packagePictureName = sharedPreferences.getString(BaseConstants.PICTURE_NAME, "")
                ?: return@registerForActivityResult
            val packageNameFile = getPackageNamePictureFile(packagePictureName)

            val cropPackageName = getPictureFilename(CROP_PACKAGE_NAME_PICTURE_AFTER)
            val cropPackageNameFile = getPackageNamePictureFile(cropPackageName)

            sharedPreferences.edit {
                this.putString(BaseConstants.CROP_PICTURE_NAME, cropPackageName)
            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                val pictureUri = Uri.fromFile(packageNameFile)
                val cropUri = Uri.fromFile(cropPackageNameFile)

                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(pictureUri, "image/*")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri)

                flcIvPicturePreview.setImageURI(pictureUri)
                cropPackageNameResultLauncher.launch(intent)
                return@registerForActivityResult
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val pictureUri = getPictureUri(packageNameFile)
                val cropUri = getPictureUri(cropPackageNameFile)

                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(pictureUri, "image/*")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri)
                intent.clipData = ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, cropUri)

                flcIvPicturePreview.setImageURI(pictureUri)
                cropPackageNameResultLauncher.launch(intent)
                return@registerForActivityResult
            }
        }

    private val cropPackageNameResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            Log.d("maho", "activityResult: $activityResult")

            val cropPackagePictureName =
                sharedPreferences.getString(BaseConstants.CROP_PICTURE_NAME, "")
                    ?: return@registerForActivityResult

            val cropPackageNameFile = getPackageNamePictureFile(cropPackagePictureName)

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                val cropUri = Uri.fromFile(cropPackageNameFile)
                refreshAlbum(cropUri.toString())
                flcIvPicturePreview.setImageURI(cropUri)
                return@registerForActivityResult
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val cropUri = getPictureUri(cropPackageNameFile)
                refreshAlbum(cropUri.toString())
                flcIvPicturePreview.setImageURI(cropUri)
                return@registerForActivityResult
            }
        }

    private val createPackageNameResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isTakePicture ->

            if (!isTakePicture) {
                return@registerForActivityResult
            }

            val pictureName = sharedPreferences.getString(BaseConstants.PICTURE_NAME, "")
                ?: return@registerForActivityResult

            val packageNameFile = getPackageNamePictureFile(pictureName)

            refreshAlbum(packageNameFile.toString())
            flcIvPicturePreview.setImageURI(getPictureUri(packageNameFile))
        }

    private val createPhoneResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isTakePicture ->

            if (!isTakePicture) {
                return@registerForActivityResult
            }

            val pictureName = sharedPreferences.getString(BaseConstants.PICTURE_NAME, "")
                ?: return@registerForActivityResult

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                val phoneFile = getPackageNamePictureFile(pictureName)

                refreshAlbum(phoneFile.toString())
                flcIvPicturePreview.setImageURI(getPictureUri(phoneFile))
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val selection = "${MediaStore.Images.ImageColumns.DISPLAY_NAME} = '$pictureName'"
                val orderBy = "${MediaStore.Images.ImageColumns.DATE_ADDED} DESC"

                val uriQuery = requireActivity().contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    selection,
                    null,
                    orderBy
                ) ?: return@registerForActivityResult

                uriQuery.moveToFirst()

                val id = uriQuery.getColumnIndex(MediaStore.Images.ImageColumns._ID)
                val pictureId = uriQuery.getLong(id)

                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    pictureId
                )

                refreshAlbum(uri.toString())
                flcIvPicturePreview.setImageURI(uri)
            }
        }

    private val createCropPhoneResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isTakePicture ->
            if (!isTakePicture) {
                return@registerForActivityResult
            }

            val intent = Intent("com.android.camera.action.CROP").apply {
                this.putExtra("crop", true)
                this.putExtra("return-data", false)
                this.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
                this.putExtra("scale", true)
            }

            val phonePictureName = sharedPreferences.getString(BaseConstants.PICTURE_NAME, "")
                ?: return@registerForActivityResult
            val phoneFile = getPhonePictureFile(phonePictureName)

            val cropPhoneName = getPictureFilename(CROP_PHONE_PICTURE_AFTER)
            val cropPhoneFile = getPhonePictureFile(cropPhoneName)

            sharedPreferences.edit {
                this.putString(BaseConstants.CROP_PICTURE_NAME, cropPhoneName)
            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                val pictureUri = Uri.fromFile(phoneFile)
                val cropUri = Uri.fromFile(cropPhoneFile)

                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(pictureUri, "image/*")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri)

                Log.d("maho", "pictureUri: $pictureUri")
                Log.d("maho", "cropUri: $cropUri")

                cropPhoneResultLauncher.launch(intent)
                return@registerForActivityResult
            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                val pictureUri = getPictureUri(phoneFile)
                val cropUri = getPictureUri(cropPhoneFile)

                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(pictureUri, "image/*")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri)
                intent.clipData = ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, cropUri)

                cropPhoneResultLauncher.launch(intent)
                return@registerForActivityResult
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val selection =
                    "${MediaStore.Images.ImageColumns.DISPLAY_NAME} = '$phonePictureName'"
                val orderBy = "${MediaStore.Images.ImageColumns.DATE_ADDED} DESC"

                val uriQuery = requireActivity().contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    selection,
                    null,
                    orderBy
                ) ?: return@registerForActivityResult

                uriQuery.moveToFirst()

                val id = uriQuery.getColumnIndex(MediaStore.Images.ImageColumns._ID)
                val pictureId = uriQuery.getLong(id)

                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    pictureId
                )

                val contentValue = ContentValues().apply {
                    this.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, cropPhoneName)
                    this.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg")
                    this.put(
                        MediaStore.Images.ImageColumns.RELATIVE_PATH,
                        "${Environment.DIRECTORY_PICTURES}/AndroidSystem"
                    )
                }

                val cropUri = requireActivity().contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValue
                )

                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(uri, "image/*")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri)

                cropPhoneResultLauncher.launch(intent)
                return@registerForActivityResult
            }
        }

    private val cropPhoneResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            Log.d("maho", "activityResult: $activityResult")
            refreshAlbum(activityResult.toString())
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            BaseConstants.CREATE_PHONE_PICTURE_PERMISSIONS -> {
                startCreatePhonePicture()
            }
            BaseConstants.CREATE_CROP_PHONE_PICTURE_PERMISSIONS -> {
                startCropCreatePhonePicture()
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        PermissionsDeniedAlertDialog(requireActivity()).show(
            resources.getString(R.string.permissions_denied),
            resources.getString(
                R.string.permissions_setting_open,
                EasyPermissionsTextTransformer().getPermissionsList(perms)
            )
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LensControlFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LensControlFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val PACKAGE_NAME_PICTURE = "lenPackageName"
        const val PHONE_PICTURE = "lenPhone"
        const val CROP_PACKAGE_NAME_PICTURE_BEFORE = "lenPackageNameCropBefore"
        const val CROP_PACKAGE_NAME_PICTURE_AFTER = "lenPackageNameCropAfter"
        const val CROP_PHONE_PICTURE_BEFORE = "lenPhoneCropBefore"
        const val CROP_PHONE_PICTURE_AFTER = "lenPhoneCropAfter"
    }
}
