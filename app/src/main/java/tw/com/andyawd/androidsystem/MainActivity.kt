package tw.com.andyawd.androidsystem

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.credentials.Credential
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initClickListener()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.amMainFragment).navigateUp()
    }

    private fun initClickListener() {
        amMbResultContractsPage.setOnClickListener {
            val intent = Intent(this, ResultContractsActivity::class.java)

            resultLauncher.launch(intent)
        }

//        amMbCreateDocument.setOnClickListener {
//            createDocumentResultLauncher.launch("saberEat.jpg")
//        }
//
//        amMbGetContent.setOnClickListener {
//            getContentResultLauncher.launch("image/*")
//        }
//
//        amMbGetMultipleContents.setOnClickListener {
//            getMultipleContentsResultLauncher.launch("image/*")
//        }
//
//        amMbOpenDocument.setOnClickListener {
//            openDocumentResultLauncher.launch(arrayOf("image/jpeg", "video/mp4"))
//        }
//
//        amMbOpenDocumentTree.setOnClickListener {
//            openDocumentTreeResultLauncher.launch(null)
//        }
//
//        amMbOpenMultipleDocuments.setOnClickListener {
//            openMultipleDocumentsResultLauncher.launch(arrayOf("image/jpeg", "video/mp4"))
//        }
//
//        amMbPickContact.setOnClickListener {
//            pickContactResultLauncher.launch(null)
//        }
//
//        amMbRequestMultiplePermissions.setOnClickListener {
//            requestMultiplePermissionsResultLauncher.launch(
//                arrayOf(
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                )
//            )
//        }
//
//        amMbRequestPermission.setOnClickListener {
//            requestPermissionResultLauncher.launch(Manifest.permission.CAMERA)
//        }
//
//        amMbStartIntentSenderForResult.setOnClickListener {
//            val hintRequest = HintRequest
//                .Builder()
//                .setPhoneNumberIdentifierSupported(true)
//                .build()
//
//            val credentialsOptions = CredentialsOptions
//                .Builder()
//                .forceEnableSaveDialog()
//                .build()
//
//            val credentials = Credentials
//                .getClient(this, credentialsOptions)
//                .getHintPickerIntent(hintRequest)
//
//            startIntentSenderForResultResultLauncher.launch(
//                IntentSenderRequest
//                    .Builder(credentials)
//                    .build()
//            )
//        }
//
//        amMbTakePicture.setOnClickListener {
//            takePictureCreateDocumentResultLauncher.launch("002.jpg")
//        }
//
//        amMbTakePicture2.setOnClickListener {
//            val picturePath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "003.jpg")
//            val uri =
//                getUriForFile(this, "$packageName${BaseConstants.DOT_FILEPROVIDER}", picturePath)
//
//            takePictureResultLauncher.launch(uri)
//        }
//
//        amMbTakePicturePreview.setOnClickListener {
//            takePicturePreviewResultLauncher.launch(null)
//        }
//
//        amMbTakeVideo.setOnClickListener {
//            //路徑為空，會儲存到 DCIM 資料夾
//            //takeVideoResultLauncher.launch(null)
//
//            //指定儲存路徑
//            val picturePath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "005.mp4")
//            val uri =
//                getUriForFile(this, "$packageName${BaseConstants.DOT_FILEPROVIDER}", picturePath)
//            takeVideoResultLauncher.launch(uri)
//        }
//

//
//        amMbCropLensPicture.setOnClickListener {
//            val intent = Intent(this, CropLensActivity::class.java)
//            resultLauncher.launch(intent)
//        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            Log.d("maho", "回傳: $activityResult")
        }

    private val createDocumentResultLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
            Log.d("maho", "回傳: $uri")
        }

    private val getContentResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            Log.d("maho", "回傳: $uri")
        }

    private val getMultipleContentsResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uri ->
            Log.d("maho", "回傳: $uri")
        }

    private val openDocumentResultLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            Log.d("maho", "回傳: $uri")
        }

    private val openDocumentTreeResultLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            Log.d("maho", "回傳: $uri")
        }


    private val openMultipleDocumentsResultLauncher =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { list ->
            Log.d("maho", "回傳: $list")
        }

    private val pickContactResultLauncher =
        registerForActivityResult(ActivityResultContracts.PickContact()) { uri ->
            Log.d("maho", "回傳: $uri")
        }

    private val requestMultiplePermissionsResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            Log.d("maho", "回傳: $map")
        }

    private val requestPermissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { boolean ->
            Log.d("maho", "回傳: $boolean")
        }


    private val startIntentSenderForResultResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            Log.d("maho", "回傳: $result")
            if (RESULT_OK == result.resultCode) {
                val credential: Credential? = result
                    .data
                    ?.getParcelableExtra(Credential.EXTRA_KEY)

                Log.d(
                    "maho",
                    "id: ${credential?.id} " +
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
            Log.d("maho", "回傳: $uri")
            takePictureResultLauncher.launch(uri)
        }

    private val takePictureResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { boolean ->
            Log.d("maho", "回傳: $boolean")
        }

    private val takePicturePreviewResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            Log.d("maho", "回傳: $bitmap")
//            amIvTakePicturePreview.setImageBitmap(bitmap)

            val picturePath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "004.jpg")
            val fileOutputStream = FileOutputStream(picturePath)
            val bufferedOutputStream = BufferedOutputStream(fileOutputStream)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream)
            bufferedOutputStream.flush()
            bufferedOutputStream.close()
        }

    private val takeVideoResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakeVideo()) { bitmap ->
            Log.d("maho", "回傳: $bitmap")
//            amIvTakeVideo.setImageBitmap(bitmap)
        }
}
