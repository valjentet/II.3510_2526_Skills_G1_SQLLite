package fr.isep.vajg61969.sqllitedemo.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import fr.isep.vajg61969.sqllitedemo.data.model.Expense
import fr.isep.vajg61969.sqllitedemo.data.model.Income

class BudgetDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "budget.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS expenses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                amount REAL,
                category TEXT,
                date TEXT
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS incomes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                month TEXT,
                amount REAL
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS expenses")
        db.execSQL("DROP TABLE IF EXISTS incomes")
        onCreate(db)
    }

    // inserts
    fun insertExpense(e: Expense): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", e.title)
            put("amount", e.amount)
            put("category", e.category)
            put("date", e.date)
        }
        return db.insert("expenses", null, values)
    }

    fun insertIncome(i: Income): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("month", i.month)
            put("amount", i.amount)
        }
        return db.insert("incomes", null, values)
    }

    // reads
    fun getExpensesForMonth(month: String): List<Expense> {
        val list = mutableListOf<Expense>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, title, amount, category, date FROM expenses WHERE date LIKE ? ORDER BY date DESC", arrayOf("$month-%"))
        try {
            while (cursor.moveToNext()) {
                list.add(
                    Expense(
                        id = cursor.getInt(0),
                        title = cursor.getString(1),
                        amount = cursor.getDouble(2),
                        category = cursor.getString(3),
                        date = cursor.getString(4)
                    )
                )
            }
        } finally {
            cursor.close()
        }
        return list
    }

    fun getMonthlyTotal(month: String): Double {
        var total = 0.0
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT SUM(amount) AS total FROM expenses WHERE date LIKE ?", arrayOf("$month-%"))
        try {
            if (cursor.moveToFirst()) {
                val totalIndex = cursor.getColumnIndexOrThrow("total")
                if (!cursor.isNull(totalIndex)) {
                    total = cursor.getDouble(totalIndex)
                }
            }
        } finally {
            cursor.close()
        }
        return total
    }

    fun getMonthlyIncome(month: String): Double {
        var total = 0.0
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT SUM(amount) AS total FROM incomes WHERE month = ?", arrayOf(month))
        try {
            if (cursor.moveToFirst()) {
                val totalIndex = cursor.getColumnIndexOrThrow("total")
                if (!cursor.isNull(totalIndex)) {
                    total = cursor.getDouble(totalIndex)
                }
            }
        } finally {
            cursor.close()
        }
        return total
    }

    // update
    fun updateExpense(e: Expense): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", e.title)
            put("amount", e.amount)
            put("category", e.category)
            put("date", e.date)
        }
        return db.update("expenses", values, "id=?", arrayOf(e.id.toString()))
    }

    fun updateIncome(i: Income): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("month", i.month)
            put("amount", i.amount)
        }
        return db.update("incomes", values, "id=?", arrayOf(i.id.toString()))
    }

    // read income by id
    fun getIncomeById(id: Int): Income? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, month, amount FROM incomes WHERE id = ?", arrayOf(id.toString()))
        return try {
            if (cursor.moveToFirst()) {
                Income(
                    id = cursor.getInt(0),
                    month = cursor.getString(1),
                    amount = cursor.getDouble(2)
                )
            } else {
                null
            }
        } finally {
            cursor.close()
        }
    }

    // get all incomes for a month
    fun getIncomesForMonth(month: String): List<Income> {
        val list = mutableListOf<Income>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, month, amount FROM incomes WHERE month = ?", arrayOf(month))
        try {
            while (cursor.moveToNext()) {
                list.add(
                    Income(
                        id = cursor.getInt(0),
                        month = cursor.getString(1),
                        amount = cursor.getDouble(2)
                    )
                )
            }
        } finally {
            cursor.close()
        }
        return list
    }

    // optional helpers
    fun deleteExpense(id: Int): Int {
        val db = writableDatabase
        return db.delete("expenses", "id=?", arrayOf(id.toString()))
    }

    fun deleteIncome(id: Int): Int {
        val db = writableDatabase
        return db.delete("incomes", "id=?", arrayOf(id.toString()))
    }
}
