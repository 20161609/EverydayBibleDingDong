package com.android.everydaybyble_dingdong

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleOwner
import java.time.Month
import java.time.Year
import androidx.lifecycle.Observer
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

public class excuteButton(
    inputLifecycleOwner: LifecycleOwner,
    inputMainactivity : Activity,
    inputMessages : Array<androidx.cardview.widget.CardView>,
    inputDrawerAppInfoInput : androidx.drawerlayout.widget.DrawerLayout) {

    /*-- Variables --*/
    /*Run Button*/
    private val activity : Activity = inputMainactivity // MainActivity
    private val messagesaved = inputMessages[0] // 메시지0 : "저장되었습니다"
    private val messageNotConnection = inputMessages[1] // 메시지1 : "네트워크를 확인해주세요."
    private val drawerAppInfo = inputDrawerAppInfoInput // Drawer로 출력할 앱정보
    private val lifecycleOwner = inputLifecycleOwner

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
        if(buttonBlocked || drawerAppInfo.isDrawerOpen(Gravity.LEFT)) return

        when(Clicked){
            0 -> ButtonSelectingImage()//어제말씀
            1 -> ButtonSavingImage(activity.findViewById(R.id.Current_picture),
                activity, messagesaved)//저장
            2 -> ButtonSharingImage()//공유
            3 -> ButtonAppInfo(drawerAppInfo)//앱정보
        }
    }

    /*button0000 : 어제 말씀*/
    private fun ButtonSelectingImage(){
        /*A. Make frame ("최근 일주일 말씀입니다", (x), "00월", "0 1 2 3 4 5 6")*/
        /*A.1. 전체 frame 그리기*/
        val home2 = LinearLayout(activity)
        home2.setBackgroundColor(Color.parseColor("#DDDDDD"))
        home2.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            230
        )
        home2.orientation = LinearLayout.VERTICAL

        /*A.2. "최근 일주일 말씀입니다."*/
        val home2_0 = LinearLayout(activity)
        val layoutParams2_0 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams2_0.setMargins(20,0,0,0)
        home2_0.layoutParams = layoutParams2_0
        home2_0.orientation = LinearLayout.HORIZONTAL

        val home2_0_0 = TextView(activity)
        home2_0_0.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        home2_0_0.setText("최근 일주일 말씀입니다.")
        home2_0_0.setTypeface(home2_0_0.typeface, Typeface.BOLD)
        home2_0_0.setTextSize(DP, 37.toFloat())

        /*A.3. (X) 뒤로가기 버튼*/
        val home2_0_1 = androidx.appcompat.widget.AppCompatButton(activity)
        val layoutParams2 = LinearLayout.LayoutParams(60, 60)
        layoutParams2.setMargins(350,20,0,0)
        home2_0_1.layoutParams = layoutParams2
        home2_0_1.setBackgroundResource(android.R.drawable.presence_offline)

        val home2_1 = LinearLayout(activity)
        val layoutParams3 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        home2_1.layoutParams = layoutParams3
        home2_1.orientation = LinearLayout.VERTICAL

        /*A.4. N월*/
        val home2_1_0 = TextView(activity)
        val layoutParams2_1_0 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams2_1_0.setMargins(35,10,0,0)
        home2_1_0.layoutParams = layoutParams2_1_0
        home2_1_0.setText(today.valueMonth.toString() + "월")
        home2_1_0.setTextSize(DP, 30.toFloat())



        /*B. Making Buttons*/
        /*B.1. 버튼 frame 그리기*/
        val home2_2 = LinearLayout(activity)
        home2_2.orientation = LinearLayout.HORIZONTAL
        home2_2.gravity = Gravity.CENTER
        home2_2.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0,20,0,0)
        }
        /*B.2. Drawing Buttons(날짜 selection)*/
        var Button_Set : Array<TextView> = arrayOf() // selecions of 7 days
        for (i in 0..6){
            val date_button = TextView(activity)//androidx.appcompat.widget.AppCompatButton(this)
            var Margin = 52
            if(i==0) Margin = 0

            date_button.layoutParams = LinearLayout.LayoutParams(
                65,65
            ).apply {
                setMargins(Margin,0,0,0)
            }
            if(is_first_Clicked && current_picture_index == i) {
                date_button.setBackgroundResource(R.drawable.shape_for_circle_button_clicked)
                date_button.setTextColor(Color.WHITE)
            }
            else{
                date_button.setBackgroundResource(R.drawable.shape_for_circle_button)
                date_button.setTextColor(Color.BLACK)
            }
            date_button.setGravity(Gravity.CENTER)
            date_button.isClickable = true
            date_button.text = arrayFileNames[i].substring(6,8)
            date_button.setStateListAnimator(null)
            Button_Set = Button_Set.plus(date_button)
        }

        /*B.3. Running Buttons(날짜 selection)*/
        for(Clicked in 0..6){
            Button_Set[Clicked].setOnClickListener(){
                /*1. 네트워크 연결 여부*/
                val connection = NetworkConnection(activity.applicationContext)
                connection.observe(lifecycleOwner, Observer { isConnected ->
                    if (isConnected) {//1.1. 네트워크 연결 성공

                        /*onCreate후, (다른 날짜)클릭한적 있는지 여부. */
                        if(is_first_Clicked){//클릭한 적 있다.
                            if(Clicked != current_picture_index){//현재 select된 날짜와 다른 날짜만 select가능
                                /*기존버튼 해제*/
                                Button_Set[current_picture_index].setBackgroundResource(
                                    R.drawable.shape_for_circle_button
                                )
                                Button_Set[current_picture_index].setTextColor(Color.BLACK)

                                /*클릭된 버튼 표식*/
                                current_picture_index = Clicked
                                Button_Set[current_picture_index].setBackgroundResource(
                                    R.drawable.shape_for_circle_button_clicked
                                )
                                today.today_card(activity.findViewById(R.id.Current_picture))
                                Button_Set[current_picture_index].setTextColor(Color.WHITE)
                            }
                        }
                        else{//클릭한 적 없다.
                            is_first_Clicked = true//(다른 날짜)클릭한적 있는지 여부

                            /*클릭된 버튼 표식*/
                            current_picture_index = Clicked
                            Button_Set[current_picture_index].setBackgroundResource(
                                R.drawable.shape_for_circle_button_clicked
                            )
                            Button_Set[current_picture_index].setTextColor(Color.WHITE)
                            today.today_card(activity.findViewById(R.id.Current_picture))
                        }
                    }
                    else {//1.2. 네트워크 연결 실패 --> 메시지 "네트워크 연결을 확인해주세요" 출력
                        Toast(activity).apply {
                            setGravity(Gravity.CENTER, 0, 550)
                            view = messageNotConnection
                            duration = Toast.LENGTH_SHORT
                        }.show().run{
                            mCountDown.start()
                        }
                    }
                })
            }
            home2_2.addView(Button_Set[Clicked])
        }

        /*C. Add all Views*/
        home2_0.addView(home2_0_0)
        home2_0.addView(home2_0_1)
        home2.addView(home2_0)

        home2_1.addView(home2_1_0)
        home2.addView(home2_1)

        home2.addView(home2_2)
        val home1 : LinearLayout = activity.findViewById(R.id.button_box)

        /*D. A~C에서 완성한 뷰어 하단에 추가*/
        val card_box : androidx.cardview.widget.CardView = activity.findViewById(R.id.card_box)
        card_box.removeAllViews()
        card_box.addView(home2)

        /*E. Back버튼*/
        home2_0_1.setOnClickListener(){
            card_box.removeAllViews()
            card_box.addView(home1)
        }
    }

    /*button0001 : 저장*/
    private fun ButtonSavingImage(imageview : ImageView, activity : Activity,
        messageSaved : androidx.cardview.widget.CardView) : Boolean{

        var result : Boolean = true
        val connection = NetworkConnection(activity.applicationContext)
        connection.observe(lifecycleOwner, Observer { isConnected ->
            if (isConnected) {//네트워크 연결 성공
                /*저장실행*/
                val imageView : ImageView = activity.findViewById(R.id.Current_picture)
                val save = SaveImageFile(imageView.context)

                /*외부저장소 접근 가능여부*/
                if(save.isExternalStorageWritable() == false){//접근 실패
                    result = false
                }
                else {//접근 후 저장 완료.. --> "저장했습니다" 메시지 출력
                    save.saveImage(imageView, activity)
                    Toast(activity).apply {
                        view = messageSaved
                        duration = Toast.LENGTH_SHORT
                        setGravity(Gravity.CENTER, 0, 550)
                    }.show().run{
                        mCountDown.start()
                    }
                }
            }
            else{//네트워크 연결 실패
                Toast(activity).apply {
                    view = messageNotConnection
                    duration = Toast.LENGTH_SHORT
                    setGravity(Gravity.CENTER, 0, 550)
                }.show().run{
                    mCountDown.start()
                }
            }
        })

        return result
    }

    /*button0002 : 공유*/
    private fun ButtonSharingImage(){
        val imageview : ImageView= activity.findViewById(R.id.Current_picture);
        val storage = Firebase.storage
        val storageRef = storage.reference
        val islandRef = storageRef.child(arrayFileNames[current_picture_index])
        val ONE_MEGABYTE: Long = 1024 * 1024

        var sex : Int = 123
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
            Log.e("getBytes","failure")
        }
    }

    /*button0003 : 앱정보*/
    private fun ButtonAppInfo(drawerAppInfoInput : androidx.drawerlayout.widget.DrawerLayout){
        drawerAppInfoInput.openDrawer(Gravity.LEFT)
    }
}