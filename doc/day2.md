# 110/02 - 只有 StartActivityForResult 可以用嗎？ - 1

###### tags: `IT鐵人`

***

前一天講到合約(Contracts)和啟動器(Launcher)取代StartActivityForResult，官方也幫我們建立了14種常見的合約模板，以下是官方的14種合約

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

這篇先介紹這三種

````kotlin
ActivityResultContracts.CreateDocument()
ActivityResultContracts.GetContent()
ActivityResultContracts.GetMultipleContents()
````

## ActivityResultContracts.CreateDocument()

> An ActivityResultContract to prompt the user to select a path for creating a new document, returning the content: Uri of the item that was created. The input is the suggested name for the new file.
>
>This can be extended to override createIntent if you wish to pass additional extras to the Intent created by super.createIntent().

`CreateDocument()`可以用在拍照前的建立空白檔案。

以下範例是建立空白的`saberEat.jpg`檔案，然後回傳檔案的`content://uri`，需要注意使用者可以更改檔案名稱。

````kotlin
createDocumentResultLauncher.launch("saberEat.jpg")
````

````kotlin
private val createDocumentResultLauncher =
    registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
        Log.d("maho", "回傳: $uri")
    }
````

實際執行程式後的Log

````
D/maho: 回傳: content://com.android.providers.downloads.documents/document/1692
````

## ActivityResultContracts.GetContent()

> An ActivityResultContract to prompt the user to pick a piece of content, receiving a content:// Uri for that content that allows you to use android.content.ContentResolver.openInputStream(Uri) to access the raw data. By default, this adds Intent.CATEGORY_OPENABLE to only return content that can be represented as a stream.
>
>The input is the mime type to filter by, e.g. image/*. This can be extended to override createIntent if you wish to pass additional extras to the Intent created by super.createIntent().

以下範例是輸入一個`MIME type`指定類型，然後選擇一個檔案，回傳檔案的`content://uri`，不可以輸入`null`。

````kotlin
getContentResultLauncher.launch("image/*")
````

````kotlin
private val getContentResultLauncher =
    registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        Log.d("maho", "回傳: $uri")
    }
````

實際執行程式後的Log

````
D/maho: 回傳: content://com.android.providers.downloads.documents/document/1688
````

## ActivityResultContracts.GetMultipleContents()

> An ActivityResultContract to prompt the user to pick one or more a pieces of content, receiving a content:// Uri for each piece of content that allows you to use android.content.ContentResolver.openInputStream(Uri) to access the raw data. By default, this adds Intent.CATEGORY_OPENABLE to only return content that can be represented as a stream.
>
>The input is the mime type to filter by, e.g. image/*.
>
>This can be extended to override createIntent if you wish to pass additional extras to the Intent created by super.createIntent().

以下範例是輸入一個`MIME type`指定類型，然後長按選擇多個檔案，用陣列的形式回傳檔案的`content://uri`，不可以輸入`null`。

````kotlin
getMultipleContentsResultLauncher.launch("image/*")
````

````kotlin
private val getMultipleContentsResultLauncher =
    registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uri ->
        Log.d("maho", "回傳: $uri")
    }
````

實際執行程式後的Log

````
D/maho: 回傳: [
content://com.android.providers.downloads.documents/document/1688,
content://com.android.providers.downloads.documents/document/1677
]
````

***
程式碼放在`feature/resultTemplate`分支  
[https://github.com/AndyAWD/AndroidSystem/tree/feature/resultTemplate](https://github.com/AndyAWD/AndroidSystem/tree/feature/resultTemplate)