package tw.com.andyawd.androidsystem

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.getUriForFile
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsOptions
import com.google.android.gms.auth.api.credentials.HintRequest
import kotlinx.android.synthetic.main.fragment_result_contracts.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ResultContractsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultContractsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var pictureUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        Log.d("maho", "onAttach activity: $activity")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("maho", "onAttach context: $context")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result_contracts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initClickListener()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initClickListener() {
        frcMbCreateDocument.setOnClickListener {
            createDocumentResultLauncher.launch("saberEat.jpg")
        }

        frcMbGetContent.setOnClickListener {
            getContentResultLauncher.launch("image/*")
        }

        frcMbGetMultipleContents.setOnClickListener {
            getMultipleContentsResultLauncher.launch("image/*")
        }

        frcMbOpenDocument.setOnClickListener {
            openDocumentResultLauncher.launch(arrayOf("image/jpeg", "video/mp4"))
        }

        frcMbOpenDocumentTree.setOnClickListener {
            openDocumentTreeResultLauncher.launch(null)
        }

        frcMbOpenMultipleDocuments.setOnClickListener {
            openMultipleDocumentsResultLauncher.launch(arrayOf("image/jpeg", "video/mp4"))
        }

        frcMbPickContact.setOnClickListener {
            pickContactResultLauncher.launch(null)
        }

        frcMbRequestMultiplePermissions.setOnClickListener {
            requestMultiplePermissionsResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

        frcMbRequestPermission.setOnClickListener {
            requestPermissionResultLauncher.launch(Manifest.permission.CAMERA)
        }

        frcMbStartIntentSenderForResult.setOnClickListener {
            val hintRequest = HintRequest
                .Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build()

            val credentialsOptions = CredentialsOptions
                .Builder()
                .forceEnableSaveDialog()
                .build()

            val credentials = Credentials
                .getClient(requireActivity(), credentialsOptions)
                .getHintPickerIntent(hintRequest)

            startIntentSenderForResultResultLauncher.launch(
                IntentSenderRequest
                    .Builder(credentials)
                    .build()
            )
        }

        frcMbTakePictureCreateDocument.setOnClickListener {
            takePictureCreateDocumentResultLauncher.launch("002.jpg")
        }

        frcMbTakePictureFile.setOnClickListener {

            val authority = "${requireActivity().packageName}${BaseConstants.DOT_FILEPROVIDER}"
            val picturePath = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "003.jpg"
            )
            val uri = getUriForFile(requireActivity(), authority, picturePath)

            pictureUri = uri
            takePictureResultLauncher.launch(uri)
        }

        frcMbTakePicturePreview.setOnClickListener {
            takePicturePreviewResultLauncher.launch(null)
        }

        frcMbTakeVideo.setOnClickListener {
            //路徑為空，會儲存到 DCIM 資料夾
            //takeVideoResultLauncher.launch(null)

            //指定儲存路徑
            val authority = "${requireActivity().packageName}${BaseConstants.DOT_FILEPROVIDER}"
            val picturePath = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "005.mp4"
            )
            val uri = getUriForFile(requireActivity(), authority, picturePath)
            takeVideoResultLauncher.launch(uri)
        }
    }

    private val createDocumentResultLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
            Log.d("maho", "CreateDocument 回傳: $uri")
        }

    private val getContentResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            Log.d("maho", "GetContent 回傳: $uri")
        }

    private val getMultipleContentsResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uri ->
            Log.d("maho", "GetMultipleContents 回傳: $uri")
        }

    private val openDocumentResultLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            Log.d("maho", "OpenDocument 回傳: $uri")
        }

    private val openDocumentTreeResultLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            Log.d("maho", "OpenDocumentTree 回傳: $uri")
        }


    private val openMultipleDocumentsResultLauncher =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { list ->
            Log.d("maho", "OpenMultipleDocuments 回傳: $list")
        }

    private val pickContactResultLauncher =
        registerForActivityResult(ActivityResultContracts.PickContact()) { uri ->
            Log.d("maho", "PickContact 回傳: $uri")
        }

    private val requestMultiplePermissionsResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            Log.d("maho", "RequestMultiplePermissions 回傳: $map")
        }

    private val requestPermissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { boolean ->
            Log.d("maho", "RequestPermission 回傳: $boolean")
        }

    private val startIntentSenderForResultResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            Log.d("maho", "StartIntentSenderForResult 回傳: $result")
            if (AppCompatActivity.RESULT_OK == result.resultCode) {

                val credential: Credential? = result.data?.getParcelableExtra(Credential.EXTRA_KEY)

                Log.d(
                    "maho",
                    "Credential.EXTRA_KEY: " +
                            "\nid: ${credential?.id} " +
                            "\naccountType: ${credential?.accountType} " +
                            "\nfamilyName: ${credential?.familyName} " +
                            "ngivenName: ${credential?.givenName} " +
                            "\nidTokens: ${credential?.idTokens} " +
                            "\nname: ${credential?.name} " +
                            "\npassword: ${credential?.password} " +
                            "\nprofilePictureUri: ${credential?.profilePictureUri}"
                )
            }
        }

    private val takePictureCreateDocumentResultLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
            Log.d("maho", "takePictureCreateDocument 回傳: $uri")

            pictureUri = uri
            takePictureResultLauncher.launch(uri)
        }

    private val takePictureResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { boolean ->
            Log.d("maho", "TakePicture 回傳: $boolean")

            if (pictureUri != null) {
                frcIvPicturePreview.setImageURI(pictureUri)
            }
        }

    private val takePicturePreviewResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            Log.d("maho", "TakePicturePreview 回傳: $bitmap")
            frcIvPicturePreview.setImageBitmap(bitmap)

            val picturePath = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "004.jpg"
            )
            val fileOutputStream = FileOutputStream(picturePath)
            val bufferedOutputStream = BufferedOutputStream(fileOutputStream)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream)
            bufferedOutputStream.flush()
            bufferedOutputStream.close()
        }

    private val takeVideoResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakeVideo()) { bitmap ->
            Log.d("maho", "TakeVideo 回傳: $bitmap")
            frcIvPicturePreview.setImageBitmap(bitmap)
        }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ResultContractsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ResultContractsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}