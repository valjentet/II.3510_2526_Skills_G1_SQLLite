package fr.isep.vajg61969.sqllitedemo.repository

import fr.isep.vajg61969.sqllitedemo.data.database.BudgetDatabaseHelper
import fr.isep.vajg61969.sqllitedemo.data.model.Expense
import fr.isep.vajg61969.sqllitedemo.data.model.Income

/**
 * Repository pattern: Abstraction layer between ViewModel and SQLite database.
 * Delegates all SQLite operations to BudgetDatabaseHelper.
 * This separation allows easy testing and future migration to Room database.
 */
class BudgetRepository(private val db: BudgetDatabaseHelper) {
    fun addExpense(e: Expense) = db.insertExpense(e)
    fun addIncome(i: Income) = db.insertIncome(i)
    fun getExpenses(month: String) = db.getExpensesForMonth(month)
    fun getTotalExpenses(month: String) = db.getMonthlyTotal(month)
    fun getTotalIncome(month: String) = db.getMonthlyIncome(month)
    fun removeExpense(id: Int) = db.deleteExpense(id)
    fun updateExpense(e: Expense) = db.updateExpense(e)
    fun updateIncome(i: Income) = db.updateIncome(i)
    fun getIncomeById(id: Int) = db.getIncomeById(id)
    fun getIncomes(month: String) = db.getIncomesForMonth(month)
    fun removeIncome(id: Int) = db.deleteIncome(id)
}
