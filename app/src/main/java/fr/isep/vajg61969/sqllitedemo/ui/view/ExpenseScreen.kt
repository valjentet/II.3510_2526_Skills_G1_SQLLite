package fr.isep.vajg61969.sqllitedemo.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import fr.isep.vajg61969.sqllitedemo.data.model.Expense
import fr.isep.vajg61969.sqllitedemo.ui.viewmodel.BudgetViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ExpenseScreen(vm: BudgetViewModel, onDone: () -> Unit = {}) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
    var date by remember { mutableStateOf(today) }
    var message by remember { mutableStateOf("") }
    var messageColor by remember { mutableStateOf(Color.Gray) }
    val currentMonth = if (date.length >= 7) date.substring(0, 7) else today.substring(0, 7)
    val errorColor = MaterialTheme.colorScheme.error

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "New Expense",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category *") },
            placeholder = { Text("Ex: Food, Transport...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (€) *") },
            placeholder = { Text("0.00") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date *") },
            placeholder = { Text("YYYY-MM-DD") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text("Format: YYYY-MM-DD (e.g.: $today)") }
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Description (optional)") },
            placeholder = { Text("Expense description") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val amt = amount.toDoubleOrNull()
                if (category.isBlank() || amt == null || amt <= 0) {
                    message = "Please fill in the category and a valid amount."
                    
                    messageColor = errorColor
                    return@Button
                }
                if (date.isBlank() || !date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                    message = "Invalid date. Use format YYYY-MM-DD"
                    messageColor = errorColor
                    return@Button
                }
                val e = Expense(
                    title = title.trim().ifBlank { "Expense" },
                    amount = amt,
                    category = category.trim(),
                    date = date.trim()
                )
                vm.addExpense(e, currentMonth)
                title = ""
                amount = ""
                category = ""
                date = today
                message = "✓ Expense saved successfully!"
                messageColor = Color(0xFF006400)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Save Expense", fontSize = 16.sp)
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = messageColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
