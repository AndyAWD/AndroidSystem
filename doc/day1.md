# 1 - 什麼！startActivityForResult被標記棄用？

***

講到硬體就會用到權限控制，然後一定會用到onActivityResult和startActivityForResult  
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

現在Google建議使用[Activity Results API](https://developer.android.com/training/basics/intents/result)  
所以現在來實作怎麼使用

首先是兩個Activity帶值互相跳頁的寫法  
建立MainActivity和ResultOneActivity兩個Activity    
MainActivity會帶一個Key值為system的android字串到ResultOneActivity    
ResultOneActivity結束時會帶一個Key值為result的one字串到MainActivity

MainActivity畫面如下

````xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools" android:layout_width="match_parent"
    android:layout_height="match_parent" tools:context=".MainActivity">

    <com.google.android.material.button.MaterialButton android:id="@+id/amMbResultOne"
        android:layout_width="match_parent" android:layout_height="0dp"
        android:text="跳轉 ResultOneActivity" android:textAllCaps="false"
        app:layout_constraintDimensionRatio="5:1" app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
````

ResultOneActivity畫面如下

````xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools" android:layout_width="match_parent"
    android:layout_height="match_parent" tools:context=".ResultOneActivity">

    <com.google.android.material.button.MaterialButton android:id="@+id/aroMbMain"
        android:layout_width="match_parent" android:layout_height="0dp"
        android:text="跳轉 MainActivity" android:textAllCaps="false"
        app:layout_constraintDimensionRatio="5:1" app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
````

MainActivity程式碼的部份，以前是建立onActivityResult，現在不用  
改成建立合約(Contract)和啟動器(Launcher)

所以我們先在MainActivity建立啟動器，裡面的監聽跟以前onActivityResult的寫法一樣

````kotlin
private val resultOneLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (RESULT_OK == activityResult.resultCode) {
            Log.d(
                "maho",
                "ResultOneActivity帶過來的值: ${activityResult.data?.getStringExtra("result")}"
            )
        }
    }
````

至於跳頁，以前我們使用startActivity(intent)，因為建立啟動器的關係，所以改用啟動器執行

````kotlin
val intent = Intent(this, ResultOneActivity::class.java).apply {
    this.putExtra("system", "android")
}

resultOneLauncher.launch(intent)
````

MainActivity全部的程式碼是這樣

````kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initClickListener()
    }

    private fun initClickListener() {
        amMbResultOne.setOnClickListener {

            val intent = Intent(this, ResultOneActivity::class.java).apply {
                this.putExtra("system", "android")
            }

            resultOneLauncher.launch(intent)
        }
    }

    private val resultOneLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (RESULT_OK == activityResult.resultCode) {
                Log.d(
                    "maho",
                    "ResultOneActivity帶過來的值: ${activityResult.data?.getStringExtra("result")}"
                )
            }
        }
}
````

ResultOneActivity程式碼的部份，寫法就和以前一樣

````kotlin
class ResultOneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_one)

        val system = intent.getStringExtra("system")
        Log.d("maho", "MainActivity帶過來的值: $system")

        initClickListener()
    }

    private fun initClickListener() {
        aroMbMain.setOnClickListener {

            val intent = Intent().apply {
                this.putExtra("result", "one")
            }

            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
````

實際執行程式後的Log

````
tw.com.andyawd.androidsystem D/maho: MainActivity帶過來的值: android
tw.com.andyawd.androidsystem D/maho: ResultOneActivity帶過來的值: one
````

所以我們已經跨出第一步，不使用onActivityResult監聽Result  
改成使用合約(Contract)和啟動器(Launcher)監聽Result  
接下來幾天會繼續講解Activity Results API的用法

程式碼放在feature/result分支    
[https://github.com/AndyAWD/AndroidSystem/tree/feature/result](https://github.com/AndyAWD/AndroidSystem/tree/feature/result)