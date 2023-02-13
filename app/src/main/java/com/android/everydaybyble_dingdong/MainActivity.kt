package com.android.everydaybyble_dingdong

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

/*날짜 관련 변수*/
var Date : Int = getTodayDateInt() // 2023년 1월 25일 수요일 --> 202301253
var today : Today = Today(202301253)//Date)

var buttonBlocked : Boolean = false //버튼 클릭 가능여부
var current_picture_index : Int = 6
var arrayFileNames : Array<String> = arrayOf()
var dayClicked : Int = -1///클릭된 date번호

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*0. 사이즈 관련*/
        val height : Int = resources.displayMetrics.heightPixels
        val width : Int = resources.displayMetrics.widthPixels

        /*1. 첫화면 초기화*/
        initToday()

        /*1-1. 메시지 출력용 카드뷰 임시제거*/
        val Entire_box : LinearLayout = findViewById(R.id.Entire_box)
        val messageSaved : androidx.cardview.widget.CardView = findViewById(R.id.messageSaved)
        val messageNotConnection : androidx.cardview.widget.CardView = findViewById(R.id.messageNotConnection)
        val messages : Array<androidx.cardview.widget.CardView> = arrayOf(
            messageSaved, messageNotConnection
        )

        Entire_box.removeView(messageSaved)
        Entire_box.removeView(messageNotConnection)

        /*1-2. 어제 말씀 창 임시 제거*/
        val day_list_cover : androidx.cardview.widget.CardView = findViewById(R.id.day_list_cover)
        Entire_box.removeView(day_list_cover)

        //앱정보 drawer
        val drawerAppInfo : androidx.drawerlayout.widget.DrawerLayout = findViewById(R.id.drawerAppInfo)

        /*2. 버튼 기능 실행*/
        val ExcuteButton = excuteButton(
            this,
            this,
            day_list_cover,
            messages,
            drawerAppInfo,
            height, width
        )
        val button_id : Array<Int> = arrayOf(R.id.button0, R.id.button1, R.id.button2, R.id.button3)
        for(i in 0..3) {
            val button: Button = findViewById<Button?>(button_id[i]).apply {
                setOnClickListener() { ExcuteButton.Run(i) }
            }
        }

        /*3. 날짜 갱신 (24시 정각)*/

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
        Date = getTodayDateInt()//Date = 202302131
        today = Today(Date)
        dayClicked = -1
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
        ImageCaching(findViewById(R.id.Current_picture), 6)
    }
}