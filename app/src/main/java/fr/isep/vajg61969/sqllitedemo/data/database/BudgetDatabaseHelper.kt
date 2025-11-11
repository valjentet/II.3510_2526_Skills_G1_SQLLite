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
        // Create a fonction that creates the tables Expenses and incomes (if they don't exist) OnCreate
    }
    /**
     * SQLite migration: Called when database version changes.
     * Drops existing tables and recreates them (destroys all data).
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //Destroy db and use Oncreate
    }

    /**
     * SQLite insert: Adds new expense row to expenses table.
     * ContentValues safely maps Kotlin data to SQLite columns (prevents SQL injection).
     * Returns the auto-generated row ID, or -1 if insertion fails.
     */
    fun insertExpense(e: Expense): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            //ADD Right fields in the table with put
            // Exemple : put("title", e.title)
        }

        // Insert values in the expenses table and return the ID of the new row
        // Indice : utilisez db.insert(...)

        return -1L // Temporary value to be replaced
    }


    /**
     * SQLite insert: Adds new income row to incomes table.
     * Returns the auto-generated row ID for the inserted income record.
     */
    fun insertIncome(i: Income): Long {
        //Implement function to insert an income into the "incomes" table
        return -1 //Provide the return value
    }


    /**
     * SQLite select: Queries expenses table using parameterized query (WHERE date LIKE ?).
     * LIKE operator with "$month-%" pattern matches all dates starting with the month.
     */
    fun getExpensesForMonth(month: String): List<Expense> {
        val list = mutableListOf<Expense>()
        val db = readableDatabase
            //Implement function to retrieve all expenses for a given month

        return emptyList() //Provide the return value
    }

    /**
     * SQLite aggregation: Uses SUM() function to calculate total expenses for a month.
     * SUM() returns NULL when no rows match, so we check for null before reading the value.
     * WHERE date LIKE ? filters expenses by month pattern (ex, "2024-01-%") it won"t work with an other approach.
     */
    fun getMonthlyTotal(month: String): Double {
        //Implement function to calculate the total expenses for a given month
        return 0.0
    }


    /**
     * SQLite aggregation: Calculates total income using SUM() function.
     * Uses exact match on month column (WHERE month = ?) since month is stored as "YYYY-MM".
     */
    fun getMonthlyIncome(month: String): Double {
        //Implement function to calculate the total income for a given month
        return 0.0
    }


    /**
     * SQLite update: Modifies existing expense row identified by id.
     * ContentValues contains new column values, WHERE clause uses parameterized id=? for safety.
     * Returns number of rows updated (1 if successful, 0 if id not found).
     */
    fun updateExpense(e: Expense): Int {
        //Implement function to update an existing expense in the expenses table
        return 0
    }


    /**
     * SQLite update: Modifies existing income row by id.
     * Updates month and amount columns for the specified income record.
     */
    fun updateIncome(i: Income): Int {
        //Implement function to update an existing income in the incomes table
        return 0
    }


    /**
     * SQLite select: Retrieves single income by primary key (id).
     * Returns Income object if found, null if no record matches the id.
     */
    fun getIncomeById(id: Int): Income? {
        //Implement function to retrieve an income by its ID
        return null
    }


    /**
     * SQLite select: Queries all incomes for a specific month.
     * Uses exact match on month column (WHERE month = ?) to filter income records.
     */
    fun getIncomesForMonth(month: String): List<Income> {
        //Implement function to retrieve all incomes for a given month
        return emptyList()
    }


    /**
     * SQLite delete: Removes expense row from expenses table by id.
     * Parameterized WHERE clause (id=?) prevents SQL injection attacks.
     * Returns number of rows deleted (1 if successful, 0 if id not found).
     */
    fun deleteExpense(id: Int): Int {
        //Implement function to delete an expense by its ID
        return 0
    }


    /**
     * SQLite delete: Removes income row from incomes table by id.
     * Deletes the income record with the specified primary key.
     */
    fun deleteIncome(id: Int): Int {
        //Implement function to delete an income by its ID
        return 0
    }

}
