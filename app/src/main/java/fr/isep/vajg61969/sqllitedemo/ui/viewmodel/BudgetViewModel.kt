package fr.isep.vajg61969.sqllitedemo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.isep.vajg61969.sqllitedemo.data.model.Expense
import fr.isep.vajg61969.sqllitedemo.data.model.Income
import fr.isep.vajg61969.sqllitedemo.repository.BudgetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel: Executes SQLite operations on background thread (Dispatchers.IO).
 * SQLite database operations are blocking and must run off the main UI thread.
 * StateFlow exposes reactive data streams that automatically update UI when SQLite data changes.
 */
class BudgetViewModel(private val repo: BudgetRepository) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses

    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome

    private val _incomes = MutableStateFlow<List<Income>>(emptyList())
    val incomes: StateFlow<List<Income>> = _incomes

    /**
     * SQLite SELECT: Loads all budget data for a month from SQLite database.
     * Executes multiple queries: expenses list, totals (SUM aggregation), and incomes list.
     * All queries run on IO dispatcher to avoid blocking the UI thread.
     */
    fun loadMonth(month: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _expenses.value = repo.getExpenses(month)
            _totalExpenses.value = repo.getTotalExpenses(month)
            _totalIncome.value = repo.getTotalIncome(month)
            _incomes.value = repo.getIncomes(month)
        }
    }

    /**
     * SQLite INSERT: Adds new expense to database, then reloads month data.
     * Repository performs INSERT INTO expenses table with ContentValues.
     */
    fun addExpense(expense: Expense, monthToReload: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addExpense(expense)
            loadMonth(monthToReload)
        }
    }

    /**
     * SQLite INSERT: Adds new income to database, then reloads month data.
     * Repository performs INSERT INTO incomes table with month and amount.
     */
    fun addIncome(income: Income, monthToReload: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addIncome(income)
            loadMonth(monthToReload)
        }
    }

    /**
     * SQLite DELETE: Removes expense from database by id, then reloads data.
     * Repository executes DELETE FROM expenses WHERE id=? to remove the row.
     */
    fun deleteExpense(id: Int, monthToReload: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeExpense(id)
            loadMonth(monthToReload)
        }
    }

    /**
     * SQLite UPDATE: Modifies existing expense in database, then reloads data.
     * Repository executes UPDATE expenses SET ... WHERE id=? to update the row.
     */
    fun updateExpense(expense: Expense, monthToReload: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateExpense(expense)
            loadMonth(monthToReload)
        }
    }

    /**
     * SQLite UPDATE: Modifies existing income in database, then reloads data.
     * Repository updates month and amount columns for the specified income record.
     */
    fun updateIncome(income: Income, monthToReload: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateIncome(income)
            loadMonth(monthToReload)
        }
    }

    /**
     * SQLite DELETE: Removes income from database by id, then reloads data.
     * Repository executes DELETE FROM incomes WHERE id=? to remove the row.
     */
    fun deleteIncome(id: Int, monthToReload: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeIncome(id)
            loadMonth(monthToReload)
        }
    }
}
