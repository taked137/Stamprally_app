package com.example.nakatsuka.newgit.navigationAction

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.nakatsuka.newgit.R
import com.example.nakatsuka.newgit.mainAction.MainActivity
import com.example.nakatsuka.newgit.mainAction.controller.api.ApiController
import com.example.nakatsuka.newgit.mainAction.controller.beacon.BeaconController
import com.example.nakatsuka.newgit.mainAction.lifecycle.ActivityLifeCycle
import com.example.nakatsuka.newgit.mainAction.lifecycle.IActivityLifeCycle
import com.example.nakatsuka.newgit.mainAction.model.api.ImageData
import com.example.nakatsuka.newgit.mainAction.model.api.ImageResponse
import com.example.nakatsuka.newgit.mainAction.model.beacon.MyBeaconData
import kotlinx.android.synthetic.main.fragment_stamp.*
import org.altbeacon.beacon.BeaconConsumer
import retrofit2.Response


/**配列の最大数を7つ*/
class StampFragment : Fragment()  {

    var TAG = this.javaClass.simpleName

    interface fragmentListner {
        fun goActivity(answerNumber: Int, isSend: Boolean, imageURL: String)
        fun take1(answerNumber: Int)
        fun goActivity(answerNumber: Int, isSend: Boolean)
        fun takeGoal()
        fun saveURL(quizCode: Int,imageURL:String)
        fun isGoal():Boolean
    }

    lateinit var a: fragmentListner

    private val isGot = arrayOf(null, false, false, false, false, false, false)

    //private val buttonResult = mutableListOf(0, 0, 0, 0, 0, 0, 0)
    //private var imageUrl = mutableListOf("", "", "", "", "", "", "")
    lateinit var texts: Array<TextView?>
    var data = arrayOf<ImageData?>(null, null, null, null, null, null)

    lateinit var uuid: String
    lateinit var userName: String

    private var cheatMode = false
    private var choice = false

    private var isProcessing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        a = activity as fragmentListner

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        mBeaconController = BeaconController(activity as Context)
        val view = inflater.inflate(R.layout.fragment_stamp, container, false)
        val buttonResult: IntArray = arguments!!.getIntArray("buttonResult")



        texts = arrayOf(
                null,
                view!!.findViewById(R.id.text1),
                view!!.findViewById(R.id.text2),
                view!!.findViewById(R.id.text3),
                view!!.findViewById(R.id.text4),
                view!!.findViewById(R.id.text5),
                view!!.findViewById(R.id.text6)
        )

        for (i in 1..6) {
            if (buttonResult[i] == 1)
                texts[i]!!.text = "\n\n問題取得済み"
            else texts[i]!!.text = "\n\n問題未取得"
        }

        //lifecycle.addObserver(ActivityLifeCycle(this))
        uuid = arguments!!.getString("UUID", "")
        userName = arguments!!.getString("USERNAME", "")

        val buttons = arrayOf<Button?>(
                null,
                view.findViewById(R.id.imageButton1),
                view.findViewById(R.id.imageButton2),
                view.findViewById(R.id.imageButton3),
                view.findViewById(R.id.imageButton4),
                view.findViewById(R.id.imageButton5),
                view.findViewById(R.id.imageButton6)
        )
        for (i in 1..6)
            buttons[i]!!.setOnClickListener {
                if (!isProcessing) {
                    event(i)
                }
            }

//        if (!choice) {
//            AlertDialog.Builder(activity)
//                    .setMessage("デバッグモードを使用しますか？")
//                    .setPositiveButton("はい") { _, _ ->
//                        cheatMode = true
//                    }
//                    .setNegativeButton("いいえ", null)
//                    .show()
//            choice = true
//        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buttonResult: IntArray = arguments!!.getIntArray("buttonResult")
        //a!!.goActivity(1,true)


        for (i in 1..6) {
            if (buttonResult[i] == 2 || buttonResult[i] == 3) {

                when (i) {
                    1 -> {
                        imageButton1.setBackgroundResource(0)
                        imageButton1.text = ""
                        text1.text = ""
                    }
                    2 -> {
                        imageButton2.setBackgroundResource(0)
                        imageButton2.text = ""
                        text2.text = ""
                    }
                    3 -> {
                        imageButton3.setBackgroundResource(0)
                        imageButton3.text = ""
                        text3.text = ""
                    }
                    4 -> {
                        imageButton4.setBackgroundResource(0)
                        imageButton4.text = ""
                        text4.text = ""
                    }
                    5 -> {
                        imageButton5.setBackgroundResource(0)
                        imageButton5.text = ""
                        text5.text = ""
                    }
                    6 -> {
                        imageButton6.setBackgroundResource(0)
                        imageButton6.text = ""
                        text6.text = ""
                    }
                }
            }
        }
    }

    //ProgressDialog機能を追加させました
    //ゴール時の反応を追加しました
    private fun event(quizNumber: Int) {
        val uuid = arguments!!.getString("UUID")
        val buttonResult: IntArray = arguments!!.getIntArray("buttonResult")

        if (buttonResult[quizNumber] == 0) {
            isProcessing = true
            val mProgressDialog = ProgressDialog.newInstance("ビーコン取得中...")
            mProgressDialog.setTargetFragment(this, 100)

            val thread = Thread(Runnable {
                try {
                    Handler(Looper.getMainLooper()).post {
                        mProgressDialog.show(activity!!.supportFragmentManager, "dialog")
                    }
                    Thread.sleep(1000)
                    isProcessing = mProgressDialog.brk
                    Handler(Looper.getMainLooper()).post {
                        mProgressDialog.move()
                    }
                    Thread.sleep(2050)
                    isProcessing = mProgressDialog.brk
                    Handler(Looper.getMainLooper()).post {
                        mProgressDialog.dismiss()
                    }
                    Thread.sleep(1000)
                    isProcessing = false
                } catch (e: Exception) {
                }
            })
            thread.start()

            //TODO delete by koudaisai
//            val cheat = mutableListOf<MyBeaconData>()
//            if (cheatMode)
//                for (i in 1..9)
//                    when (quizNumber) {
//                        1 ->
//                            cheat.add(MyBeaconData(442, 0))
//                        2 ->
//                            cheat.add(MyBeaconData(298, 0))
//                        3 ->
//                            cheat.add(MyBeaconData(91, 0))
//                        4 ->
//                            cheat.add(MyBeaconData(503, 0))
//                        5 ->
//                            cheat.add(MyBeaconData(844, 0))
//                        6 ->
//                            cheat.add(MyBeaconData(1212, 0))
//                    }
//            Log.d("DEBAG",activity.toString())
//            Log.d("DEBAG",uuid)
//            Log.d("DEBAG",quizNumber.toString())
//            Log.d("DEBAG",cheat.toString())
            //Log.d("DEBAG",)
//            Log.d("DEBAG",mBeaconController.toString())

            (activity as MainActivity).rangeBeacon {
                Log.d("DEBAG", "Rangebeacon called from stampfragment")
                if (mProgressDialog.brk) {
//                    if (cheatMode) {
//                        mApiController.requestImage(activity, uuid, quizNumber, cheat, requestImagesFunc(quizNumber))
//                        Log.d("DEBAG", requestImagesFunc(quizNumber).toString())
//                    } else
                        mApiController.requestImage(activity, uuid, quizNumber, it as MutableList<MyBeaconData>, requestImagesFunc(quizNumber))
                }
            }
            Log.d("DEBAG","end")
        }

        if (buttonResult[quizNumber] == 1) {
            Log.d("quiznumber", quizNumber.toString())
            val imageURL = arguments!!.getStringArrayList("imageURL")
            a!!.goActivity(quizNumber, true,  imageURL[quizNumber])
        }
        /*if(buttonResult[quizNumber] == 2){
        val completed = "すでにスタンプは押されています"
            AlertDialog.Builder(activity)
                    .setTitle("結果")
                    .setMessage(completed)
                    .setPositiveButton("OK") { _, _ ->

                    }
                    .show()

        }*/

        val goalApiIsCalled = a!!.isGoal()
        Log.d("buttonResult", buttonResult[quizNumber].toString())
        if (!goalApiIsCalled) {
            if (buttonResult[quizNumber] == 3) {
                val builder = AlertDialog.Builder(activity)
                        .setTitle("ゲームクリア")
                        .setMessage("ゲームを終了しますか？")
                        .setPositiveButton("OK") { _, _ ->

                            val mApiController = ApiController()
                            mApiController.requestGoal(uuid) { response ->
                                when (response.code()) {
                                    200 -> {

                                        response.body()?.let {
                                            a!!.takeGoal()
                                            Log.d("",goalApiIsCalled.toString())

                                            val builder = AlertDialog.Builder(activity)
                                                    .setTitle("クリア済")
                                                    .setMessage("$userName さん、クリアおめでとうございます！景品受取所まで景品(数に限りがございます)を受け取りにお越しください！")
                                                    .setPositiveButton("OK") { _, _ ->
                                                    }
                                            builder.show()
                                        }
                                    }

                                    else -> Log.d("goaldesu", response.code().toString())
                                }
                            }

                        }
                        .setNegativeButton("キャンセル") { _, _ ->
                        }
                builder.show()
            }
        } else {
            val builder = AlertDialog.Builder(activity)
                    .setTitle("クリア済")
                    .setMessage("$userName さん、クリアおめでとうございます！景品受取所まで景品(数に限りがございます)を受け取りにお越しください！")
                    .setPositiveButton("OK") { _, _ ->
                    }
            builder.show()
        }
    }


    /*Beacon通信用のいろいろ*/
    //Controller関係
    val mApiController = ApiController()
    lateinit var mBeaconController: BeaconController
    //関数型オブジェクトを返す高階関数(?)です。受け取ったquizCodeの値に応じて、関数型オブジェクトを返します。
    //imageRequest時に実行
    val requestImagesFunc = fun(quizCode: Int): (Response<ImageResponse>) -> Unit {
        val go = fun(response: Response<ImageResponse>) {
            Log.d("requestImagesFunc",response.code().toString())
            //val buttonResult: IntArray = arguments!!.getIntArray("buttonResult")
            when (response.code()) {
                200 -> {
                    val buttonResult: IntArray = arguments!!.getIntArray("buttonResult")

                    if (response.body()!!.isSend) {
                        response.body()?.let {
                            data[quizCode - 1] = ImageData(it.quizCode, it.isSend, it.imageURL)
                           // imageUrl[quizCode] = it.imageURL
                            val isSend = it.isSend
                            buttonResult[quizCode] = 1

                            a!!.take1(quizCode)
                            a!!.saveURL(quizCode,it.imageURL)

                            texts[quizCode]!!.text = "\n\n問題取得済み"
                            isGot[quizCode] = true

                            AlertDialog.Builder(activity)
                                    .setTitle("判定結果")
                                    .setMessage(R.string.dialog_in_area)
                                    .setPositiveButton("問題へ") { _, _ ->
                                        a!!.goActivity(quizCode, isSend,it.imageURL)

                                    }
                                    .setNegativeButton("戻る") { _, _
                                        ->
                                    }.show()

                        }
                    } else {
                        AlertDialog.Builder(activity)
                                .setTitle(R.string.dialog_out_of_area)
                                .setMessage("もう一度試してみて下さい！")
                                .setPositiveButton("OK") { _, _
                                    ->
                                    //Do Nothing
                                }.show()
                    }
                }
                else -> {
                    AlertDialog.Builder(activity)
                            .setTitle("通信エラー")
                            .setMessage(R.string.dialog_connection_error)
                            .setPositiveButton("OK") { _, _
                                ->
                                //Do Nothing
                            }.show()
                }
            }
        }
        return go
    }

}


