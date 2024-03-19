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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.bonnefon.androiderestaurant.ui.theme.AndroidERestaurantTheme

import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


val customItemsStyle = TextStyle(
    fontSize = 20.sp, // Adjust the size as needed
    // Add more style attributes if necessary
)

class CategoryActivity : ComponentActivity() {
    /*private lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        name = intent.getStringExtra("name") ?: ""
        val items = getItemsForCategory(name)
        println("CategoryActivity: $name, $items")
        setContent {
            AndroidERestaurantTheme {
                MenuCourses(name, items, this)
            }
        }
    }

    private fun getItemsForCategory(category: String): List<String> {
        return when (category) {
            getString(R.string.starters_title) -> resources.getStringArray(R.array.starters_items).toList()
            getString(R.string.main_courses_title) -> resources.getStringArray(R.array.main_courses_items).toList()
            getString(R.string.desserts_title) -> resources.getStringArray(R.array.desserts_items).toList()
            else -> emptyList()
        }
    }*/
    private lateinit var name: String
    private var items: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        name = intent.getStringExtra("name") ?: ""
        getMenuItems()
        setContent {
            CategoryScreen(name, items)
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
                items = parseMenuItems(response)
                setContent {
                    CategoryScreen(name, items)
                }
            },
            { error ->
                println("Error: $error")
            })

        requestQueue.add(jsonObjectRequest)
    }

    private fun parseMenuItems(response: JSONObject): List<String> {
        val items = mutableListOf<String>()
        val dataArray = response.getJSONArray("data")

        for (i in 0 until dataArray.length()) {
            val categoryObject = dataArray.getJSONObject(i)
            val categoryItems = categoryObject.getJSONArray("items")

            for (j in 0 until categoryItems.length()) {
                val itemObject = categoryItems.getJSONObject(j)
                val itemName = itemObject.getString("name_fr") // Assuming you want to use the French name
                items.add(itemName)
            }
        }
        println("Items: $items")
        return items
    }

}

/*@Composable
fun MenuCourses(name: String, items: List<String>, context: Context, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = name,
            style = customTitleStyle, // Using custom title style
            modifier = Modifier.padding(top = 50.dp)
        )
        Divider(modifier = Modifier.padding(horizontal = 90.dp, vertical = 8.dp), color = Color.Gray, thickness = 1.dp)
        items.forEach { item ->
            Text(
                text = item,
                style = customItemsStyle,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clickable {
                        navigateToCourseActivity(context, item)
                    }
            )
        }
    }
}*/

@Composable
fun CategoryScreen(name: String, items: List<String>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = name,
            style = customTitleStyle, // Using custom title style
            modifier = Modifier.padding(top = 50.dp)
        )
        items.forEach { item ->
            Text(
                text = item,
                style = customItemsStyle,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

// Function to navigate to CourseActivity
fun navigateToCourseActivity(context: Context, itemName: String) {
    val intent = Intent(context, CourseActivity::class.java)
    intent.putExtra("itemName", itemName)
    context.startActivity(intent)
}