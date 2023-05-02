package com.android.everydaybyble_dingdong

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class excuteButton(
    private val activity : Activity,
    private val context : Context,
    private val screenHeight: Int,
) {
    /*-- Variables --*/
    /*Run Button*/
    private val card_box : LinearLayout = activity.findViewById(R.id.button_box)
    private val dialog : BottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)

    /*Toast 출력 좌표*/
    private var toastX : Int = 0
    private var toastY : Int = 0

    /*-- Functions --*/
    /*button Run*/
    public fun run(Clicked : Int){

        /*ToastView좌표 및 duration설정*/
        val location = IntArray(2)
        card_box.getLocationOnScreen(location)
        val a = card_box.marginBottom.toInt()
        Log.e(a.toString(), "HERE HI")

        toastY = a//screenHeight - location[1] - card_box.height
        toastX = location[0]

        Log.e(day_list_cover?.marginTop.toString(), "day_list_cover")

        val layoutParams = card_box.layoutParams as ViewGroup.MarginLayoutParams
        val marginBottom = layoutParams.bottomMargin

        ToastMessage = Toast(activity).apply {
            view = messageNotConnection
            duration = Toast.LENGTH_LONG
            setGravity(Gravity.BOTTOM or Gravity.START, toastX, toastY)
        }

        try{
            if(buttonBlocked || dialog.isShowing) return
            Log.e("Clicked", arrayOf("어제말씀", "저장", "공유", "앱정보")[Clicked])
            when(Clicked){
                0 -> ButtonSelectingImage()//어제말씀
                1 -> ButtonSavingImage1()//저장
                2 -> ButtonSharingImage()//공유
                3 -> ButtonAppInfo()//앱정보
            }
        }
        catch (e : Exception){
            ToastMessage?.view = messageNotConnection
            errorText!!.text = "에러가 발생하였습니다"
            val handler = Handler()
            handler.postDelayed({ ToastMessage!!.cancel() }, 1000.toLong())
            ToastMessage!!.show().run{ mCountDown.start() }
        }
    }

    /*button0000 : 어제 말씀*/
    private fun ButtonSelectingImage(){
        /*0. 버튼 박스 구성*/
        dayCardList(activity, context)

        /*1, 버튼박스 갱신*/
        boxView?.removeView(card_box)

        val dayCard = dayCard(activity, context)
        boxView?.addView(dayCard)
        isDayCard=true
        for(i in 0..6){
            dayButtons[i]?.setOnClickListener(){
                if (current_picture_index!=i) {
                    if (true){//isNetworkConnected(activity) && ){
                        current_picture_index = i
                        for (j in 0..6) {
                            if (j == current_picture_index) {
                                dayButtons[j]?.setBackgroundResource(R.drawable.shape_for_circle_button_clicked)
                                dayButtons[j]?.setTextColor((ContextCompat.getColor(context, R.color.white)))
                            } else {
                                dayButtons[j]?.setBackgroundResource(R.drawable.shape_for_circle_button)
                                dayButtons[j]?.setTextColor((ContextCompat.getColor(context, R.color.black)))
                            }
                        }
                        imageCaching(activity.findViewById(R.id.Current_picture), i)
                    }
                    else{
                        ToastMessage?.view = messageNotConnection
                        errorText!!.text = "네트워크 연결을 확인해주세요"
                        val handler = Handler()
                        handler.postDelayed({ ToastMessage!!.cancel() }, 1000.toLong())
                        ToastMessage!!.show().run{ mCountDown.start() }
                        Log.e("log", errorText!!.text.toString())
                    }
                }
            }
        }

        back?.setOnClickListener(){
            boxView?.removeView(dayCard)
            boxView?.addView(card_box)
            isDayCard=false
        }
        return


        boxView?.addView(day_list_cover)

        /*2. 뒤로 가기*/
        back?.setOnClickListener(){
            Log.e("Go","Back")
            boxView?.removeView(day_list_cover)
            boxView?.addView(card_box)
        }
    }

    /*button0001 : 저장*/
    private fun ButtonSavingImage1(){
        val imageView : ImageView = activity.findViewById(R.id.Current_picture)
        if(imageView.drawable == null){
            ToastMessage?.view = messageNotConnection
            errorText!!.text = "사진이 로드되지 않았습니다"
            val handler = Handler()
            handler.postDelayed({ ToastMessage!!.cancel() }, 1000.toLong())
            ToastMessage!!.show().run{ mCountDown.start() }
            Log.e("log", errorText!!.text.toString())
            return
        }

        val save = SaveImageFile(imageView.context)

        /*외부저장소 접근 가능여부*/
        if(save.isExternalStorageWritable()){// -> a. 외부저장소 접근 가능
            //a.1. 저장
            save.saveImage_v10(imageView, activity)

            //a.2. 저장메시지 ToastView
            ToastMessage?.view = messageSaved
            val handler = Handler()
            handler.postDelayed({ ToastMessage!!.cancel() }, 1000.toLong())
            ToastMessage!!.show().run{
                mCountDown.start()
                v_animation?.playAnimation()
            }
            Log.e("Save", "Success")
        }
        else{// -> b. 외부저장소 접근 불가
            ToastMessage?.view = messageNotConnection
            errorText!!.text = "앱 설정에서 앨범 접근을 허용해주세요"
            val handler = Handler()
            handler.postDelayed({ ToastMessage!!.cancel() }, 1000.toLong())
            ToastMessage!!.show().run{ mCountDown.start() }
            Log.e("log", errorText!!.text.toString())
            Log.e("Save", "Fail")
        }
    }

    /*button0002 : 공유*/
    private fun ButtonSharingImage(){
        val imageView: ImageView = activity.findViewById(R.id.Current_picture)
        shareImage_v11(imageView, activity)
    }

    /*button0003 : 앱정보*/
    private fun ButtonAppInfo(){
        val dialogView = activity.layoutInflater.inflate(R.layout.app_info, null)
        dialog.setContentView(dialogView)
        dialog.behavior.state=BottomSheetBehavior.STATE_COLLAPSED
        dialog.show()
    }
}