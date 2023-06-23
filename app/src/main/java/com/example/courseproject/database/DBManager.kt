package com.example.courseproject.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class DBManager(context: Context) {
    var dbHelper = DBHelper(context)
    var db : SQLiteDatabase? = null

    fun open(){
        db = dbHelper.writableDatabase
    }

    fun insert(title: String, data: String){
        val values = ContentValues().apply {
            put(DBConstants.COLUMN_NAME_TITLE, title)
            put(DBConstants.COLUMN_NAME_DATA, data)
        }
        db?.insert(DBConstants.DATABASE_TABLE, null, values)
    }

    fun getProject(id: Int): String {
        val selection = "${DBConstants.COLUMN_NAME_ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = db?.query(DBConstants.DATABASE_TABLE, null, selection, selectionArgs, null, null, null)
        cursor?.moveToFirst()

        val data = cursor?.getString(cursor.getColumnIndexOrThrow(DBConstants.COLUMN_NAME_DATA))

        return data.toString()
    }

    fun close(){
        db?.close()
    }
}