package fr.isep.vajg61969.sqllitedemo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper (context: Context) :
    SQLiteOpenHelper(context, "tasks.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE tasks
             (id INTEGER PRIMARY KEY AUTOINCREMENT,
              title TEXT,
               isDone INTEGER)
               """
            .trimIndent()
        )

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tasks")
        onCreate(db)
    }

    fun insertTask(title: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", title)
            put("isDone", 0)
        }
        return db.insert("tasks", null, values)
    }
}
