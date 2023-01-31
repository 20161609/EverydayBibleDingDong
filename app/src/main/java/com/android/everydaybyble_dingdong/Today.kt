package com.android.everydaybyble_dingdong

import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.time.LocalDateTime

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
fun Date_today(): Int {
    val now = LocalDateTime.now()
    return (now.year.toInt() * 100000 +
            now.monthValue.toInt() * 1000 +
            now.dayOfMonth.toInt()*10 +
            now.dayOfWeek.value.toInt()
            )
}

/*날짜 활용 출력텍스트 및 사진파일명*/
class Today(input_date : Int) {

    public val valueYear : Int = (Date/100000)
    public val valueMonth : Int = (Date/1000) % 100
    public val valueDay : Int = (Date%1000)/10

    val file_name : String = (input_date/10).toInt().toString() + ".jpeg"
    val text_date : String = input_date.toString().substring(0,4) + "년 "+
            input_date.toString().substring(4,6) + "월 "+
            input_date.toString().substring(6,8) + "일 "+
            arrayOf('월', '화', '수', '목', '금', '토', '일')[input_date%10-1].toString() + "요일"

    public fun today_card(imageview : ImageView){
        val storage = Firebase.storage
        val storageRef = storage.reference
        val islandRef = storageRef.child(arrayFileNames[current_picture_index])
        val ONE_MEGABYTE: Long = 1024 * 1024

        islandRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(imageview.context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(imageview)
        }.addOnFailureListener {
            // Handle any errors
            Log.e("getBytes","failure")
        }
    }
}