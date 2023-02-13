package com.android.everydaybyble_dingdong

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

public class excuteButton(
    inputLifecycleOwner: LifecycleOwner,
    inputMainactivity : Activity,
    day_list_cover : androidx.cardview.widget.CardView,
    inputMessages : Array<androidx.cardview.widget.CardView>,
    inputDrawerAppInfoInput : androidx.drawerlayout.widget.DrawerLayout,
    screenHeight : Int, screenWidth : Int
) {

    /*-- Variables --*/
    /*Run Button*/
    private val activity : Activity = inputMainactivity // MainActivity
    private val screenHeight = screenHeight;
    private val screenWidth = screenWidth;
    private val lifecycleOwner = inputLifecycleOwner

    private val card_box : androidx.cardview.widget.CardView = activity.findViewById(R.id.card_box)
    private val card_box_cover : androidx.cardview.widget.CardView = activity.findViewById(R.id.card_box_cover)

    private val day_list_cover : androidx.cardview.widget.CardView = day_list_cover

    private val messagesaved = inputMessages[0] // 메시지0 : "저장되었습니다"
    private val messageNotConnection = inputMessages[1] // 메시지1 : "네트워크를 확인해주세요."

    private val drawerAppInfo = inputDrawerAppInfoInput // Drawer로 출력할 앱정보

    /*메시지 출력동안 버튼 block상태*/
    private val mCountDown : CountDownTimer = object : CountDownTimer(2000, 5000) {
        override fun onTick(millisUntilFinished: Long) {
            buttonBlocked = true
        }

        override fun onFinish() {
            //countdown finish
            buttonBlocked = false
        }
    }

    /*-- Functions --*/
    /*button Run*/
    public fun Run(Clicked : Int){
        if(buttonBlocked || drawerAppInfo.isDrawerOpen(GravityCompat.START)) return

        when(Clicked){
            0 -> ButtonSelectingImage()//어제말씀
            1 -> ButtonSavingImage1()//(activity.findViewById(R.id.Current_picture), activity, messagesaved)//저장
            2 -> ButtonSharingImage()//공유
            3 -> ButtonAppInfo(drawerAppInfo)//앱정보
        }
    }

    /*button0000 : 어제 말씀*/
    private fun ButtonSelectingImage(){
        fun UI_clicked(button : Button){
            button.setBackgroundResource(R.drawable.shape_for_circle_button_clicked)
            button.setTextColor(Color.WHITE)
        }
        fun UI_unclicked(button : Button){
            button.setBackgroundResource(R.drawable.shape_for_circle_button)
            button.setTextColor(Color.BLACK)
        }

        val Entire_box : LinearLayout = activity.findViewById(R.id.Entire_box)
        Entire_box.removeView(card_box_cover)
        Entire_box.addView(day_list_cover)

        val buttonArray : Array<Int> = arrayOf(R.id.day0, R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6)

        for (i in 0..6){
            val button : Button = activity.findViewById(buttonArray[i])
            button.text = arrayFileNames[i].substring(6,8)
            if(i == dayClicked) UI_clicked(button)
            button.setOnClickListener(){
                if(!isNetworkConnected(activity)){//네트워크 연결 불가
                    val location = IntArray(2)
                    card_box.getLocationOnScreen(location)

                    val toastY : Int = screenHeight - location[1] - card_box.height
                    val toastX : Int = (card_box.measuredWidth - card_box_cover.measuredWidth) / 2
                    messageNotConnection.layoutParams.width = card_box.width
                    Toast(activity).apply {
                        view = messageNotConnection
                        duration = Toast.LENGTH_SHORT
                        setGravity(Gravity.BOTTOM, toastX, toastY)
                    }.run{
                        mCountDown.start()
                    }
                }
                else if(dayClicked == -1){//버튼 클릭이 안된 상태.
                    dayClicked = i
                    current_picture_index = dayClicked
                    UI_clicked(button)
                    if(i != 6)//오늘이 아닌 날짜 선택.
                        ImageCaching(activity.findViewById(R.id.Current_picture), i)
                }else if(dayClicked != i){
                    UI_unclicked(activity.findViewById(buttonArray[dayClicked]))//기존 버튼 해제
                    UI_clicked(button)//클릭된 버튼으로 갱신

                    //이미지 갱신
                    dayClicked = i
                    current_picture_index = dayClicked
                    ImageCaching(activity.findViewById(R.id.Current_picture), i)
                }
            }
        }

        val button_back : Button = activity.findViewById(R.id.button_back)
        button_back.setOnClickListener(){
            Entire_box.removeView(day_list_cover)
            Entire_box.addView(card_box_cover)
        }
    }

    /*button0001 : 저장*/
    private fun ButtonSavingImage1(){
        val imageView : ImageView = activity.findViewById(R.id.Current_picture)
        val save = SaveImageFile(imageView.context)

        /*외부저장소 접근 가능여부*/
        if(save.isExternalStorageWritable()){//접근 가능
            //저장
            save.saveImage(imageView, activity)

            //저장메시지 Log.e
            Log.e("Save", "Success")
            // 저장메시지 ToastView
            val location = IntArray(2)
            card_box.getLocationOnScreen(location)
            var toastY : Int = screenHeight - location[1] - card_box.height
            var toastX : Int = (card_box.measuredWidth - card_box_cover.measuredWidth) / 2

            Toast(activity).apply {
                view = messagesaved
                duration = Toast.LENGTH_SHORT
                setGravity(Gravity.BOTTOM, 0,toastY)//toastX, toastY)
            }.show().run{
                mCountDown.start()
            }
        }
        else//외부저장소 접근 여부
            Log.e("Save", "Success")

    }


    private fun ButtonSavingImage(imageview : ImageView, activity : Activity,
        messageSaved : androidx.cardview.widget.CardView) : Boolean{

        fun floatMessage(message : androidx.cardview.widget.CardView){
            val location = IntArray(2)
            card_box.getLocationOnScreen(location)

            val toastY : Int = screenHeight - location[1] - card_box.height
            val toastX : Int = (card_box.measuredWidth - card_box_cover.measuredWidth) / 2

//            message.layoutParams.width = card_box.width
//            val handler = Handler(Looper.getMainLooper())            handler.postDelayed({ ToastMessage.cancel() }, 5000)
            val title : TextView = activity.findViewById(R.id.Title)
            Toast(activity).apply {
                view = message
                duration = Toast.LENGTH_SHORT
                setGravity(Gravity.BOTTOM, 0,0)//toastX, toastY)
            }.run{
                mCountDown.start()
            }
            title.text = "FUCK you"
        }

        if(!isNetworkConnected(activity)){
            val imageView : ImageView = activity.findViewById(R.id.Current_picture)
            val save = SaveImageFile(imageView.context)

            /*외부저장소 접근 가능여부*/
            if(!save.isExternalStorageWritable()){//접근 실패
                return false
            }
            else {//접근 후 저장 완료.. --> "저장했습니다" 메시지 출력
                /*1. 저장*/
                save.saveImage(imageView, activity)

                /*2. 메시지 출력 . "저장되었습니다"*/
                val location = IntArray(2)
                card_box.getLocationOnScreen(location)

                val toastY : Int = screenHeight - location[1] - card_box.height
                val toastX : Int = (card_box.measuredWidth - card_box_cover.measuredWidth) / 2

                messageSaved.layoutParams.width = card_box.width
                Toast(activity).apply {
                    view = messageSaved
                    duration = Toast.LENGTH_SHORT
                    setGravity(Gravity.BOTTOM, toastX, toastY)
                }.run{
                    mCountDown.start()
                }

                return true
            }
        }
        else{//네트워크 연결 X
            /*메시지 출력 . "네트워크 연결을 확인해 주세요."*/
            floatMessage(messageNotConnection)
            return false
        }
    }

    /*button0002 : 공유*/
    private fun ButtonSharingImage(){
        val imageview : ImageView= activity.findViewById(R.id.Current_picture);
        val storage = Firebase.storage
        val storageRef = storage.reference
        val islandRef = storageRef.child(arrayFileNames[current_picture_index])
        val ONE_MEGABYTE: Long = 1024 * 1024
        var shareUri : Uri

        islandRef.downloadUrl.addOnSuccessListener { uri ->
            val shareIntent : Intent = Intent().apply{
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/jpeg"
            }
            activity.startActivity(Intent.createChooser(shareIntent, activity.resources.getText(R.string.app_name)))
            Log.e("공유","성공")
        }.addOnFailureListener {
            // Handle any errors
            Log.e("Sharing","failure")
        }
    }

    /*button0003 : 앱정보*/
    private fun ButtonAppInfo(drawerAppInfoInput : androidx.drawerlayout.widget.DrawerLayout){
        drawerAppInfoInput.openDrawer(Gravity.LEFT)
    }
}