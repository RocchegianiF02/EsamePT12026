package com.esamept12026.model

import android.content.ContentValues
import android.database.Cursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

class GameRepository(private val dbHelper: GameRepositoryHelper) {

    //Converte una lista di stringhe in una stringa JSON
    private fun listToJson(list: List<String>): String {
        val jsonArray = JSONArray(list)
        return jsonArray.toString()
    }

    //Converte una stringa JSON in una lista di stringhe
    private fun jsonToList(json: String): List<String> {
        val jsonArray = JSONArray(json)
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list
    }

    suspend fun inserisci(result: GameResult): Long = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(GameResultsTable.COLUMN_SEQUENCE, listToJson(result.sequence))
            put(GameResultsTable.COLUMN_ERROR_INDEX, result.errorIndex)
        }
        db.insert(GameResultsTable.TABLE_NAME, null, values)
    }

    suspend fun elimina(id: Long): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete(
            GameResultsTable.TABLE_NAME,
            "${GameResultsTable.COLUMN_ID}=?",
            arrayOf(id.toString())
        )
    }

    suspend fun getAll(): List<GameResult> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            GameResultsTable.TABLE_NAME,
            null, null, null, null, null,
            "${GameResultsTable.COLUMN_ID} DESC"
        )
        val lista = mutableListOf<GameResult>()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(GameResultsTable.COLUMN_ID))
            val sequenceJson = cursor.getString(cursor.getColumnIndexOrThrow(GameResultsTable.COLUMN_SEQUENCE))
            val errorIndex = cursor.getInt(cursor.getColumnIndexOrThrow(GameResultsTable.COLUMN_ERROR_INDEX))
            lista.add(
                GameResult(
                    id = id,
                    sequence = jsonToList(sequenceJson),
                    errorIndex = errorIndex
                )
            )
        }
        cursor.close()
        lista
    }

    suspend fun getById(id: Long): GameResult? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            GameResultsTable.TABLE_NAME,
            null,
            "${GameResultsTable.COLUMN_ID}=?",
            arrayOf(id.toString()),
            null, null, null
        )
        var result: GameResult? = null
        if (cursor.moveToFirst()) {
            result = GameResult(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(GameResultsTable.COLUMN_ID)),
                sequence = jsonToList(cursor.getString(cursor.getColumnIndexOrThrow(GameResultsTable.COLUMN_SEQUENCE))),
                errorIndex = cursor.getInt(cursor.getColumnIndexOrThrow(GameResultsTable.COLUMN_ERROR_INDEX))
            )
        }
        cursor.close()
        result
    }
}