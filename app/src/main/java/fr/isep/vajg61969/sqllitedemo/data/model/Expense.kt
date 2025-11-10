package fr.isep.vajg61969.sqllitedemo.data.model
/**
 * Creates classes: expense
 */
data class Expense(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String // "YYYY-MM-DD"
)
