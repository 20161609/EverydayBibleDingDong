package com.android.everydaybyble_dingdong

import DateChangeReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

val androidVersion = Build.VERSION.SDK_INT

var buttonBlocked : Boolean = false //버튼 클릭 가능여부
var current_picture_index : Int = 6 // 현재 출력되어있는 말씀카드 인덱스
var daySelection : Array<Today> = arrayOf() // 7일간의 데이터 array
var dayButtons : Array<AppCompatButton?> = arrayOf()
var dayClicked : Int = -1 //클릭된 date번호
var errorText : TextView? = null // 출력할 error Message

var activity_sv : View? = null
var set_viewers : RelativeLayout? = null

var ToastMessage: Toast? = null // 공통된 toastMessage

var messageSaved : androidx.cardview.widget.CardView? = null
var v_animation : com.airbnb.lottie.LottieAnimationView? = null
var o_animation : com.airbnb.lottie.LottieAnimationView? = null

var messageNotConnection : androidx.cardview.widget.CardView? = null

var day_list_cover : androidx.cardview.widget.CardView? = null
var back : Button? = null
var Entire_box : RelativeLayout? = null
var imageBox : RelativeLayout? = null

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_EverydayByble_DingDong)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*0. 사이즈 관련*/
        val screenHeight : Int = resources.displayMetrics.heightPixels
        val screenWidth : Int = resources.displayMetrics.widthPixels

        Entire_box = findViewById(R.id.Entire_box)
        activity_sv = LayoutInflater.from(this).inflate(R.layout.activity_set_viewrers,null)
        set_viewers = activity_sv?.findViewById(R.id.set_viewers)
        messageSaved = activity_sv?.findViewById(R.id.messageSaved)
        messageNotConnection = activity_sv?.findViewById(R.id.messageNotConnection)

        v_animation = activity_sv?.findViewById(R.id.v_animation1)
        val textRemove : TextView = findViewById(R.id.textRemove)
        imageBox = findViewById(R.id.imageBox)
        imageBox?.removeView(textRemove)
        o_animation = findViewById(R.id.o_animation)
        imageBox?.removeView(o_animation)

        errorText = activity_sv?.findViewById(R.id.errorText)
        day_list_cover = activity_sv?.findViewById(R.id.day_list_cover)
        back = activity_sv?.findViewById(R.id.button_back)
        for(b_id in arrayOf(R.id.day0,R.id.day1,R.id.day2,R.id.day3,R.id.day4,R.id.day5,R.id.day6))
            dayButtons=dayButtons.plus(activity_sv?.findViewById(b_id))

        initToday(this, this)

        set_viewers?.removeView(messageSaved)
        set_viewers?.removeView(messageNotConnection)

        /*1-2. 어제 말씀 창 임시 제거*/
        set_viewers?.removeView(day_list_cover)

        /*2. 버튼 기능 실행*/
        val ExcuteButton = excuteButton(this, this, screenHeight)
        val buttonId : Array<Int> = arrayOf(R.id.button0, R.id.button1, R.id.button2, R.id.button3)
        for(click in 0..3) {
            val button : Button = findViewById<Button?>(buttonId[click])
            button.setOnClickListener() {ExcuteButton.run(click)}
        }

        /*3. 날짜 갱신 (24시 정각)*/


        try{
            val dateChangeReceiver = DateChangeReceiver(this,this)
            val intentFilter = IntentFilter(Intent.ACTION_DATE_CHANGED)
            this.registerReceiver(dateChangeReceiver, intentFilter)
        }
        catch (e : Exception){
            Log.e("J같은","co딩")
        }
    }

    override fun onPause() {
        super.onPause()
        ToastMessage?.cancel()
    }

}
public fun initToday(activity: AppCompatActivity, context: Context){
    /*1. 날짜 갱신*/
    current_picture_index = 6
    dayClicked = -1
    daySelection= emptyArray()
    for(ago in 6 downTo 0)//7일 day list작성
        daySelection = daySelection.plus(Today(ago))

    /*2. 오늘의 날짜 출력*/
    val textDate : TextView = activity.findViewById(R.id.text_date)
    textDate.text = daySelection[6].text_date

    /*3. 사진 갱신*/
    imageCaching(activity.findViewById(R.id.Current_picture), 6)

    /*4. 어제말씀 창 day 리스트 재구성*/
    dayCardList(activity, context)
}

public val mCountDown : CountDownTimer = object : CountDownTimer(2000, 5000){
    override fun onTick(millisUntilFinished: Long) {
        buttonBlocked = true
    }
    override fun onFinish() {
        buttonBlocked = false
    }
}