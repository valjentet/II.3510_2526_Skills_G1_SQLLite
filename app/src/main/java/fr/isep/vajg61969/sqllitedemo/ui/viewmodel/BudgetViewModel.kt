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

class BudgetViewModel(private val repo: BudgetRepository) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses

    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome

    private val _incomes = MutableStateFlow<List<Income>>(emptyList())
    val incomes: StateFlow<List<Income>> = _incomes

    // load data for month (format "YYYY-MM")
    fun loadMonth(month: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val ex = repo.getExpenses(month)
            val totE = repo.getTotalExpenses(month)
            val totI = repo.getTotalIncome(month)
            val inc = repo.getIncomes(month)
            _expenses.value = ex
            _totalExpenses.value = totE
            _totalIncome.value = totI
            _incomes.value = inc
        }
    }

    fun addExpense(expense: Expense, monthToReload: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addExpense(expense)
            loadMonth(monthToReload)
        }
    }

    fun addIncome(income: Income, monthToReload: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addIncome(income)
            loadMonth(monthToReload)
        }
    }

    fun deleteExpense(id: Int, monthToReload: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeExpense(id)
            loadMonth(monthToReload)
        }
    }

    fun updateExpense(expense: Expense, monthToReload: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateExpense(expense)
            loadMonth(monthToReload)
        }
    }

    fun updateIncome(income: Income, monthToReload: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateIncome(income)
            loadMonth(monthToReload)
        }
    }

    fun deleteIncome(id: Int, monthToReload: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeIncome(id)
            loadMonth(monthToReload)
        }
    }
}
