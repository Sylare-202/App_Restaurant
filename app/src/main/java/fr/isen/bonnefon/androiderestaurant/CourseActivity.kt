package fr.isen.bonnefon.androiderestaurant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

import android.widget.Toast
import androidx.compose.material.icons.materialIcon

class CourseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemName = intent.getStringExtra("itemName") ?: ""
        val ingredient = intent.getStringExtra("ingredients") ?: ""
        val images = intent.getStringArrayListExtra("imageURLs") ?: emptyList<String>()
        val itemPrices = intent.getStringArrayListExtra("prices") ?: emptyList<String>()

        setContent {
            AndroidERestaurantTheme {
                Column {
                    TopBar(
                        onBackClicked = { finish() },
                        onCartClicked = {
                            val intent = Intent(this@CourseActivity, CartActivity::class.java)
                            startActivity(intent)
                        },
                        topBarName = if (itemName.length > 15) "${itemName.take(15)}... Details" else "$itemName Details"
                    )
                    CourseDetails(
                        itemName = itemName,
                        itemPrices = itemPrices,
                        ingredient = ingredient,
                        images = images,
                        context = this@CourseActivity
                    )
                }
            }
        }
    }
}

@Composable
fun CourseDetails(
    itemName: String,
    itemPrices: List<String>,
    ingredient: String,
    images: List<String>,
    context: Context
) {
    var quantity by remember {
        mutableStateOf(1)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(images.size) { index ->
                        Image(
                            painter = rememberImagePainter(images[index]),
                            contentDescription = "Item Image",
                            modifier = Modifier
                                .width(400.dp)
                                .height(200.dp),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                }
            }
            item {
                // Title
                Text(
                    text = itemName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                // Divider
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = Color.Gray
                )
                // Ingredients
                Text(
                    text = "Ingredients: $ingredient",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                // Spacer
                Spacer(modifier = Modifier.height(100.dp))
                // Quantity Selector
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(Alignment.CenterVertically)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }
                // Spacer
                Spacer(modifier = Modifier.height(150.dp))
                // Button
                itemPrices.forEach { price ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Button(
                            onClick = {
                                val totalPrice = price.replace("€", "").toDouble() * quantity
                                val cartItem = CartItem(
                                    itemName,
                                    totalPrice,
                                    ingredient,
                                    images[0], // Assuming there's only one image per item
                                    quantity
                                )
                                addToCart(cartItem, context)
                            },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth(0.75f)
                        ) {
                            val totalPrice = price.replace("€", "").toDouble() * quantity
                            Text(
                                text = "Total = $totalPrice€",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


fun addToCart(item: CartItem, context: Context) {
    val cartFile = File(context.filesDir, "cart.json")
    val jsonArray = if (cartFile.exists()) {
        JSONArray(cartFile.readText())
    } else {
        JSONArray()
    }

    val jsonObject = JSONObject().apply {
        put("name", item.name)
        put("price", item.price)
        put("ingredient", item.ingredient)
        put("img", item.img)
        put("quantity", item.quantity)
    }
    jsonArray.put(jsonObject)

    cartFile.writeText(jsonArray.toString())

    Toast.makeText(context, "Objet ajouté du panier", Toast.LENGTH_SHORT).show()
    println("jsonArray: $jsonArray")
    println("cartFile: ${cartFile.readText()}")
}

