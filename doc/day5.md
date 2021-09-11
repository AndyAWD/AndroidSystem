# 110/05 - 只有 StartActivityForResult 可以用嗎？ - 4

###### tags: `IT鐵人`

***

繼續介紹官方14種合約的範例

````kotlin
ActivityResultContracts.CreateDocument()
ActivityResultContracts.GetContent()
ActivityResultContracts.GetMultipleContents()
ActivityResultContracts.OpenDocument()
ActivityResultContracts.OpenDocumentTree()
ActivityResultContracts.OpenMultipleDocuments()
ActivityResultContracts.PickContact()
ActivityResultContracts.RequestMultiplePermissions()
ActivityResultContracts.RequestPermission()
ActivityResultContracts.StartActivityForResult()
ActivityResultContracts.StartIntentSenderForResult()
ActivityResultContracts.TakePicture()
ActivityResultContracts.TakePicturePreview()
ActivityResultContracts.TakeVideo()
````

今天介紹這四種，`StartActivityForResult()`第一天已經介紹過，所以就不再介紹範例。

````kotlin
ActivityResultContracts.StartIntentSenderForResult()
ActivityResultContracts.TakePicture()
ActivityResultContracts.TakePicturePreview()
ActivityResultContracts.TakeVideo()
````

## ActivityResultContracts.StartIntentSenderForResult()

> An ActivityResultContract that calls Activity.startIntentSender(IntentSender, Intent, int, int, int). This ActivityResultContract takes an IntentSenderRequest, which must be constructed using an IntentSenderRequest.Builder. If the call to Activity.startIntentSenderForResult(IntentSender, int, Intent, int, int, int) throws an IntentSender.SendIntentException the androidx.activity.result.ActivityResultCallback will receive an ActivityResult with an Activity.RESULT_CANCELED resultCode and whose intent has the action of ACTION_INTENT_SENDER_REQUEST and an extra EXTRA_SEND_INTENT_EXCEPTION that contains the thrown exception.

我不知道這個是做什麼用的，Google後才知道是Phone Selector Api，可以用來做簡訊驗證之類的功能。

````kotlin
//implementation 'com.google.android.gms:play-services-auth:19.2.0'

val hintRequest = HintRequest
    .Builder()
    .setPhoneNumberIdentifierSupported(true)
    .build()

val credentialsOptions = CredentialsOptions
    .Builder()
    .forceEnableSaveDialog()
    .build()

val credentials = Credentials
    .getClient(this, credentialsOptions)
    .getHintPickerIntent(hintRequest)

startIntentSenderForResultResultLauncher.launch(
    IntentSenderRequest
        .Builder(credentials)
        .build()
)
````

````kotlin
private val startIntentSenderForResultResultLauncher =
    registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
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
````

實際執行程式後的Log

````
D/maho: id: +886910123456 
    accountType: null 
    familyName: null 
    givenName: null 
    idTokens: [] 
    name: null 
    password: null 
    profilePictureUri: null

````

## ActivityResultContracts.TakePicture()

> An ActivityResultContract to take a picture saving it into the provided content-Uri.
>
>Returns true if the image was saved into the given Uri.
>
>This can be extended to override createIntent if you wish to pass additional extras to the Intent created by super.createIntent().

以下範例是建立空白檔案後再開啟相機拍攝照片，回傳`true`表示儲存成功，`false`表示儲存失敗。

第一種寫法：用`ActivityResultContracts.CreateDocument()`建立檔案，取得檔案的`uri`後再使用`TakePicture()`拍照

````kotlin
takePictureCreateDocumentResultLauncher.launch("002.jpg")
````

````kotlin
private val takePictureCreateDocumentResultLauncher =
    registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
        takePictureResultLauncher.launch(uri)
    }

private val takePictureResultLauncher =
    registerForActivityResult(ActivityResultContracts.TakePicture()) { boolean ->
        Log.d("maho", "回傳: $boolean")
    }    
````

實際執行程式後的Log

````
//ActivityResultContracts.CreateDocument()的Log
D/maho: 回傳: content://com.android.providers.downloads.documents/document/1701

//ActivityResultContracts.TakePicture()的Log
D/maho: 回傳: true
````

第二種寫法：使用`File()`建立檔案，再使用`getUriForFile()`取得檔案的`uri`後再使用`TakePicture()`拍照

````kotlin
val picturePath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "003.jpg")
val uri = getUriForFile(this, "$packageName.fileprovider", picturePath)

takePictureResultLauncher.launch(uri)
````

````kotlin
private val takePictureResultLauncher =
    registerForActivityResult(ActivityResultContracts.TakePicture()) { boolean ->
        Log.d("maho", "回傳: $boolean")
    }    
````

如果要用第二種寫法還要前置作業

1. res 資料夾底下新增 xml 資料夾
2. 建立 tool_provider_paths.xml

````xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path name="lens_picture" path="." />
</paths>
````

3. AndroidManifest 新增

````xml
<?xml version="1.0" encoding="utf-8"?>
<manifest>
    <application>

        <provider android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider" android:exported="false"
            android:grantUriPermissions="true">
            <meta-data android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/tool_provider_paths" />
        </provider>

    </application>
</manifest>
````

實際執行程式後的Log

````
D/maho: 回傳: true
````

## ActivityResultContracts.TakePicturePreview()

> An ActivityResultContract to take small a picture preview, returning it as a Bitmap.
>
>This can be extended to override createIntent if you wish to pass additional extras to the Intent created by super.createIntent().

以下範例是開啟相機，拍攝照片後回傳相片縮圖的`bitmap`
，因為是縮圖，所以圖片會非常小，[官方文件表示 take small a picture preview](https://developer.android.com/reference/kotlin/androidx/activity/result/contract/ActivityResultContracts.TakePicturePreview?hl=zh-tw)，所以我也不太懂這個可以用來做什麼功能。

````kotlin
takePicturePreviewResultLauncher.launch(null)
````

````kotlin
private val takePicturePreviewResultLauncher =
    registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        amIvTakePicturePreview.setImageBitmap(bitmap)

        //把縮圖存起來的程式碼
        val picturePath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "004.jpg")
        val fileOutputStream = FileOutputStream(picturePath)
        val bufferedOutputStream = BufferedOutputStream(fileOutputStream)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream)
        bufferedOutputStream.flush()
        bufferedOutputStream.close()
    }
````

實際執行程式後的Log

````
D/maho: 回傳: android.graphics.Bitmap@93f6ede
````

## ActivityResultContracts.TakeVideo()

> An ActivityResultContract to take a video saving it into the provided content-Uri. Returns a thumbnail.
>
>This can be extended to override createIntent if you wish to pass additional extras to the Intent created by super.createIntent().

以下範例是建立空白檔案後再開啟相機錄製影片，錄製完後回傳影片縮圖的`bitmap`。

````kotlin
//路徑為空，會儲存到 DCIM 資料夾
takeVideoResultLauncher.launch(null)

//指定儲存路徑
val picturePath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "005.mp4")
val uri = getUriForFile(this, "$packageName.fileprovider", picturePath)
takeVideoResultLauncher.launch(uri)
````

````kotlin
private val takeVideoResultLauncher =
    registerForActivityResult(ActivityResultContracts.TakeVideo()) { bitmap ->
        amIvTakeVideo.setImageBitmap(bitmap)
    }
````

實際執行程式後的Log，影片有儲存成功，理論上要回傳影片縮圖的`bitmap`，但我在`Android 11`
一直試不出來，[StackOverflow也有一樣的問題](https://stackoverflow.com/questions/65704408/activityresultcontracts-takevideo-is-returning-null-after-recording-the-video)，也還沒解決，只能說是官方的坑

````
D/maho: 回傳: null
````

***
程式碼放在`feature/resultTemplate`分支  
[https://github.com/AndyAWD/AndroidSystem/tree/feature/resultTemplate](https://github.com/AndyAWD/AndroidSystem/tree/feature/resultTemplate)