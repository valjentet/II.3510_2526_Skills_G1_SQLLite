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
import fr.isep.vajg61969.sqllitedemo.data.model.Income
import fr.isep.vajg61969.sqllitedemo.ui.viewmodel.BudgetViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun IncomeScreen(vm: BudgetViewModel, onDone: () -> Unit = {}) {
    var month by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))) }
    var amount by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var messageColor by remember { mutableStateOf(Color.Gray) }
    val errorColor = MaterialTheme.colorScheme.error

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Monthly Income",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = month,
            onValueChange = { month = it },
            label = { Text("Month *") },
            placeholder = { Text("YYYY-MM") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text("Format: YYYY-MM (e.g.: ${LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))})") }
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

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val amt = amount.toDoubleOrNull()
                if (amt == null || amt <= 0) {
                    message = "Please enter a valid amount."
                    messageColor = errorColor
                    return@Button
                }
                if (!month.matches(Regex("\\d{4}-\\d{2}"))) {
                    message = "Invalid month format. Use YYYY-MM"
                    messageColor = errorColor
                    return@Button
                }
                val i = Income(month = month.trim(), amount = amt)
                vm.addIncome(i, month.trim())
                amount = ""
                message = "✓ Income saved successfully!"
                messageColor = Color(0xFF006400)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Save Income", fontSize = 16.sp)
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
