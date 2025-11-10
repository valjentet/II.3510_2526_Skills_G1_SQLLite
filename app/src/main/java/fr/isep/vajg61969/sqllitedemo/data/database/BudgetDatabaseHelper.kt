package fr.isep.vajg61969.sqllitedemo.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import fr.isep.vajg61969.sqllitedemo.data.model.Expense
import fr.isep.vajg61969.sqllitedemo.data.model.Income

/**
 * SQLite Database Helper - manages budget.db database.
 * SQLiteOpenHelper automatically creates/opens the database file and manages connections.
 * Database file location: /data/data/[package]/databases/budget.db in the phone's internal storage.
 */
class BudgetDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "budget.db", null, 1) {

    /**
     * SQLite CREATE TABLE: Executed when database is first created.
     * Creates expenses and incomes tables with appropriate column types.
     * INTEGER PRIMARY KEY AUTOINCREMENT generates unique IDs automatically.
     */
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

    /**
     * SQLite migration: Called when database version changes.
     * Drops existing tables and recreates them (destroys all data).
     * In production, use ALTER TABLE to preserve existing data.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS expenses")
        db.execSQL("DROP TABLE IF EXISTS incomes")
        onCreate(db)
    }

    /**
     * SQLite INSERT: Adds new expense row to expenses table.
     * ContentValues safely maps Kotlin data to SQLite columns (prevents SQL injection).
     * Returns the auto-generated row ID, or -1 if insertion fails.
     */
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

    /**
     * SQLite INSERT: Adds new income row to incomes table.
     * Returns the auto-generated row ID for the inserted income record.
     */
    fun insertIncome(i: Income): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("month", i.month)
            put("amount", i.amount)
        }
        return db.insert("incomes", null, values)
    }

    /**
     * SQLite SELECT: Queries expenses table using parameterized query (WHERE date LIKE ?).
     * LIKE operator with "$month-%" pattern matches all dates starting with the month.
     */
    fun getExpensesForMonth(month: String): List<Expense> {
        val list = mutableListOf<Expense>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, title, amount, category, date FROM expenses WHERE date LIKE ? ORDER BY date DESC",
            arrayOf("$month-%")
        )
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

    /**
     * SQLite aggregation: Uses SUM() function to calculate total expenses for a month.
     * SUM() returns NULL when no rows match, so we check for null before reading the value.
     * WHERE date LIKE ? filters expenses by month pattern (ex, "2024-01-%") it won"t work with an other approach.
     */
    fun getMonthlyTotal(month: String): Double {
        var total = 0.0
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT SUM(amount) AS total FROM expenses WHERE date LIKE ?", arrayOf("$month-%"))
        try {
            if (cursor.moveToFirst() && !cursor.isNull(cursor.getColumnIndexOrThrow("total"))) {
                total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
            }
        } finally {
            cursor.close()
        }
        return total
    }

    /**
     * SQLite aggregation: Calculates total income using SUM() function.
     * Uses exact match on month column (WHERE month = ?) since month is stored as "YYYY-MM".
     */
    fun getMonthlyIncome(month: String): Double {
        var total = 0.0
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT SUM(amount) AS total FROM incomes WHERE month = ?", arrayOf(month))
        try {
            if (cursor.moveToFirst() && !cursor.isNull(cursor.getColumnIndexOrThrow("total"))) {
                total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
            }
        } finally {
            cursor.close()
        }
        return total
    }

    /**
     * SQLite UPDATE: Modifies existing expense row identified by id.
     * ContentValues contains new column values, WHERE clause uses parameterized id=? for safety.
     * Returns number of rows updated (1 if successful, 0 if id not found).
     */
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

    /**
     * SQLite UPDATE: Modifies existing income row by id.
     * Updates month and amount columns for the specified income record.
     */
    fun updateIncome(i: Income): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("month", i.month)
            put("amount", i.amount)
        }
        return db.update("incomes", values, "id=?", arrayOf(i.id.toString()))
    }

    /**
     * SQLite SELECT: Retrieves single income by primary key (id).
     * Returns Income object if found, null if no record matches the id.
     */
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

    /**
     * SQLite SELECT: Queries all incomes for a specific month.
     * Uses exact match on month column (WHERE month = ?) to filter income records.
     */
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

    /**
     * SQLite DELETE: Removes expense row from expenses table by id.
     * Parameterized WHERE clause (id=?) prevents SQL injection attacks.
     * Returns number of rows deleted (1 if successful, 0 if id not found).
     */
    fun deleteExpense(id: Int): Int {
        val db = writableDatabase
        return db.delete("expenses", "id=?", arrayOf(id.toString()))
    }

    /**
     * SQLite DELETE: Removes income row from incomes table by id.
     * Deletes the income record with the specified primary key.
     */
    fun deleteIncome(id: Int): Int {
        val db = writableDatabase
        return db.delete("incomes", "id=?", arrayOf(id.toString()))
    }
}
