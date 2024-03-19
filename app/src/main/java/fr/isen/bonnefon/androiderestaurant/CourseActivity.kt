package fr.isen.bonnefon.androiderestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.isen.bonnefon.androiderestaurant.ui.theme.AndroidERestaurantTheme

import androidx.compose.foundation.layout.Column

class CourseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemName = intent.getStringExtra("itemName") ?: ""
        val itemPrice = intent.getStringExtra("prices") ?: ""
        val ingredient = intent.getStringExtra("ingredients") ?: ""
        val image = intent.getStringExtra("imageURL") ?: ""

        setContent {
            AndroidERestaurantTheme {
                CourseDetails(
                    itemName = itemName,
                    itemPrice = itemPrice,
                    ingredient = ingredient,
                    image = image
                )
            }
        }
    }
}

@Composable
fun CourseDetails(
    itemName: String,
    itemPrice: String,
    ingredient: String,
    image: String
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Text(text = "Item Name: $itemName")
            Text(text = "Price: $itemPrice")
            Text(text = "Ingredients: $ingredient")
            Text(text = "Image URL: $image")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CourseDetailsPreview() {
    AndroidERestaurantTheme {
        CourseDetails(
            itemName = "Item Name",
            itemPrice = "$10",
            ingredient = "Ingredient 1, Ingredient 2",
            image = "https://example.com/image.jpg"
        )
    }
}