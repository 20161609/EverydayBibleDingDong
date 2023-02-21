package com.android.everydaybyble_dingdong

import DateChangeReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

var buttonBlocked : Boolean = false //버튼 클릭 가능여부
var current_picture_index : Int = 6
var arrayFileNames : Array<String> = arrayOf()
var daySelection : Array<Today> = arrayOf()
var dayClicked : Int = -1///클릭된 date번호

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*0. 사이즈 관련*/
        val screenHeight : Int = resources.displayMetrics.heightPixels
        val screenWidth : Int = resources.displayMetrics.widthPixels

        /*1. 첫화면 초기화*/
        initToday()

        /*1-1. 메시지 출력용 카드뷰 임시제거*/
        val Entire_box : LinearLayout = findViewById(R.id.Entire_box)
        val messageSaved : androidx.cardview.widget.CardView = findViewById(R.id.messageSaved)
        val messageNotConnection : androidx.cardview.widget.CardView = findViewById(R.id.messageNotConnection)
        val messages : Array<androidx.cardview.widget.CardView> = arrayOf(
            messageSaved, messageNotConnection
        )
        val v_animation : com.airbnb.lottie.LottieAnimationView = findViewById(R.id.v_animation)
        Entire_box.removeView(messageSaved)
        Entire_box.removeView(messageNotConnection)

        /*1-2. 어제 말씀 창 임시 제거*/
        val day_list_cover : androidx.cardview.widget.CardView = findViewById(R.id.day_list_cover)
        Entire_box.removeView(day_list_cover)

        /*2. 버튼 기능 실행*/
        val ExcuteButton = excuteButton(
            this,
            day_list_cover,
            messages,
            screenHeight,
            v_animation
        )
        val button_id : Array<Int> = arrayOf(R.id.button0, R.id.button1, R.id.button2, R.id.button3)
        for(click in 0..3) {
            val button: Button = findViewById<Button?>(button_id[click]).apply {
                setOnClickListener() {
                    try{
                        ExcuteButton.run(click)
                    }
                    catch(e:Exception){
                        //print message "에러가 발생하였습니다."
                    }
                }
            }
        }

        /*3. 날짜 갱신 (24시 정각)*/
        try{
            val dateChangeReceiver = DateChangeReceiver()
            val intentFilter = IntentFilter(Intent.ACTION_DATE_CHANGED)
            this.registerReceiver(dateChangeReceiver, intentFilter)
        }
        catch (e : Exception){
            Log.e("G같은","co딩")
        }

        val location = IntArray(2)
        val card_box : androidx.cardview.widget.CardView = findViewById(R.id.card_box)
        card_box.getLocationOnScreen(location)
        Log.e(location[0].toString(), location[1].toString())

        Log.e("섹","스1")
    }

    private fun initToday(){
        /*1. 날짜 갱신*/
        dayClicked = -1
        current_picture_index = 6
        
        for(ago in 6 downTo 0)//7일 day list작성
            daySelection = daySelection.plus(Today(ago))

        /*2. 오늘의 날짜 출력*/
        val text_date : TextView = findViewById(R.id.text_date)
        text_date.text = daySelection[6].text_date//dateText(arrayFileNames[6].substring(0,8).toInt(), dayOfWeek)

        /*3. 사진 갱신*/
        ImageCaching(findViewById(R.id.Current_picture), 6)
    }
}