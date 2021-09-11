# 110/04 - 只有 StartActivityForResult 可以用嗎？ - 3

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

今天介紹這三種

````kotlin
ActivityResultContracts.PickContact()
ActivityResultContracts.RequestMultiplePermissions()
ActivityResultContracts.RequestPermission()
````

## ActivityResultContracts.PickContact()

> An ActivityResultContract to request the user to pick a contact from the contacts app.
>
>The result is a content: Uri.
>
>See Also:
ContactsContract

選擇單個連絡人，回傳`content://uri`

````kotlin
pickContactResultLauncher.launch(null)
````

````kotlin
private val pickContactResultLauncher =
    registerForActivityResult(ActivityResultContracts.PickContact()) { uri ->
        Log.d("maho", "回傳: $uri")
    }
````

實際執行程式後的Log

````
D/maho: 回傳: content://com.android.contacts/contacts/lookup/1519iaad44c18aa14d9a/10
````

## ActivityResultContracts.RequestMultiplePermissions()

> An Intent action for making a permission request via a regular Activity.startActivityForResult API. Caller must provide a String[] extra EXTRA_PERMISSIONS Result will be delivered via Activity.onActivityResult(int, int, Intent) with String[] EXTRA_PERMISSIONS and int[] EXTRA_PERMISSION_GRANT_RESULTS, similar to Activity.onRequestPermissionsResult(int, String[], int[])
>
>See Also:
Activity.requestPermissions(String[], int), Activity.onRequestPermissionsResult(int, String[], int[])

> Key for the extra containing all the requested permissions. See Also:
ACTION_REQUEST_PERMISSIONS

> Key for the extra containing whether permissions were granted. See Also:
ACTION_REQUEST_PERMISSIONS

以下範例是輸入多個系統權限，用map的形式回傳每個權限的`true`和`false`，可以輸入`null`，不過沒意義。

````kotlin
requestMultiplePermissionsResultLauncher.launch(
    arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
)
````

````kotlin
private val requestMultiplePermissionsResultLauncher =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
        Log.d("maho", "回傳: $map")
    }
````

實際執行程式後的Log

````
D/maho: 回傳: {
android.permission.CAMERA=false, 
android.permission.WRITE_EXTERNAL_STORAGE=false
}
````

## ActivityResultContracts.RequestPermission()

> An ActivityResultContract to request a permission

以下範例是輸入一個系統權限，回傳權限的`true`和`false`，可以輸入`null`，不過沒意義。

````kotlin
requestPermissionResultLauncher.launch(Manifest.permission.CAMERA)
````

````kotlin
private val requestPermissionResultLauncher =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { boolean ->
        Log.d("maho", "回傳: $boolean")
    }
````

實際執行程式後的Log

````
D/maho: 回傳: false
````

***
程式碼放在`feature/resultTemplate`分支  
[https://github.com/AndyAWD/AndroidSystem/tree/feature/resultTemplate](https://github.com/AndyAWD/AndroidSystem/tree/feature/resultTemplate)