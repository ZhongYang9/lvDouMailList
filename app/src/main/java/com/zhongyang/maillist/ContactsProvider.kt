package com.zhongyang.maillist

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.net.Uri

/**
 * @项目名称 MailList
 * @类名 ContactsProvider
 * @包名 com.zhongyang.maillist
 * @创建时间 2021/1/19 10:16
 * @作者 钟阳
 * @描述 电话表的内容提供者
 */
class ContactsProvider : ContentProvider() {

    private val mCallDir = 0//访问电话表中的所有数据
    private val mCallItem = 1//访问电话表中的单条数据
    private val mAuthority = "com.zhongyang.maillist.provider"//声明authority
    private var mHelper: ContactsDBHelper? = null

    private val uriMatcher by lazy {
        /*实例化UriMatcher*/
        val matcher = UriMatcher(UriMatcher.NO_MATCH)
        /*将期望匹配的内容URI格式传递进去*/
        matcher.addURI(mAuthority, Constants.TB_NAME_CALL, mCallDir)
        matcher.addURI(mAuthority, Constants.TB_NAME_CALL + "/#", mCallItem)
        /*by lazy代码块的返回值*/
        matcher
    }

    override fun onCreate() = context?.let {
        /*实例化数据库辅助类*/
        mHelper = ContactsDBHelper(it, Constants.DB_NAME, Constants.DB_VERSION_CODE)
        /*let结构体的返回值*/
        true
    } ?: false

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ) = mHelper?.let {
        val db = it.readableDatabase//获取数据库操作对象
        /*查询数据*/
        //使用when循环对解析的uri参数进行判断，并返回给变量cursor
        val cursor = when (uriMatcher.match(uri)) {
            mCallDir -> db.query(
                Constants.TB_NAME_CALL,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            )
            mCallItem -> {
                /*获取单条数据的id*/
                val callId = uri.pathSegments[1]
                /*调用查询方法*/
                db.query(
                    Constants.TB_NAME_CALL,
                    projection,
                    "id = ?",
                    arrayOf(callId),
                    null,
                    null,
                    sortOrder
                )
            }
            else -> null
        }
        cursor
    }

    override fun getType(uri: Uri) = when (uriMatcher.match(uri)) {
        mCallDir -> "vnd.android.cursor.dir/vnd.$mAuthority.${Constants.TB_NAME_CALL}"
        mCallItem -> "vnd.android.cursor.item/vnd.$mAuthority.${Constants.TB_NAME_CALL}"
        else -> null
    }

    override fun insert(uri: Uri, values: ContentValues?) = mHelper?.let {
        /*添加数据*/
        val db = it.writableDatabase
        val newData = when (uriMatcher.match(uri)) {
            mCallDir, mCallItem -> {
                val newCallId = db.insert(Constants.TB_NAME_CALL, null, values)
                Uri.parse("content://$mAuthority/${Constants.TB_NAME_CALL}/$newCallId")//解析标准内容URI
            }
            else -> null
        }
        /*返回添加的新数据*/
        newData
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) =
        mHelper?.let {
            /*删除数据*/
            val db = it.writableDatabase
            /*判断uri*/
            val delRows = when (uriMatcher.match(uri)) {
                mCallDir -> db.delete(Constants.TB_NAME_CALL, selection, selectionArgs)
                mCallItem -> {
                    val callId = uri.pathSegments[1]
                    db.delete(Constants.TB_NAME_CALL, "id = ?", arrayOf(callId))
                }
                else -> 0
            }
            /*返回删除行数*/
            delRows
        } ?: 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ) = mHelper?.let {
        /*更新数据*/
        val db = it.writableDatabase//获取数据库操作对象
        /*判断uri*/
        val updateRows = when (uriMatcher.match(uri)) {
            mCallDir -> db.update(Constants.TB_NAME_CALL, values, selection, selectionArgs)
            mCallItem -> {
                val callId = uri.pathSegments[1]
                db.update(Constants.TB_NAME_CALL, values, "id = ?", arrayOf(callId))
            }
            else -> 0
        }
        /*返回更新的行数*/
        updateRows
    } ?: 0
}