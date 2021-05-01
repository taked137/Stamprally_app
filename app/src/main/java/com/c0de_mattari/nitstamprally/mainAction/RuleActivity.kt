package com.c0de_mattari.nitstamprally.mainAction

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.c0de_mattari.nitstamprally.R
import com.c0de_mattari.nitstamprally.mainAction.controller.api.ApiController
import kotlinx.android.synthetic.main.activity_rule.*

class RuleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rule)

        // 利用規約をサーバから受信
        // サーバ停止中につき固定値を使用
        rule_contents.text = "本来は利用規約が表示されます\n(現在はサーバ停止中)"

//        val mApiController = ApiController()
//        mApiController.getRegulation(this, agree_contents, rule_title) { response ->
//            when (response.code()) {
//                200 -> {
//                    response.body()?.let {
//                        rule_contents.text = it.ruleText
//                    }
//                }
//            }
//        }

        agree_contents.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        disagree.setOnClickListener {
            finish()
        }
    }

    fun jump() {
        finish()
    }
}
