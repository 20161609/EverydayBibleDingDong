package com.android.everydaybyble_dingdong

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.provider.Settings.Global.getString
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

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
                //날짜 갱신
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

                //날짜 및 이미지 갱신
                dayClicked = i
                current_picture_index = dayClicked
                imageCaching(activity.findViewById(R.id.Current_picture), i)
            }
        }
    }
}

public fun dayCard(activity : Activity, context : Context) : LinearLayout{

    fun dpToPx(dp: Float, context:Context): Int {
        val px= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics())
        return px.toInt()
    }

    val dayListLinearLayout = LinearLayout(context).apply {
        id = R.id.day_list
        layoutParams = RelativeLayout.LayoutParams(
            dpToPx(340.toFloat() ,context), dpToPx(113.toFloat() ,context)
        ).apply {
            bottomMargin = dpToPx(30.toFloat() ,context)
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        }
        background = ContextCompat.getDrawable(context, R.drawable.custom_shadow2)
        orientation = LinearLayout.VERTICAL
    }

    val headerRelativeLayout = RelativeLayout(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            dpToPx(315.toFloat() ,context), ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = dpToPx(10.toFloat() ,context)
        }
    }

    val dayBoxTextView = TextView(context).apply {
        id = View.generateViewId()
        text = "최근 일주일 말씀입니다."
        setTextColor(ContextCompat.getColor(context, R.color.black))
        typeface = ResourcesCompat.getFont(context, R.font.arita4_0_b)
        layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_START)
            topMargin = dpToPx(10.toFloat() ,context)
        }
    }
    val backButton = AppCompatButton(context).apply {
        id = R.id.button_back
        layoutParams = RelativeLayout.LayoutParams(
            dpToPx(30.toFloat() ,context), dpToPx(30.toFloat() ,context)
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_END)
            topMargin = dpToPx(8.toFloat() ,context)
        }
        background = ContextCompat.getDrawable(context, android.R.drawable.presence_offline)
        isClickable = true
    }
    back=backButton
    headerRelativeLayout.addView(dayBoxTextView)
    headerRelativeLayout.addView(backButton)

    dayButtons = arrayOf()
    val dayButtonsLinearLayout = LinearLayout(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            dpToPx(340.toFloat() ,context), ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dpToPx(35.toFloat() ,context)
        }
        for (i in 0..6) {
            val dayButton = AppCompatButton(context).apply {
                id = resources.getIdentifier("day$i", "id", context.packageName)
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(30.toFloat() ,context), dpToPx(30.toFloat() ,context)
                ).apply {
                    marginStart = if (i == 0) {
                        dpToPx(5.45.toFloat() ,context)
                    } else {
                        dpToPx(18.toFloat() ,context)
                    }
                    gravity = Gravity.CENTER
                }
                stateListAnimator=null
                gravity=Gravity.CENTER
                setPadding(0,0,0,0)
                if(i==current_picture_index){
                    background = ContextCompat.getDrawable(context, R.drawable.shape_for_circle_button_clicked)
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                }else{
                    background = ContextCompat.getDrawable(context, R.drawable.shape_for_circle_button)
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                }
                text = (daySelection[i].day + 100).toString().substring(1, 3)
                typeface = ResourcesCompat.getFont(context, R.font.arita4_0_m)
                textSize = 16f
                isClickable = true
            }
            addView(dayButton)
            dayButtons=dayButtons.plus(dayButton)
        }
    }

    dayListLinearLayout.addView(headerRelativeLayout)
    dayListLinearLayout.addView(dayButtonsLinearLayout)
    return dayListLinearLayout
}