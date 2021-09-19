# 110/07 - 建立自己的 ResultContracts

###### tags: `IT鐵人`

***

前面講完官方的14種`Contracts`，但我們也能建立屬於自己的`Contracts`
，以下範例是實作一個跟`ActivityResultContracts.StartActivityForResult()`一樣功能的`Contracts`，差別在於這次傳入的是`Bundle`
，回傳一樣是武器名稱

建立`LancerActivity`、`LancerActivityResultContract`  
`MainActivity`透過`LancerActivityResultContract`跳到`LancerActivity`時會帶姓名  
`LancerActivity`離開頁面後會回傳武器

`MainActivity`畫面如下

````xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools" android:layout_width="match_parent"
    android:layout_height="match_parent" tools:context=".MainActivity">

    <com.google.android.material.button.MaterialButton android:id="@+id/amMbLancer"
        android:layout_width="match_parent" android:layout_height="0dp"
        android:layout_marginStart="8dp" android:layout_marginEnd="8dp"
        android:text="跳轉 LancerActivity" android:textAllCaps="false"
        app:layout_constraintDimensionRatio="5:1" app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/amMbSaber" />
</androidx.constraintlayout.widget.ConstraintLayout>
````

`LancerActivity`畫面如下

````xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools" android:layout_width="match_parent"
    android:layout_height="match_parent" tools:context=".LancerActivity">

    <com.google.android.material.button.MaterialButton android:id="@+id/alMbMain"
        android:layout_width="match_parent" android:layout_height="0dp"
        android:layout_marginStart="8dp" android:layout_marginTop="4dp"
        android:layout_marginEnd="8dp" android:text="跳轉 Main 回傳 Gae Bolg"
        android:textAllCaps="false" app:layout_constraintDimensionRatio="5:1"
        app:layout_constraintEnd_toEndOf="parent" app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
````

跳轉後的程式碼

````kotlin
class LancerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lancer)

        val bundle = intent.extras
        val name = bundle?.getString(BaseConstants.NAME)
        val gender = bundle?.getString(BaseConstants.GENDER)

        Log.d("maho", "姓名: $name / 性別：$gender")

        initClickListener()
    }

    private fun initClickListener() {
        alMbMain.setOnClickListener {

            val intent = Intent().apply {
                this.putExtra(BaseConstants.WEAPON, "Gae Bolg")
            }

            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
````

建立合約`Contract`類別，名稱為`LancerActivityResultContract`，需要繼承`ActivityResultContract<input, output>`，程式碼如下

````kotlin
class LancerActivityResultContract : ActivityResultContract<Bundle, String>() {
    override fun createIntent(context: Context, input: Bundle): Intent {
        return Intent(context, LancerActivity::class.java).apply {
            putExtras(input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String {
        val weapon = intent?.getStringExtra(BaseConstants.WEAPON)

        return if (Activity.RESULT_OK == resultCode && null != weapon) {
            weapon
        } else {
            BaseConstants.STRING_EMPTY
        }
    }
}
````

所以自定義合約`Contract`很簡單，只要決定好輸入和輸出的型態就好，而且把`Result`拉出來做成類別後，也比較好找程式碼，不用去`Activity`找

建立好`LancerActivityResultContract`後，和之前的寫法一樣，去`MainActivity`，把合約`Contract`和啟動器`Launcher`實作出來就好，程式碼如下

````kotlin
val bundle = Bundle()
bundle.putString(BaseConstants.NAME, "CuChulainn")
bundle.putString(BaseConstants.GENDER, "male")

lancerActivityResultContract.launch(bundle)
````

````kotlin
private val lancerActivityResultContract =
    registerForActivityResult(LancerActivityResultContract()) { weapon ->
        Log.d("maho", "回傳: $weapon")
    }
````

實際執行程式後的Log

````
D/maho: 姓名: CuChulainn / 性別：male
D/maho: 回傳: Gae Bolg
````

程式碼放在`feature/createContracts`分支    
[https://github.com/AndyAWD/AndroidSystem/tree/feature/result](https://github.com/AndyAWD/AndroidSystem/tree/feature/createContracts)