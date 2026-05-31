package com.esamept12026.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GameRepositoryHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "game.db"
        const val DATABASE_VERSION = 1

        const val SQL_CREATE_TABLE = """
            CREATE TABLE ${GameResultsTable.TABLE_NAME} (
                ${GameResultsTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${GameResultsTable.COLUMN_SEQUENCE} TEXT NOT NULL,
                ${GameResultsTable.COLUMN_ERROR_INDEX} INTEGER NOT NULL
            );
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${GameResultsTable.TABLE_NAME}")
        onCreate(db)
    }
}