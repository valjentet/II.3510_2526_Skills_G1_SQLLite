package fr.isep.vajg61969.sqllitedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.isep.vajg61969.sqllitedemo.ui.theme.SQLliteDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val db = DatabaseHelper(this)
            SQLliteDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        androidx.compose.material3.Button(
                            onClick = {
                                val id = db.insertTask("Ma première tâche")
                                println("Tâche insérée avec l'ID $id")
                            },
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            androidx.compose.material3.Text("Ajouter tâche test")
                        }
                    }
                }
            }
        }
    }

        @Composable
        fun Greeting(name: String, modifier: Modifier = Modifier) {
            Text(
                text = "Hello $name!",
                modifier = modifier
            )
        }

        @Preview(showBackground = true)
        @Composable
        fun GreetingPreview() {
            SQLliteDemoTheme {
                Greeting("Android")
        }
    }
}