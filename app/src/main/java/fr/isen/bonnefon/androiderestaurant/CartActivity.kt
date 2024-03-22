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
import android.content.Intent
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Divider
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.verticalScroll

import androidx.compose.foundation.rememberScrollState

import org.json.JSONObject

import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue



class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                var cartItemCount = countCartItems(this)
                Column {
                    TopBar(
                        onBackClicked = {
                            val intent = Intent(this@CartActivity, MainActivity::class.java)
                            startActivity(intent)
                        },
                        onCartClicked = {
                            Toast.makeText(
                                this@CartActivity,
                                "Vous etes deja dans le panier",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        topBarName = "Panier",
                        cartItemCount = cartItemCount
                    )
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        CartContent(this@CartActivity) { item ->
                            onDeleteItem(this@CartActivity, item)
                            cartItemCount = countCartItems(this@CartActivity)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartContent(context: Context, onDeleteItem: (CartItem) -> Unit) {
    // Read contents of cart.json and parse JSON data
    var cartItems by remember { mutableStateOf(readCartItems(context)) } // Use mutableStateOf to hold the cart items

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
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
            // List of items
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                cartItems.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (item.name.length > 20) "${item.name.take(20)}..." else item.name,
                            style = customItemsStyle,
                            modifier = Modifier.weight(1f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${item.price}€ x ${item.quantity}",
                                style = customItemsStyle,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                            // Add delete icon button with onDeleteItem callback
                            IconButton(
                                onClick = {
                                    onDeleteItem(item)
                                    cartItems = readCartItems(context)
                                    Toast.makeText(context, "Objet enlevé du panier", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.padding(end = 10.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
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
                    text = "${cartItems.sumOf { it.price * it.quantity }}€",
                    style = customItemsStyle,
                    modifier = Modifier.padding(end = 2.dp)
                )
            }
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

// Function to handle deletion of an item from the cart
fun onDeleteItem(context: Context, item: CartItem) {
    val cartItems = readCartItems(context).toMutableList()
    // Find the index of the item to delete
    val index = cartItems.indexOfFirst { it == item }
    if (index != -1) {
        // Remove the item from the list
        cartItems.removeAt(index)
        // Update the JSON file with the modified list
        val jsonArray = JSONArray()
        cartItems.forEach { cartItem ->
            val jsonObject = JSONObject().apply {
                put("name", cartItem.name)
                put("price", cartItem.price)
                put("ingredient", cartItem.ingredient)
                put("quantity", cartItem.quantity)
                put("img", cartItem.img)
            }
            jsonArray.put(jsonObject)
        }
        val cartFile = File(context.filesDir, "cart.json")
        cartFile.writeText(jsonArray.toString())
    }
}