# 110/03 - 只有 StartActivityForResult 可以用嗎？ - 2

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
ActivityResultContracts.OpenDocument()
ActivityResultContracts.OpenDocumentTree()
ActivityResultContracts.OpenMultipleDocuments()
````

## ActivityResultContracts.OpenDocument()

> An ActivityResultContract to prompt the user to open a document, receiving its contents as a file:/http:/content: Uri.
>
>The input is the mime types to filter by, e.g. image/*.
>
>This can be extended to override createIntent if you wish to pass additional extras to the Intent created by super.createIntent().
>
>See Also: DocumentsContract

以下範例是輸入多個`MIME type`指定類型，然後選擇一個檔案，回傳檔案的`content://uri`，可以輸入`null`表示不指定類型。

````kotlin
openDocumentResultLauncher.launch(arrayOf("image/jpeg", "video/mp4"))
````

````kotlin
private val openDocumentResultLauncher =
    registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        Log.d("maho", "回傳: $uri")
    }
````

實際執行程式後的Log

````
D/maho: 回傳: content://com.android.providers.downloads.documents/document/1677
````

## ActivityResultContracts.OpenDocumentTree()

> An ActivityResultContract to prompt the user to select a directory, returning the user selection as a Uri. Apps can fully manage documents within the returned directory.
>
>The input is an optional Uri of the initial starting location.
>
>This can be extended to override createIntent if you wish to pass additional extras to the Intent created by super.createIntent().
>
>See Also:
> Intent.ACTION_OPEN_DOCUMENT_TREE, DocumentsContract.buildDocumentUriUsingTree, DocumentsContract.buildChildDocumentsUriUsingTree

以下範例是選擇資料夾，然後回傳資料夾的`content://uri`，可以輸入`null`表示不指定路徑。

````kotlin
openDocumentTreeResultLauncher.launch(null)
````

````kotlin
private val openDocumentTreeResultLauncher =
    registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        Log.d("maho", "回傳: $uri")
    }
````

選擇`Download/Duo`資料夾，實際執行程式後的Log

````
D/maho: 回傳: content://com.android.externalstorage.documents/tree/primary%3ADownload%2FDuo
````

## ActivityResultContracts.OpenMultipleDocuments()

> An ActivityResultContract to prompt the user to open (possibly multiple) documents, receiving their contents as file:/http:/content: Uris.
>
>The input is the mime types to filter by, e.g. image/*.
>
>This can be extended to override createIntent if you wish to pass additional extras to the Intent created by super.createIntent().
>
>See Also:
DocumentsContract

以下範例是輸入多個`MIME type`指定類型，然後選擇多個檔案，用陣列的形式回傳檔案的`content://uri`，不可以輸入`null`。

````kotlin
openMultipleDocumentsResultLauncher.launch(arrayOf("image/jpeg", "video/mp4"))
````

````kotlin
private val openMultipleDocumentsResultLauncher =
    registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { list ->
        Log.d("maho", "回傳: $list")
    }
````

實際執行程式後的Log

````
D/maho: 回傳: [
content://com.android.providers.media.documents/document/image%3A5789, 
content://com.android.providers.media.documents/document/image%3A5791
]
````

***
程式碼放在`feature/resultTemplate`分支  
[https://github.com/AndyAWD/AndroidSystem/tree/feature/resultTemplate](https://github.com/AndyAWD/AndroidSystem/tree/feature/resultTemplate)