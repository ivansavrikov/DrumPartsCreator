package com.example.courseproject.database

import android.provider.BaseColumns

object DBConstants :BaseColumns {
    const val DATABASE_NAME = "R808 MACHINE DATABASE"
    const val DATABASE_VERSION = 1

    const val DATABASE_TABLE = "Project"
    const val COLUMN_NAME_ID = BaseColumns._ID
    const val COLUMN_NAME_TITLE = "title"
    const val COLUMN_NAME_DATA = "data"

    const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $DATABASE_TABLE($COLUMN_NAME_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME_TITLE TEXT, $COLUMN_NAME_DATA TEXT)"
}