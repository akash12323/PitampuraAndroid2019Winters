package com.puldroid.roomdatabase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    val db by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java, "user.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    val nMgr by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    val name: MutableLiveData<String> = MutableLiveData()
    var trigger = MutableLiveData<Boolean>()
    val list = arrayListOf<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = UserAdapter(list)
        db.userDao().getAllUsers().observe(this, Observer {
            list.clear()
//            Thread.sleep(5000)
            trigger.value = true
            list.addAll(it)
            adapter.notifyDataSetChanged()
        })
        trigger.observe(this, Observer {
            Log.i("LiveData", "$it")
            btn.isEnabled = it
        })
        name.observe(this, Observer {
            Log.i("LiveData", "$it")
        })
        btn.setOnClickListener {
            trigger.value = false
            GlobalScope.launch(Dispatchers.Main) {
               val a =  withContext(Dispatchers.IO) { db.userDao().insertUser(User(editText.text.toString(), 15, "Student")) }
                Toast.makeText(this@MainActivity,"Entry With id = $a",Toast.LENGTH_SHORT).show()
                showNotification()
            }
        }

        userRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter = adapter
        }


    }

    fun showNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nMgr.createNotificationChannel(
                NotificationChannel(
                    "first",
                    "default",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        val notification = Notification.Builder(this,"first")
            .setContentTitle("Header")
            .setContentText("This is the Description")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        nMgr.notify(System.currentTimeMillis().toInt(),notification)
    }
}
