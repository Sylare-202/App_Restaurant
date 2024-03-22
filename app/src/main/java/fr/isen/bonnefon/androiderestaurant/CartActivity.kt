package fr.isen.bonnefon.androiderestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.isen.bonnefon.androiderestaurant.ui.theme.AndroidERestaurantTheme
import org.json.JSONArray
import java.io.File

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                Column {
                    TopBar(
                        onBackClicked = { finish() },
                        onCartClicked = {
                            Toast.makeText(this@CartActivity, "Vous etes deja dans le panier", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        CartContent(this@CartActivity)
                    }
                }
            }
        }
    }
}

@Composable
fun CartContent(context: Context) { // Accept Context as parameter
    // Read contents of cart.json and parse JSON data
    val cartItems = readCartItems(context)
    println(cartItems)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Panier",
            style = customTitleStyle,
            modifier = Modifier.padding(top = 50.dp)
        )
        Divider(
            modifier = Modifier.padding(horizontal = 90.dp, vertical = 8.dp),
            color = Color.Gray,
            thickness = 1.dp
        )
        cartItems.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    style = customItemsStyle,
                    modifier = Modifier.padding(end = 10.dp)
                )
                Text(
                    text = item.price.toString() + "€ x " + item.quantity.toString(),
                    style = customItemsStyle,
                    modifier = Modifier.padding(end = 2.dp)
                )
            }
        }
        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = Color.Gray,
            thickness = 1.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total",
                style = customItemsStyle,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(
                text = cartItems.sumOf { it.price * it.quantity }.toString() + "€",
                style = customItemsStyle,
                modifier = Modifier.padding(end = 2.dp)
            )
        }
    }
}



// Function to read contents of cart.json and parse JSON data
fun readCartItems(context: Context): List<CartItem> { // Accept Context as parameter
    val cartFile = File(context.filesDir, "cart.json")
    val cartItems = mutableListOf<CartItem>()

    if (cartFile.exists()) {
        val jsonArray = JSONArray(cartFile.readText())
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val name = jsonObject.getString("name")
            val price = jsonObject.getDouble("price")
            val ingredient = jsonObject.getString("ingredient")
            val quantity = jsonObject.getInt("quantity")
            val img = jsonObject.getString("img")
            cartItems.add(CartItem(name, price, ingredient, img, quantity))
        }
    }

    return cartItems
}

@Preview(showBackground = true)
@Composable
fun CartContentPreview() {
    AndroidERestaurantTheme {
        CartContent(
            context = MainActivity()
        )
    }
}
