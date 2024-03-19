package fr.isen.bonnefon.androiderestaurant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.bonnefon.androiderestaurant.ui.theme.AndroidERestaurantTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import android.widget.Toast

val customTitleStyle = TextStyle(
    fontSize = 40.sp, // Adjust the size as needed
    // Add more style attributes if necessary
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MenuScreen(this)
                }
            }
        }
    }

    fun showToastMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        println("MainActivity destroyed")
    }
}

@Composable
fun MenuScreen(activity: MainActivity) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Restaurant Menu",
            style = customTitleStyle,
            modifier = Modifier.padding(bottom = 100.dp)
        )
        MenuButton(activity, "Entrées")
        Divider(modifier = Modifier.padding(horizontal = 90.dp, vertical = 8.dp), color = Color.Gray, thickness = 1.dp)
        MenuButton(activity, "Plats")
        Divider(modifier = Modifier.padding(horizontal = 90.dp, vertical = 8.dp), color = Color.Gray, thickness = 1.dp)
        MenuButton(activity, "Desserts")
    }
}

@Composable
fun MenuButton(activity: MainActivity, text: String) {
    Button(
        onClick = {
            val intent = Intent(activity, getCategoryActivity("$text"))
            intent.putExtra("name", "$text") // Pass the category name
            activity.startActivity(intent)
        },
        modifier = Modifier
            .padding(8.dp)
            .size(200.dp, 50.dp)
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun MenuPreview() {
    AndroidERestaurantTheme {
        MenuScreen(MainActivity())
    }
}

// Function to get the corresponding category activity
fun getCategoryActivity(category: String): Class<out Any> {
    return when (category) {
        "Entrées" -> CategoryActivity::class.java
        "Plats" -> CategoryActivity::class.java
        "Desserts" -> CategoryActivity::class.java
        else -> MainActivity::class.java
    }
}