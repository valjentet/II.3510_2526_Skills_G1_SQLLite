package fr.isep.vajg61969.sqllitedemo.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import fr.isep.vajg61969.sqllitedemo.data.model.Expense
import fr.isep.vajg61969.sqllitedemo.data.model.Income
import fr.isep.vajg61969.sqllitedemo.ui.viewmodel.BudgetViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.round

@Composable
fun SummaryScreen(vm: BudgetViewModel) {
    val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    var month by remember { mutableStateOf(currentMonth) }
    var expenses by remember { mutableStateOf(emptyList<Expense>()) }
    var incomes by remember { mutableStateOf(emptyList<Income>()) }
    var totalE by remember { mutableStateOf(0.0) }
    var totalI by remember { mutableStateOf(0.0) }
    var editingExpense by remember { mutableStateOf<Expense?>(null) }
    var editingIncome by remember { mutableStateOf<Income?>(null) }

    // Charger automatiquement le mois actuel au démarrage
    LaunchedEffect(Unit) {
        vm.loadMonth(currentMonth)
    }

    LaunchedEffect(month) {
        vm.loadMonth(month)
    }

    // Collecter les flows
    LaunchedEffect(Unit) {
        vm.expenses.collectLatest { expenses = it }
    }
    LaunchedEffect(Unit) {
        vm.incomes.collectLatest { incomes = it }
    }
    LaunchedEffect(Unit) {
        vm.totalExpenses.collectLatest { totalE = it }
    }
    LaunchedEffect(Unit) {
        vm.totalIncome.collectLatest { totalI = it }
    }

    val balance = totalI - totalE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Prévision Mensuelle",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = month,
                onValueChange = { month = it },
                label = { Text("Mois") },
                placeholder = { Text("YYYY-MM") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(
                onClick = { vm.loadMonth(month) },
                modifier = Modifier.height(56.dp)
            ) {
                Text("Actualiser")
            }
        }

        // Carte de résumé
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Revenu total", fontSize = 16.sp)
                    Text(
                        text = "${formatMoney(totalI)} €",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF006400)
                    )
                }

                Divider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Dépenses totales", fontSize = 16.sp)
                    Text(
                        text = "${formatMoney(totalE)} €",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB00020)
                    )
                }

                Divider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Solde restant",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${formatMoney(balance)} €",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (balance >= 0) Color(0xFF006400) else Color(0xFFB00020)
                    )
                }
            }
        }

        // Liste des revenus
        Text(
            text = "Revenus du mois",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (incomes.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Aucun revenu enregistré pour ce mois.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 150.dp)
            ) {
                items(incomes) { income ->
                    IncomeItemCard(
                        income = income,
                        onEdit = { editingIncome = it },
                        onDelete = { vm.deleteIncome(it.id, month) }
                    )
                }
            }
        }

        // Liste des dépenses
        Text(
            text = "Dépenses du mois",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (expenses.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Aucune dépense enregistrée pour ce mois.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(expenses) { expense ->
                    ExpenseItemCard(
                        expense = expense,
                        onEdit = { editingExpense = it },
                        onDelete = { vm.deleteExpense(it.id, month) }
                    )
                }
            }
        }
    }

    // Dialog de modification de dépense
    editingExpense?.let { expense ->
        EditExpenseDialog(
            expense = expense,
            onDismiss = { editingExpense = null },
            onSave = { updatedExpense ->
                vm.updateExpense(updatedExpense, month)
                editingExpense = null
            }
        )
    }

    // Dialog de modification de revenu
    editingIncome?.let { income ->
        EditIncomeDialog(
            income = income,
            onDismiss = { editingIncome = null },
            onSave = { updatedIncome ->
                vm.updateIncome(updatedIncome, month)
                editingIncome = null
            }
        )
    }
}

@Composable
fun ExpenseItemCard(
    expense: Expense,
    onEdit: (Expense) -> Unit,
    onDelete: (Expense) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.category,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = expense.title,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = expense.date,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formatMoney(expense.amount)} €",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = { onEdit(expense) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifier",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = { onDelete(expense) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun IncomeItemCard(
    income: Income,
    onEdit: (Income) -> Unit,
    onDelete: (Income) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Revenu - ${income.month}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formatMoney(income.amount)} €",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006400)
                )
                IconButton(
                    onClick = { onEdit(income) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifier",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = { onDelete(income) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun EditExpenseDialog(
    expense: Expense,
    onDismiss: () -> Unit,
    onSave: (Expense) -> Unit
) {
    var title by remember { mutableStateOf(expense.title) }
    var amount by remember { mutableStateOf(expense.amount.toString()) }
    var category by remember { mutableStateOf(expense.category) }
    var date by remember { mutableStateOf(expense.date) }
    var message by remember { mutableStateOf("") }
    val errorColor = MaterialTheme.colorScheme.error

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Modifier la dépense",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Catégorie *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Montant (€) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = { Text("Format: YYYY-MM-DD") }
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = errorColor,
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annuler")
                    }
                    Button(
                        onClick = {
                            val amt = amount.toDoubleOrNull()
                            if (category.isBlank() || amt == null || amt <= 0) {
                                message = "Veuillez remplir tous les champs correctement."
                                return@Button
                            }
                            if (!date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                                message = "Date invalide. Format: YYYY-MM-DD"
                                return@Button
                            }
                            onSave(
                                expense.copy(
                                    title = title.trim().ifBlank { "Dépense" },
                                    amount = amt,
                                    category = category.trim(),
                                    date = date.trim()
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}

@Composable
fun EditIncomeDialog(
    income: Income,
    onDismiss: () -> Unit,
    onSave: (Income) -> Unit
) {
    var month by remember { mutableStateOf(income.month) }
    var amount by remember { mutableStateOf(income.amount.toString()) }
    var message by remember { mutableStateOf("") }
    val errorColor = MaterialTheme.colorScheme.error

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Modifier le revenu",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = month,
                    onValueChange = { month = it },
                    label = { Text("Mois *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = { Text("Format: YYYY-MM") }
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Montant (€) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = errorColor,
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annuler")
                    }
                    Button(
                        onClick = {
                            val amt = amount.toDoubleOrNull()
                            if (amt == null || amt <= 0) {
                                message = "Montant invalide."
                                return@Button
                            }
                            if (!month.matches(Regex("\\d{4}-\\d{2}"))) {
                                message = "Format de mois invalide. Utilisez YYYY-MM"
                                return@Button
                            }
                            onSave(
                                income.copy(
                                    month = month.trim(),
                                    amount = amt
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}

private fun formatMoney(value: Double): String {
    return String.format("%.2f", round(value * 100) / 100.0)
}
