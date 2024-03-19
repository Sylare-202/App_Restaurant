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
import coil.compose.rememberImagePainter

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer


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
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image
            Image(
                painter = rememberImagePainter(image),
                contentDescription = "Item Image",
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.FillWidth
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Title
            Text(
                text = itemName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            // Divider
            Divider(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                color = Color.Gray
            )
            // Ingredients
            Text(
                text = "Ingredients: $ingredient",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            // Spacer
            Spacer(modifier = Modifier.height(350.dp))
            // Button
            Button(
                onClick = {
                          /*TODO*/
                          },
                modifier = Modifier.fillMaxWidth(0.75f).align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Total = $itemPrice€")
            }
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