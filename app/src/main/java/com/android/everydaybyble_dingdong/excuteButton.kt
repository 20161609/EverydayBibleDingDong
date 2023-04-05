package com.android.everydaybyble_dingdong

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
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
        toastY = screenHeight - location[1] - card_box.height
        toastX = location[0]

        Log.e(day_list_cover?.marginTop.toString(), "day_list_cover")

        val layoutParams = card_box.layoutParams as ViewGroup.MarginLayoutParams
        val marginBottom = layoutParams.bottomMargin
        Log.e("hojun", marginBottom.toString())
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
            handler.postDelayed({ ToastMessage!!.cancel() }, 1800.toLong())
            ToastMessage!!.show().run{ mCountDown.start() }
        }
    }

    /*button0000 : 어제 말씀*/
    private fun ButtonSelectingImage(){
        /*1, 버튼박스 갱신*/
        boxView?.removeView(card_box)
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
        val save = SaveImageFile(imageView.context)

        /*외부저장소 접근 가능여부*/
        if(save.isExternalStorageWritable()){// -> a. 외부저장소 접근 가능
            //a.1. 저장
            when (androidVersion) {
                Build.VERSION_CODES.Q// Android version is 10
                -> save.saveImage_v10(imageView, activity)//
                Build.VERSION_CODES.R// Android version is 11
                -> save.saveImage_v11(imageView, activity, "filename")
                else // 9밑으론 쓰지 마셈ㅗ
                -> return
            }

            //a.2. 저장메시지 ToastView
            ToastMessage?.view = messageSaved
            val handler = Handler()
            handler.postDelayed({ ToastMessage!!.cancel() }, 1800.toLong())
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
            handler.postDelayed({ ToastMessage!!.cancel() }, 1800.toLong())
            ToastMessage!!.show().run{ mCountDown.start() }
            Log.e("log", errorText!!.text.toString())
            Log.e("Save", "Fail")
        }
    }

    /*button0002 : 공유*/
    private fun ButtonSharingImage(){
        val imageView: ImageView = activity.findViewById(R.id.Current_picture)
        when(androidVersion){
            Build.VERSION_CODES.Q -> shareImage_v11(imageView, activity)
            Build.VERSION_CODES.R -> shareImage_v11(imageView, activity)
        }
    }

    /*button0003 : 앱정보*/
    private fun ButtonAppInfo(){
        val dialogView = activity.layoutInflater.inflate(R.layout.app_info, null)
        dialog.setContentView(dialogView)
        dialog.behavior.state=BottomSheetBehavior.STATE_COLLAPSED
        dialog.show()
    }
}