package com.example.nakatsuka.newgit.MainAction

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget
import com.example.nakatsuka.newgit.NavigationAction.FirstFragment
import com.example.nakatsuka.newgit.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val buttonResult = mutableListOf<Boolean>(false,false,false,false,false,false)
    val RESULT_SUBACTIVITY:Int = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val APITest: APITest = intent.extras.get("APITest") as APITest
        val infor = APITest.GetandPostUserJSON()
        userInformation.text = infor
        imageButton0.setOnClickListener{
            val answerNumber:Int = 0
            goActivity(answerNumber)
        }
        imageButton1.setOnClickListener{
            val answerNumber:Int = 1
            goActivity(answerNumber)
        }
        imageButton2.setOnClickListener{
            val answerNumber:Int = 2
            goActivity(answerNumber)
        }
        imageButton3.setOnClickListener{
            val answerNumber:Int = 3
            goActivity(answerNumber)
        }
        imageButton4.setOnClickListener{
            val answerNumber:Int = 4
            goActivity(answerNumber)
        }
        imageButton5.setOnClickListener{
            val answerNumber:Int = 5
            goActivity(answerNumber)
        }

        //GlideによるGIFの追加
        val target = GlideDrawableImageViewTarget(gifView)
        Glide.with(this).load(R.raw.icon_loader_f_ww_01_s1).into(target)

        //ナビゲーションに関するボタンの操作
        val fragmentManager : FragmentManager = supportFragmentManager
        stamp_rally.setOnClickListener {
            fragmentManager.beginTransaction()
                    .replace(R.id.container,FirstFragment())
                    .commit()
        }
    }

    override fun onActivityResult(requestCode:Int,resultCode:Int,intent: Intent?){
        super.onActivityResult(requestCode,resultCode,intent)


        if(requestCode == RESULT_SUBACTIVITY){
            if(resultCode == result_canceled){
                Log.d("cancel","cancelcalled")
            }else if(resultCode == RESULT_OK) {


                var answerNumber:Int? = intent?.getIntExtra("answerNumber",6)
                buttonResult[answerNumber!!] = true
                Log.d("startOnActivityResult","MainActivity")

                when (answerNumber) {
                    0 -> if (buttonResult[0]) {
                        imageButton0.setImageResource(R.mipmap.ic_launcher)
                    }
                    1 -> if (buttonResult[1]) {
                        imageButton1.setImageResource(R.mipmap.ic_launcher)
                    }
                    2 -> if (buttonResult[2]) {
                        imageButton2.setImageResource(R.mipmap.ic_launcher)
                    }
                    3 -> if (buttonResult[3]) {
                        imageButton3.setImageResource(R.mipmap.ic_launcher)
                    }
                    4 -> if (buttonResult[4]) {
                        imageButton4.setImageResource(R.mipmap.ic_launcher)
                    }
                    5 -> if (buttonResult[5]) {
                        imageButton5.setImageResource(R.mipmap.ic_launcher)
                    }
                }
            }
        }
    }

    private fun goActivity(answerNumber:Int){
        var isAPI: Boolean
        var APITest = APITest()
        isAPI = APITest.getIsAPI()
        val intent = Intent(this, SecondActivity::class.java)
        if(buttonResult[answerNumber]){

            val completed:String = "すでにスタンプは押されています"
            makeToast(completed,0,-200)
        }else {
            intent.putExtra("AnswerNumber", answerNumber)
            if (isAPI) {
                startActivityForResult(intent, RESULT_SUBACTIVITY)
            }
        }
    }
    private fun makeToast(message:String,x:Int,y:Int){
        val toast: Toast = Toast.makeText(this,message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER,x,y)
        toast.show()
    }


}