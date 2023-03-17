package com.android.everydaybyble_dingdong

import android.util.Log
import android.widget.Button
import java.util.*

class Today(ago : Int){
    public var year : Int
    public var month : Int
    public val day : Int

    public val dayOfWeek : Int

    public lateinit var text_date : String
    public lateinit var fileName : String

    init {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -ago)

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1
        day = calendar.get(Calendar.DAY_OF_MONTH)
        val a = calendar.get(Calendar.HOUR_OF_DAY)
        Log.e(a.toString(), "지금시간")
        print(year)
        print(month)
        println(day)
        dayOfWeek = dayOfWeek(calendar)

        text_date = dateText()
        fileName = fileName()
    }

    private fun dayOfWeek(calendar: Calendar): Int {
        val dayOfWeekInt : Int = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> -1
        }
        return dayOfWeekInt
    }

    private fun dateText(): String {
        val dateInt : Int = this.year * 10000 + this.month * 100 + this.day
        val text : String = dateInt.toString().substring(0,4) + "년 "+
                dateInt.toString().substring(4,6) + "월 "+
                dateInt.toString().substring(6,8) + "일 "+
                arrayOf('월', '화', '수', '목', '금', '토', '일')[this.dayOfWeek].toString() + "요일"
        return text
    }

    private fun fileName(): String{
        return (this.year * 10000 + this.month * 100 + this.day).toString() + ".jpeg"
    }
}

fun dateText(date : Int, dayOfWeek : Int): String {
    val text_date : String = date.toString().substring(0,4) + "년 "+
            date.toString().substring(4,6) + "월 "+
            date.toString().substring(6,8) + "일 "+
            arrayOf('월', '화', '수', '목', '금', '토', '일')[dayOfWeek].toString() + "요일"
    return text_date
}