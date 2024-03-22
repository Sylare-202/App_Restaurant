package fr.isen.bonnefon.androiderestaurant

import android.os.Bundle
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter

import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Badge
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


data class MenuItem(
    val id: String,
    val nameFr: String,
    val nameEn: String,
    val categoryId: String,
    val categoryNameFr: String,
    val categoryNameEn: String,
    val images: List<String>,
    val ingredients: List<Ingredient>,
    val prices: List<Price>
)

data class Ingredient(
    val id: String,
    val shopId: String,
    val nameFr: String,
    val nameEn: String,
    val createDate: String,
    val updateDate: String,
    val pizzaId: String
)

data class Price(
    val id: String,
    val pizzaId: String,
    val sizeId: String,
    val price: String,
    val createDate: String,
    val updateDate: String,
    val size: String
)

data class CartItem(
    val name: String,
    val price: Double,
    val ingredient: String,
    val img: String,
    var quantity: Int
)


@Composable
fun TopBar(
    onBackClicked: () -> Unit,
    onCartClicked: () -> Unit,
    topBarName: String,
    cartItemCount: MutableState<Int>
) {
    Surface(
        modifier = Modifier.fillMaxWidth()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = topBarName,
                    style = TextStyle(fontSize = 20.sp),
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            actions = {
                Box(
                    modifier = Modifier.padding(end = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onCartClicked) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color.White
                        )
                    }
                    if (cartItemCount.value > 0) {
                        // Display a badge only if there are items in the cart
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp, end = 4.dp) // Adjust padding as needed
                                .align(Alignment.TopEnd)
                        ) {
                            Badge(
                                backgroundColor = Color.Red,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = cartItemCount.value.toString(),
                                    style = TextStyle(fontSize = 12.sp, color = Color.White)
                                )
                            }
                        }
                    }
                }
            },
            // Set the background color to blue
            backgroundColor = Color(0xFF448CC4)
        )
    }
}




val customItemsStyle = TextStyle(
    fontSize = 20.sp, // Adjust the size as needed
    // Add more style attributes if necessary
)

class CategoryActivity : ComponentActivity() {
    private lateinit var name: String
    private var items: List<MenuItem> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        name = intent.getStringExtra("name") ?: ""
        getMenuItems()
        setContent {
            val cartItemCount = remember { mutableStateOf(0) }
            Column {
                CallTopBar("$name", this@CategoryActivity)
                CategoryScreen(name, items, this@CategoryActivity)
            }
        }
    }

    private fun getMenuItems() {
        val url = "http://test.api.catering.bluecodegames.com/menu"
        val id_shop = 1 // id_shop value
        val jsonParams = JSONObject().apply {
            put("id_shop", id_shop)
        }

        val requestQueue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                val items = parseMenuItems(response) // Parse the JSON response
                setContent {
                    Column {
                        CallTopBar("$name", this@CategoryActivity)
                        CategoryScreen(name, items, this@CategoryActivity)
                    }
                }
            },
            { error ->
                println("Error: $error")
            })

        requestQueue.add(jsonObjectRequest)
    }

    private fun parseMenuItems(response: JSONObject): List<MenuItem> {
        val menuItems = mutableListOf<MenuItem>()
        val dataArray = response.getJSONArray("data")

        for (i in 0 until dataArray.length()) {
            val categoryObject = dataArray.getJSONObject(i)
            val categoryNameFr = categoryObject.getString("name_fr")
            val categoryItems = categoryObject.getJSONArray("items")

            for (j in 0 until categoryItems.length()) {
                val itemObject = categoryItems.getJSONObject(j)
                val id = itemObject.getString("id")
                val nameFr = itemObject.getString("name_fr")
                val nameEn = itemObject.getString("name_en")
                val categoryId = itemObject.getString("id_category")
                val categoryNameEn = itemObject.getString("categ_name_en")
                val imagesArray = itemObject.getJSONArray("images")
                val images = mutableListOf<String>()
                for (k in 0 until imagesArray.length()) {
                    images.add(imagesArray.getString(k))
                }

                val ingredientsArray = itemObject.getJSONArray("ingredients")
                val ingredients = mutableListOf<Ingredient>()
                for (k in 0 until ingredientsArray.length()) {
                    val ingredientObject = ingredientsArray.getJSONObject(k)
                    val ingredientId = ingredientObject.getString("id")
                    val shopId = ingredientObject.getString("id_shop")
                    val ingredientNameFr = ingredientObject.getString("name_fr")
                    val ingredientNameEn = ingredientObject.getString("name_en")
                    val createDate = ingredientObject.getString("create_date")
                    val updateDate = ingredientObject.getString("update_date")
                    val pizzaId = ingredientObject.getString("id_pizza")
                    ingredients.add(
                        Ingredient(
                            ingredientId,
                            shopId,
                            ingredientNameFr,
                            ingredientNameEn,
                            createDate,
                            updateDate,
                            pizzaId
                        )
                    )
                }

                val pricesArray = itemObject.getJSONArray("prices")
                val prices = mutableListOf<Price>()
                for (k in 0 until pricesArray.length()) {
                    val priceObject = pricesArray.getJSONObject(k)
                    val priceId = priceObject.getString("id")
                    val pizzaId = priceObject.getString("id_pizza")
                    val sizeId = priceObject.getString("id_size")
                    val price = priceObject.getString("price")
                    val createDate = priceObject.getString("create_date")
                    val updateDate = priceObject.getString("update_date")
                    val size = priceObject.getString("size")
                    prices.add(Price(priceId, pizzaId, sizeId, price, createDate, updateDate, size))
                }

                val menuItem = MenuItem(
                    id,
                    nameFr,
                    nameEn,
                    categoryId,
                    categoryNameFr,
                    categoryNameEn,
                    images,
                    ingredients,
                    prices
                )
                menuItems.add(menuItem)
            }
        }

        println("Menu Items: $menuItems")
        return menuItems
    }

}

@Composable
fun CallTopBar(name: String, context: Context) {
    val cartItemCount = remember { mutableStateOf(countCartItems(context)) }

    TopBar(
        onBackClicked = { context as ComponentActivity; context.finish() },
        onCartClicked = {
            val intent = Intent(context, CartActivity::class.java)
            context.startActivity(intent)
        },
        topBarName = "$name",
        cartItemCount = cartItemCount
    )
}

@Composable
fun CategoryScreen(name: String, items: List<MenuItem>, context: Context) {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = name,
            style = customTitleStyle,
            modifier = Modifier.padding(top = 50.dp)
        )
        Divider(
            modifier = Modifier.padding(horizontal = 90.dp, vertical = 8.dp),
            color = Color.Gray,
            thickness = 1.dp
        )
        val filteredItems = items.filter { it.categoryNameFr == name }
        filteredItems.forEach { item ->
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navigateToCourseActivity(context, item)
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberImagePainter(item.images.first()), // Load the first image from the list
                    contentDescription = null,
                    modifier = Modifier
                        .height(75.dp),
                    contentScale = ContentScale.FillWidth
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.nameFr,
                        style = customItemsStyle,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = item.prices.joinToString("€, ") { it.price } + "€",
                        style = customItemsStyle
                    )
                }
            }
        }
    }
}

fun countCartItems(context: Context): Int {
    val cartItems = readCartItems(context)
    return cartItems.sumBy { it.quantity }
}


fun navigateToCourseActivity(context: Context, item: MenuItem) {
    val intent = Intent(context, CourseActivity::class.java)
    intent.putExtra("itemName", item.nameFr)
    intent.putStringArrayListExtra("imageURLs", ArrayList(item.images))
    intent.putExtra("ingredients", item.ingredients.joinToString(", ") { it.nameFr })
    println(item.ingredients.joinToString(", ") { it.nameFr })

    // Extract prices as a list of strings
    val pricesList = item.prices.map { it.price }
    intent.putStringArrayListExtra("prices", ArrayList(pricesList))
    println(pricesList.joinToString(", ") { it })

    context.startActivity(intent)
}
