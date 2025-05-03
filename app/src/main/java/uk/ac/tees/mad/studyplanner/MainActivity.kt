package uk.ac.tees.mad.studyplanner
import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyPlannerApp()
        }
    }
}

@Composable
fun StudyPlannerApp() {
    val navController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(navController = navController, startDestination = "splash") {
            composable("splash") {
                SplashScreen(navController = navController)
            }
            composable("home") {
                HomeScreen(navController = navController)
            }
        }
    }
}

@Composable
fun SplashScreen(navController: androidx.navigation.NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var username by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Study Planner",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Enter Your Name") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            runBlocking {
                context.dataStore.edit { settings ->
                    settings[stringPreferencesKey("username")] = username.text
                }
            }
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        }) {
            Text("Start")
        }
    }
}

@Composable
fun HomeScreen(navController: androidx.navigation.NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val username = runBlocking {
        context.dataStore.data.map { preferences ->
            preferences[stringPreferencesKey("username")] ?: "User"
        }.first()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome, $username!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Plan your studies efficiently.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}