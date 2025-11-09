package fr.isep.vajg61969.sqllitedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isep.vajg61969.sqllitedemo.data.database.BudgetDatabaseHelper
import fr.isep.vajg61969.sqllitedemo.repository.BudgetRepository
import fr.isep.vajg61969.sqllitedemo.ui.view.ExpenseScreen
import fr.isep.vajg61969.sqllitedemo.ui.view.IncomeScreen
import fr.isep.vajg61969.sqllitedemo.ui.view.SummaryScreen
import fr.isep.vajg61969.sqllitedemo.ui.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Création des dépendances
        val db = BudgetDatabaseHelper(this)
        val repo = BudgetRepository(db)
        val vm = BudgetViewModel(repo)

        setContent {
            var currentScreen by remember { mutableStateOf("summary") }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Gestion de Budget",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    )
                },
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Text("📊") },
                            label = { Text("Prévision") },
                            selected = currentScreen == "summary",
                            onClick = { currentScreen = "summary" }
                        )
                        NavigationBarItem(
                            icon = { Text("💸") },
                            label = { Text("Dépense") },
                            selected = currentScreen == "expense",
                            onClick = { currentScreen = "expense" }
                        )
                        NavigationBarItem(
                            icon = { Text("💰") },
                            label = { Text("Revenu") },
                            selected = currentScreen == "income",
                            onClick = { currentScreen = "income" }
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (currentScreen) {
                        "expense" -> ExpenseScreen(vm) { currentScreen = "summary" }
                        "income" -> IncomeScreen(vm) { currentScreen = "summary" }
                        else -> SummaryScreen(vm)
                    }
                }
            }
        }
    }
}
