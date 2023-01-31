package com.android.everydaybyble_dingdong

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import java.util.*

/*날짜 관련 변수*/
var Date : Int = Date_today() // 2023년 1월 25일 수요일 --> 202301253
var today : Today = Today(Date)

var buttonBlocked : Boolean = false //버튼 클릭 가능여부
var is_first_Clicked : Boolean = false // 어제말씀 첫 클릭 여부
var current_picture_index : Int = 6
var arrayFileNames : Array<String> = arrayOf()
val network_connection : Boolean = false

/*텍스트 크기*/
val DP : Int = 0
val PX : Int = 1
val SP : Int = 2

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*1. 첫화면 초기화*/
        initToday()
        NetworkConnection(applicationContext)

        val Entire_box : LinearLayout = findViewById(R.id.Entire_box)

        /*1-1. 메시지 출력용 카드뷰 임시제거*/
        val messageSaved : androidx.cardview.widget.CardView = findViewById(R.id.messageSaved)
        val messageNotConnection : androidx.cardview.widget.CardView = findViewById(R.id.messageNotConnection)
        val messages : Array<androidx.cardview.widget.CardView> = arrayOf(
            messageSaved, messageNotConnection
        )

        Entire_box.removeView(messageSaved)
        Entire_box.removeView(messageNotConnection)

        //앱정보 drawer
        val drawerAppInfo : androidx.drawerlayout.widget.DrawerLayout = findViewById(R.id.drawerAppInfo)

        /*2. 버튼 기능 실행*/
        val excuteButton = excuteButton(this,this, messages, drawerAppInfo)
        val button_id : Array<Int> = arrayOf(
            R.id.button0000, R.id.button0001, R.id.button0002, R.id.button0003
        )
        for(i in 0..3){
            val button : Button = findViewById<Button?>(button_id[i]).apply {
                setOnClickListener(){ excuteButton.Run(i) }
            }
        }

        /*2. 날짜 갱신 (24시 정각)*/
        val intent = Intent(this, MyBroadcastReceiver::class.java).apply{
            initToday()
        }
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            getMidnight(),
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun getMidnight(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.DATE, 1)
        return calendar.timeInMillis
    }

    private fun initToday(){
        /*1. 날짜 갱신*/
        Date = Date_today()
        today = Today(Date)
        current_picture_index = 6
        for(i in 6 downTo 0){
            var valueDay = today.valueDay - i
            var valueMonth = today.valueMonth
            var valueYear = today.valueYear

            if(valueDay < 1){
                valueMonth -= 1
                if(valueMonth < 1){
                    valueMonth = 12
                    valueYear -= 1
                }

                valueDay = MonthSize(valueYear, valueMonth) + valueDay
            }

            arrayFileNames = arrayFileNames.plus((valueYear*10000 + valueMonth*100 + valueDay).toString() + ".jpeg")
        }

        /*2. 날짜 출력 텍스트 갱신*/
        val text_date : TextView = findViewById(R.id.text_date)
        text_date.text = today.text_date

        /*3. 사진 갱신*/
        today.today_card(findViewById(R.id.Current_picture))
    }

}