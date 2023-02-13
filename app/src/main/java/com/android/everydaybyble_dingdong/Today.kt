package com.android.everydaybyble_dingdong

import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

/*월별 일수 계산*/
fun MonthSize(Year : Int, Month : Int): Int {
    var monthSize : Int = arrayOf(31,28,31,30,31,30,31,31,30,31,30,31)[Month - 1]
    if(Month == 2 && Year % 4 == 0 && Year % 100 != 0 || Year % 400 == 0){
        /*윤년 계산*/
        monthSize += 1
    }

    return monthSize
}

/* 날짜 갱신 함수.. --> 2023년 1월21일 토요일's return값 = 202301216(Int) */
fun getTodayDateInt(): Int {
    val calendar = Calendar.getInstance()
    val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        Calendar.SUNDAY -> 6
        else -> -1
    }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    return year * 100000 + month * 1000 + day * 10 + dayOfWeek
}

/*날짜 활용 출력텍스트 및 사진파일명*/
class Today(input_date : Int) {

    public val valueYear : Int = (Date/100000)
    public val valueMonth : Int = (Date/1000) % 100
    public val valueDay : Int = (Date%1000)/10

    public val file_name : String = (input_date/10).toInt().toString() + ".jpeg"
    public val text_date : String = input_date.toString().substring(0,4) + "년 "+
            input_date.toString().substring(4,6) + "월 "+
            input_date.toString().substring(6,8) + "일 "+
            arrayOf('월', '화', '수', '목', '금', '토', '일')[input_date%10].toString() + "요일"
}