# 1 - 什麼！startActivityForResult 被標記棄用？

***

講到硬體就會用到權限控制，然後一定會用到 onActivityResult 和 startActivityForResult  
結果上面這兩個在最新的版本已經被標記棄用

讓我們看看原始碼

````java
@SuppressWarnings("deprecation")
@Override
@CallSuper
protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data){
        mFragments.noteStateNotSaved();
        super.onActivityResult(requestCode,resultCode,data);
        }

/**
 * {@inheritDoc}
 *
 * @deprecated use
 * {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}
 * passing in a {@link StartActivityForResult} object for the {@link ActivityResultContract}.
 */
@Override
@Deprecated
public void startActivityForResult(@SuppressLint("UnknownNullness") Intent intent,int requestCode){
        super.startActivityForResult(intent,requestCode);
        }

/**
 * {@inheritDoc}
 *
 * @deprecated use
 * {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}
 * passing in a {@link StartActivityForResult} object for the {@link ActivityResultContract}.
 */
@Override
@Deprecated
public void startActivityForResult(@SuppressLint("UnknownNullness") Intent intent,int requestCode,@Nullable Bundle options){
        super.startActivityForResult(intent,requestCode,options);
        }
````

現在 Google
建議使用 [Activity Results API](https://developer.android.com/training/basics/intents/result)  
所以現在來實作怎麼使用

首先是 Activity 帶值互相跳頁的寫法  
建立 MainActivity、SaberActivity、ArcherActivity  
MainActivity 跳到 SaberActivity 和 ArcherActivity 時會帶姓名 SaberActivity 和 ArcherActivity 離開頁面後會回傳武器

MainActivity 畫面如下

````xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools" android:layout_width="match_parent"
    android:layout_height="match_parent" tools:context=".MainActivity">

    <com.google.android.material.button.MaterialButton android:id="@+id/amMbSaber"
        android:layout_width="match_parent" android:layout_height="0dp"
        android:layout_marginStart="8dp" android:layout_marginTop="4dp"
        android:layout_marginEnd="8dp" android:text="跳轉 SaberActivity" android:textAllCaps="false"
        app:layout_constraintDimensionRatio="5:1" app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" app:layout_constraintTop_toTopOf="parent" />

    <com.google.android.material.button.MaterialButton android:id="@+id/amMbArcher"
        android:layout_width="match_parent" android:layout_height="0dp"
        android:layout_marginStart="8dp" android:layout_marginEnd="8dp"
        android:text="跳轉 ArcherActivity" android:textAllCaps="false"
        app:layout_constraintDimensionRatio="5:1" app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/amMbSaber" />
</androidx.constraintlayout.widget.ConstraintLayout>
````

SaberActivity 畫面如下

````xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools" android:layout_width="match_parent"
    android:layout_height="match_parent" tools:context=".SaberActivity">

    <com.google.android.material.button.MaterialButton android:id="@+id/asMbMain"
        android:layout_width="match_parent" android:layout_height="0dp"
        android:layout_marginStart="8dp" android:layout_marginTop="4dp"
        android:layout_marginEnd="8dp" android:text="跳轉 Main 回傳 Excalibur"
        android:textAllCaps="false" app:layout_constraintDimensionRatio="5:1"
        app:layout_constraintEnd_toEndOf="parent" app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
````

ArcherActivity 畫面如下

````xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools" android:layout_width="match_parent"
    android:layout_height="match_parent" tools:context=".ArcherActivity">

    <com.google.android.material.button.MaterialButton android:id="@+id/aaMbMain"
        android:layout_width="match_parent" android:layout_height="0dp"
        android:layout_marginStart="8dp" android:layout_marginTop="4dp"
        android:layout_marginEnd="8dp" android:text="跳轉 Main 回傳 Unlimited Blade Works"
        android:textAllCaps="false" app:layout_constraintDimensionRatio="5:1"
        app:layout_constraintEnd_toEndOf="parent" app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
````

MainActivity 程式碼的部份，以前是建立 onActivityResult 監聽資料  
現在改成建立合約(Contract)和啟動器(Launcher)

所以我們先在 MainActivity 建立啟動器，裡面的監聽跟以前 onActivityResult 的寫法一樣

````kotlin
private val resultLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (RESULT_OK == activityResult.resultCode) {
            Log.d(
                "maho",
                "回傳: ${activityResult.data?.getStringExtra(BaseConstants.WEAPON)}"
            )
        }
    }
````

至於跳頁，以前我們使用 startActivity(intent)，因為建立啟動器的關係，所以改用啟動器執行

````kotlin
//跳轉到SaberActivity
val intent = Intent(this, SaberActivity::class.java).apply {
    this.putExtra(BaseConstants.NAME, "Arthur")
}

resultLauncher.launch(intent)

//跳轉到ArcherActivity
val intent = Intent(this, ArcherActivity::class.java).apply {
    this.putExtra(BaseConstants.NAME, "Emiya")
}

resultLauncher.launch(intent)
````

MainActivity 全部的程式碼是這樣

````kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initClickListener()
    }

    private fun initClickListener() {
        amMbSaber.setOnClickListener {
            val intent = Intent(this, SaberActivity::class.java).apply {
                this.putExtra(BaseConstants.NAME, "Arthur")
            }

            resultLauncher.launch(intent)
        }

        amMbArcher.setOnClickListener {
            val intent = Intent(this, ArcherActivity::class.java).apply {
                this.putExtra(BaseConstants.NAME, "Emiya")
            }

            resultLauncher.launch(intent)
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (RESULT_OK == activityResult.resultCode) {
                Log.d(
                    "maho",
                    "回傳: ${activityResult.data?.getStringExtra(BaseConstants.WEAPON)}"
                )
            }
        }
}
````

跳轉後的程式碼，寫法和以前一樣

````kotlin
class SaberActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saber)

        val name = intent.getStringExtra(BaseConstants.NAME)
        Log.d("maho", "姓名: $name")

        initClickListener()
    }

    private fun initClickListener() {
        asMbMain.setOnClickListener {

            val intent = Intent().apply {
                this.putExtra(BaseConstants.WEAPON, "Excalibur")
            }

            setResult(RESULT_OK, intent)
            finish()
        }
    }
}


class ArcherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archer)

        val name = intent.getStringExtra(BaseConstants.NAME)
        Log.d("maho", "姓名: $name")

        initClickListener()
    }

    private fun initClickListener() {
        aaMbMain.setOnClickListener {

            val intent = Intent().apply {
                this.putExtra(BaseConstants.WEAPON, "Unlimited Blade Works")
            }

            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
````

實際執行程式後的Log

````
tw.com.andyawd.androidsystem D/maho: 姓名: Arthur
tw.com.andyawd.androidsystem D/maho: 回傳: Excalibur
tw.com.andyawd.androidsystem D/maho: 姓名: Emiya
tw.com.andyawd.androidsystem D/maho: 回傳: Unlimited Blade Works
````

所以我們已經跨出第一步，不使用onActivityResult監聽Result  
改成使用合約(Contract)和啟動器(Launcher)監聽Result  
接下來幾天會繼續講解Activity Results API的用法

程式碼放在feature/result分支    
[https://github.com/AndyAWD/AndroidSystem/tree/feature/result](https://github.com/AndyAWD/AndroidSystem/tree/feature/result)