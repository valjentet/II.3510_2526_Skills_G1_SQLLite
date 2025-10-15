package fr.isep.vajg61969.sqllitedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.isep.vajg61969.sqllitedemo.ui.theme.SQLliteDemoTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val db = DatabaseHelper(this)
            SQLliteDemoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    GreetingScreen(db, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun GreetingScreen(db: DatabaseHelper, modifier: Modifier = Modifier) {
    var prenom by rememberSaveable { mutableStateOf("") }
    var submittedName by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!submittedName) {
            OutlinedTextField( //Display a text field
                value = prenom,
                onValueChange = { prenom = it },
                label = { Text("Mon Prénom") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { submittedName = true },
                enabled = prenom.isNotBlank()
            ) {Text("Valider")}
        } else {
            Text("Bonjour $prenom!")
            TaskManager(db, modifier = Modifier.padding(16.dp))
        }
    }
}
@Composable
fun TaskManager(db: DatabaseHelper, modifier: Modifier = Modifier) {
    var submittedDisplayTasks by rememberSaveable { mutableStateOf(false) }
    var tasks by rememberSaveable { mutableStateOf(emptyList<Task>()) }

    Column(
        modifier = modifier
            .background(Color.LightGray)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { submittedDisplayTasks = true
                tasks = db.getAllTasks()}
        ) {Text("Afficher les tâches")}

        if (submittedDisplayTasks) {
            if (tasks.isEmpty()) {
                Text("Aucune tâche trouvée.")
            } else {
                LazyColumn {
                    items(tasks) { task ->
                        Text("• ${task.title} ${if (task.isDone) "✅" else "❌"}")
                    }
                }
            }
        }
    }
}





