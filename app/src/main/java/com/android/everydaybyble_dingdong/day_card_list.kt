package com.android.everydaybyble_dingdong

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.Log
import androidx.appcompat.widget.AppCompatButton

/*각 date에 따른 selection 후 UI변환 함수들*/
private fun uiClicked(button : AppCompatButton?){
    button?.setBackgroundResource(R.drawable.shape_for_circle_button_clicked)
    button?.setTextColor(Color.WHITE)
}
private fun uiUnclicked(button : AppCompatButton?){
    button?.setBackgroundResource(R.drawable.shape_for_circle_button)
    button?.setTextColor(Color.BLACK)
}

public fun dayCardList(activity : Activity, context : Context) {
    val buttonArray : Array<Int> = arrayOf(R.id.day0, R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6)

    for (i in 0..6){
        /*a. user가 클릭한 버튼 표식*/
        val button : AppCompatButton? = dayButtons[i]
//        button?.text = (daySelection[i].day + 100).toString().substring(1, 3)
        dayButtons[i]?.text = (daySelection[i].day + 100).toString().substring(1, 3)
        if(i == dayClicked)uiClicked(button)//버튼 UI갱신


        /*b. 버튼 기능 수행.*/
        button?.setOnClickListener(){
            if(!isNetworkConnected(activity)){// -> b.1. 네트워크 연결 불가(메시지 출력)
                ToastMessage?.view = messageNotConnection
                errorText!!.text = "네트워크 연결을 확인해주세요"
                val handler = Handler()
                handler.postDelayed({ ToastMessage!!.cancel() }, 1800.toLong())
                ToastMessage!!.show().run{ mCountDown.start() }
                Log.e("log", errorText!!.text.toString())
            }
            else if(dayClicked == -1){// -> b.2. 네트워크 연결 가능 -> 버튼 클릭이 안된 상태.
                /*날짜 갱신*/
                dayClicked = i
                current_picture_index = dayClicked
                uiClicked(button)
                //오늘이 아닌 날짜 선택
                if(i != 6)  imageCaching(activity.findViewById(R.id.Current_picture), i)
            }else if(dayClicked != i){// -> b.3. 네트워크 연결 가능 -> 오늘이 아닌 잘짜 선택
                //기존 버튼 해제
                uiUnclicked(dayButtons[dayClicked])
                //클릭된 버튼으로 갱신
                uiClicked(button)
                /*날짜 및 이미지 갱신*/
                dayClicked = i
                current_picture_index = dayClicked
                imageCaching(activity.findViewById(R.id.Current_picture), i)
            }
        }
    }
}