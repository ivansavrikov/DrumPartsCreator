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

        cursor?.close()
        return data.toString()
    }

    fun getAllProjects(): HashMap<Int, String> {
        val columns = null // Получение всех столбцов
        val selection = null // Без условия WHERE
        val selectionArgs = null // Без аргументов для условия WHERE
        val sortOrder = "${DBConstants.COLUMN_NAME_ID} DESC"

        val cursor = db?.query(DBConstants.DATABASE_TABLE, columns, selection, selectionArgs, null, null, sortOrder)
        val projects = HashMap<Int, String>()

        if (cursor != null) {
            while (cursor.moveToNext()) {

                val id = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.COLUMN_NAME_TITLE))

                projects[id.toInt()] = title
            }

            cursor.close()
        }
        return projects
    }
    fun close(){
        db?.close()
    }
}