package com.android.everydaybyble_dingdong

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.CountDownTimer
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.GravityCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class excuteButton(
    private val activity : Activity,
    private val day_list_cover: androidx.cardview.widget.CardView,
    inputMessages : Array<androidx.cardview.widget.CardView>,
    private val screenHeight: Int,
    private val v_animation: com.airbnb.lottie.LottieAnimationView
) {

    /*-- Variables --*/
    /*Run Button*/
    private val card_box : androidx.cardview.widget.CardView = activity.findViewById<CardView?>(R.id.card_box)
    private val card_box_cover : androidx.cardview.widget.CardView = activity.findViewById(R.id.card_box_cover)

    private val drawerAppInfo :  androidx.drawerlayout.widget.DrawerLayout = activity.findViewById(R.id.drawerAppInfo)
    private val messagesaved = inputMessages[0] // 메시지0 : "저장되었습니다"
    private val messageNotConnection = inputMessages[1] // 메시지1 : "네트워크를 확인해주세요."

    private var toastX : Int = 0
    private var toastY : Int = 0

    /*메시지 출력동안 버튼 block상태*/
    private val mCountDown : CountDownTimer = object : CountDownTimer(2000, 5000) {
        override fun onTick(millisUntilFinished: Long) {
            buttonBlocked = true
        }

        override fun onFinish() {
            buttonBlocked = false
        }
    }

    /*-- Functions --*/
    /*button Run*/
    fun run(Clicked : Int){
        val location = IntArray(2)
        card_box.getLocationOnScreen(location)
        toastY = screenHeight - location[1] - card_box.height
        toastX = location[0]
        Log.e(toastY.toString(), toastX.toString())

        if(buttonBlocked || drawerAppInfo.isDrawerOpen(GravityCompat.START)) return
        when(Clicked){
            0 -> ButtonSelectingImage()//어제말씀
            1 -> ButtonSavingImage1()//저장
            2 -> ButtonSharingImage()//공유
            3 -> ButtonAppInfo(drawerAppInfo)//앱정보
        }
    }

    /*button0000 : 어제 말씀*/
    private fun ButtonSelectingImage(){
        /*각 date에 따른 selection 후 UI변환 함수들*/
        fun UI_clicked(button : Button){
            button.setBackgroundResource(R.drawable.shape_for_circle_button_clicked)
            button.setTextColor(Color.WHITE)
        }
        fun UI_unclicked(button : Button){
            button.setBackgroundResource(R.drawable.shape_for_circle_button)
            button.setTextColor(Color.BLACK)
        }

        /*button Action Init : 기존 UI변환*/
        val Entire_box : LinearLayout = activity.findViewById(R.id.Entire_box)
        Entire_box.removeView(card_box_cover)
        Entire_box.addView(day_list_cover)

        /*버튼1 : 날짜 선택*/
        val textMonth : TextView = activity.findViewById(R.id.textMonth)
        textMonth.text = daySelection[6].month.toString() + "월"

        val buttonArray : Array<Int> = arrayOf(R.id.day0, R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6)
        for (i in 0..6){
            /*a. user가 클릭한 버튼 표식*/
            val button : Button = activity.findViewById(buttonArray[i])
            button.text = (daySelection[i].day + 100).toString().substring(1, 3)//arrayFileNames[i].substring(6,8)
            if(i == dayClicked)
                UI_clicked(button)

            /*b. 버튼 기능 수행.*/
            button.setOnClickListener(){
                if(!isNetworkConnected(activity)){// -> b.1. 네트워크 연결 불가(메시지 출력)
                    val toastMessage : Toast = Toast(activity).apply {
                        view = messageNotConnection
                        duration = Toast.LENGTH_LONG
                        setGravity(Gravity.BOTTOM or Gravity.START, toastX, toastY)// -toastX, toastY)
                    }
                    val customDuration = 1800 // 0.5 seconds
                    val handler = Handler()
                    handler.postDelayed({ toastMessage.cancel() }, customDuration.toLong())
                    toastMessage.show().run{
                        mCountDown.start()
                    }
                }
                else if(dayClicked == -1){// -> b.2. 네트워크 연결 가능 -> 버튼 클릭이 안된 상태.
                    /*날짜 갱신*/
                    dayClicked = i
                    current_picture_index = dayClicked
                    UI_clicked(button)

                    if(i != 6)// -> 오늘이 아닌 날짜 선택.
                        ImageCaching(activity.findViewById(R.id.Current_picture), i)
                }else if(dayClicked != i){// -> b.3. 네트워크 연결 가능 -> 오늘이 아닌 잘짜 선택
                    //기존 버튼 해제
                    UI_unclicked(activity.findViewById(buttonArray[dayClicked]))

                    //클릭된 버튼으로 갱신
                    UI_clicked(button)

                    /*날짜 및 이미지 갱신*/
                    dayClicked = i
                    current_picture_index = dayClicked
                    ImageCaching(activity.findViewById(R.id.Current_picture), i)
                }
            }
        }

        /*버튼2 : 뒤로 가기*/
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
        if(save.isExternalStorageWritable()){// -> a. 외부저장소 접근 가능
            //a.1. 저장
            save.saveImage(imageView, activity)

            //a.2. 저장메시지 ToastView
            val toastMessage : Toast = Toast(activity).apply {
                view = messagesaved
                duration = Toast.LENGTH_LONG
                setGravity(Gravity.BOTTOM or Gravity.START, toastX, toastY)
            }
            val customDuration = 1800 // 1.8 seconds
            val handler = Handler()
            handler.postDelayed({ toastMessage.cancel() }, customDuration.toLong())
            toastMessage.show().run{
                mCountDown.start()
                v_animation.playAnimation()
            }
            //a.3. 저장메시지 : LogCat
            Log.e("Save", "Success")
        }
        else// -> b. 외부저장소 접근 불가
            Log.e("Save", "Fail")
    }

    /*button0002 : 공유*/
    private fun ButtonSharingImage(){
        val imageView: ImageView = activity.findViewById(R.id.Current_picture)

        // Get the bitmap from the ImageView
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap

        // Create a new Intent to share the image
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(activity.contentResolver, bitmap, "Title", null)
        val imageUri = Uri.parse(path)
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)

        // Show the share menu
        activity.startActivity(Intent.createChooser(shareIntent, "Share Image"))

        return
        val imageview : ImageView= activity.findViewById(R.id.Current_picture);
        val storage = Firebase.storage
        val storageRef = storage.reference
        val islandRef = storageRef.child(arrayFileNames[current_picture_index])

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
        val d : androidx.drawerlayout.widget.DrawerLayout = activity.findViewById(R.id.drawerAppInfo)
        d.openDrawer(GravityCompat.START)
//        drawerAppInfoInput.openDrawer(GravityCompat.START)
    }
}