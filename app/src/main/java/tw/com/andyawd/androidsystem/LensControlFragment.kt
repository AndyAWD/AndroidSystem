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

        initClickListener()
    }

    private fun initClickListener() {
        flcMbCreatePackageNamePicture.setOnClickListener {

            val pictureName = getPictureFilename(PACKAGE_NAME_PICTURE)

            val sharedPreferences = requireActivity().getSharedPreferences(
                BaseConstants.ANDROID_SYSTEM,
                AppCompatActivity.MODE_PRIVATE
            )

            sharedPreferences.edit {
                this.putString(BaseConstants.PICTURE_NAME, pictureName)
            }

            val packageNameFile =
                File(
                    requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    pictureName
                )
            val uri = getPictureUri(packageNameFile)

            createPackageNameResultLauncher.launch(uri)
        }

        flcMbCreatePhonePicture.setOnClickListener {
            startCreatePhonePicture()
        }
    }

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

        var uri: Uri? = null
        val pictureName = getPictureFilename(PHONE_PICTURE)
        val sharedPreferences = requireActivity().getSharedPreferences(
            BaseConstants.ANDROID_SYSTEM,
            AppCompatActivity.MODE_PRIVATE
        )

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

            uri = requireActivity().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValue
            )
        }

        createPhoneResultLauncher.launch(uri)
    }

    private val createPackageNameResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isTakePicture ->

            if (!isTakePicture) {
                return@registerForActivityResult
            }

            val sharedPreferences = requireActivity().getSharedPreferences(
                BaseConstants.ANDROID_SYSTEM,
                AppCompatActivity.MODE_PRIVATE
            )

            val pictureName = sharedPreferences.getString(BaseConstants.PICTURE_NAME, "")

            val packageNameFile = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                pictureName ?: ""
            )

            MediaScannerConnection.scanFile(
                requireActivity(),
                arrayOf(packageNameFile.toString()),
                null
            ) { _, _ ->

            }

            flcIvPicturePreview.setImageURI(getPictureUri(packageNameFile))
        }

    private val createPhoneResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isTakePicture ->

            if (!isTakePicture) {
                return@registerForActivityResult
            }

            val sharedPreferences = requireActivity().getSharedPreferences(
                BaseConstants.ANDROID_SYSTEM,
                AppCompatActivity.MODE_PRIVATE
            )

            val pictureName = sharedPreferences.getString(BaseConstants.PICTURE_NAME, "")

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                val phoneFile = File(
                    Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_PICTURES}/AndroidSystem"),
                    pictureName ?: ""
                )

                MediaScannerConnection.scanFile(
                    requireActivity(),
                    arrayOf(phoneFile.toString()),
                    null
                ) { _, _ ->

                }

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

                MediaScannerConnection.scanFile(
                    requireActivity(),
                    arrayOf(uri.toString()),
                    null
                ) { _, _ ->

                }

                flcIvPicturePreview.setImageURI(uri)
            }
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
        if (BaseConstants.READ_WRITE_PERMISSIONS == requestCode) {
            startCreatePhonePicture()
            return
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

        const val PACKAGE_NAME_PICTURE = "len001"
        const val PHONE_PICTURE = "len002"
        const val CROP_PACKAGE_NAME_PICTURE = "lenCrop003"
        const val CROP_PHONE_PICTURE = "lenCrop004"
    }
}
