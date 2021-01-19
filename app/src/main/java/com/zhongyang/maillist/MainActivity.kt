package com.zhongyang.maillist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mHelper: ContactsDBHelper
    private val tag = "MainActivity"
    private val mContactsList = ArrayList<Friends>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*实例化数据库辅助类*/
        mHelper = ContactsDBHelper(this, Constants.DB_NAME, Constants.DB_VERSION_CODE)
        /*初始化事件*/
        initEvent()
    }

    private fun initAdapter() {
        /*设置布局管理器*/
        rv_frendsList.layoutManager = LinearLayoutManager(this)
        /*设置适配器*/
        val friendsAdapter = FriendsAdapter(mContactsList)
        rv_frendsList.adapter = friendsAdapter
    }

    private fun initEvent() {
        /*删除按钮点击事件*/
        btn_delete.setOnClickListener {
            /*获取姓名输入框*/
            val name = et_contactsName.text.toString()
            /*删除操作*/
            delLogic(name)
        }
        /*修改按钮点击事件*/
        btn_update.setOnClickListener {
            /*获取输入框内容*/
            val name = et_contactsName.text.toString()//联系人姓名
            val call = et_contactsCall.text.toString()//联系人电话
            /*修改操作*/
            updateLogic(call, name)
        }
        /*查询按钮点击事件*/
        btn_query.setOnClickListener {
            /*查询操作*/
            queryLogic()
            /*初始化适配器*/
            initAdapter()
        }
        /*添加按钮点击事件*/
        btn_add.setOnClickListener {
            /*获取输入框内容*/
            val name = et_contactsName.text.toString()//联系人姓名
            val call = et_contactsCall.text.toString()//联系人电话
            /*添加联系人逻辑*/
            addContactsLogic(name, call)
        }
    }

    private fun delLogic(name: String) {
        if (name.isNotEmpty()) {
            /*获取数据库操作对象*/
            val db = mHelper.readableDatabase
            /*删除数据*/
            db.delete(Constants.TB_NAME_CALL, "name = ?", arrayOf(name))
            /*清空输入框内容*/
            et_contactsName.setText("")
            /*提示用户*/
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
        } else {
            /*为空则提示用户*/
            Toast.makeText(this, "删除信息不能为空", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateLogic(call: String, name: String) {
        /*对输入内容进行判断*/
        if (name.isNotEmpty() && call.isNotEmpty()) {
            /*获取数据库操作对象*/
            val db = mHelper.readableDatabase
            /*修改操作*/
            val values = contentValuesOf("call" to call)
            db.update(Constants.TB_NAME_CALL, values, "name = ?", arrayOf(name))
            /*清空输入框内容*/
            et_contactsCall.setText("")
            et_contactsName.setText("")
            /*提示用户*/
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show()
        } else {
            /*为空则提示用户*/
            Toast.makeText(this, "修改信息不能为空", Toast.LENGTH_SHORT).show()
        }
    }

    private fun queryLogic() {
        /*获取数据库操作对象*/
        val db = mHelper.readableDatabase
        /*查询数据库*/
        val cursor = db.query(Constants.TB_NAME_CALL, null, null, null, null, null, null)
        /*先将集合清除数据*/
        mContactsList.clear()
        /*遍历游标所查数据*/
        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndex("name"))//姓名
            val call = cursor.getString(cursor.getColumnIndex("call"))//电话
            //                Log.d(tag, "姓名==>$name；电话==>$call")
            /*添加到集合*/
            val contacts = Friends(name, call)
            mContactsList.add(contacts)
        }
        /*关闭游标*/
        cursor.close()
    }

    private fun addContactsLogic(name: String, call: String) {
        /*对输入内容进行判断*/
        if (name.isNotEmpty() && call.isNotEmpty()) {
            /*不为空则添加到数据库*/
            val db = mHelper.writableDatabase
            val values = contentValuesOf("name" to name, "call" to call)
            db.insert(Constants.TB_NAME_CALL, null, values)
            /*清空输入框内容*/
            et_contactsCall.setText("")
            et_contactsName.setText("")
            /*提示用户*/
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show()
        } else {
            /*为空则提示用户*/
            Toast.makeText(this, "添加信息不能为空", Toast.LENGTH_SHORT).show()
        }
    }

}