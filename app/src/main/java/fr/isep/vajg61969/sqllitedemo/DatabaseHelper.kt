package fr.isep.vajg61969.sqllitedemo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Task(
    val id: Int,
    val title: String,
    val isDone: Boolean
)
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

    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase

        val cursor = db.query(
            "tasks", // nom de la table
            arrayOf("id", "title", "isDone"), // colonnes à lire
            null, // WHERE (aucune condition)
            null, // valeurs pour WHERE
            null, // GROUP BY
            null, // HAVING
            "id DESC" // tri par ID (les plus récentes d’abord)
        )

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val isDone = cursor.getInt(cursor.getColumnIndexOrThrow("isDone")) == 1

            tasks.add(Task(id, title, isDone))
        }
        cursor.close()
        db.close()
        return tasks
    }
}


