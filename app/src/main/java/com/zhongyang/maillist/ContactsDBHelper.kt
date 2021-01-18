package com.zhongyang.maillist

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * @项目名称 MailList
 * @类名 ContactsDBHelper
 * @包名 com.zhongyang.maillist
 * @创建时间 2021/1/18 15:18
 * @作者 钟阳
 * @描述
 */
class ContactsDBHelper(context: Context, name: String, code: Int) :
    SQLiteOpenHelper(context, name, null, code) {

    private val tag = "ContactsDBHelper"

    override fun onCreate(db: SQLiteDatabase) {
        /*创建数据库时回调此方法*/
        Log.d(tag, "创建了数据库...")
        /*创建联系人表*/
        val createCallTb = "create table " + Constants.TB_NAME_CALL + " (" +
                "_id integer primary key autoincrement," +
                "name text," +
                "call text)"
        db.execSQL(createCallTb)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}