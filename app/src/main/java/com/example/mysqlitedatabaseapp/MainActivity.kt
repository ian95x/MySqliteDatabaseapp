package com.example.mysqlitedatabaseapp

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mysqlitedatabaseapp.models.DBHandler
import com.example.mysqlitedatabaseapp.ui.theme.MySqliteDatabaseAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MySqliteDatabaseAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   HomeScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to our App",
            color = Color.Red,
            fontSize = 40.sp,
            fontFamily = FontFamily.Cursive
        )
        Spacer(modifier = Modifier.height(20.dp))

        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var idNumber by remember { mutableStateOf("") }

        OutlinedTextField(
            value = name,
            onValueChange = {name = it},
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            label = { Text(text = "Enter name")}
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            label = { Text(text = "Enter email")}
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = idNumber,
            onValueChange = {idNumber = it},
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            label = { Text(text = "Enter id number")}
        )
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val context = LocalContext.current
            var dbHandler = DBHandler(context)
            // Access the db from the handler
            var db = dbHandler.getDatabase()


            Button(onClick = {
                if (name.isEmpty() || email.isEmpty() || idNumber.isEmpty()){
                    messages("EMPTY FIELDS", "Please fill all inputs!!!", context)
                }else{
                    // Proceed to save the data
                    dbHandler.addNewUser(name, email, idNumber)
                    messages("SUCCESS!!!","User saved successfully", context)
                    db.close()
                }
            }) {
                Text(text = "Save")
            }
            Button(onClick = {
                // Select data from the database
                val data = db.rawQuery("SELECT * FROM users", null)
                if (data.count == 0){
                    messages("NO DATA!!","Sorry, there's no data", context)
                }else{
                    val buffer = StringBuffer()
                    while (data.moveToNext()){
                        buffer.append(data.getString(0)+"\n")
                        buffer.append(data.getString(1)+"\n")
                        buffer.append(data.getString(2)+"\n")
                        buffer.append(data.getString(3)+"\n\n")
                    }
                    messages("USERS!!!", buffer.toString(),context)
                    db.close()
                }
            }) {
                Text(text = "View")
            }
            Button(onClick = {
                if (idNumber.isEmpty()){
                    messages("EMPTY FIELD","Please enter id no", context)
                }else{
                    // Proceed to delete
                    var users = db.rawQuery("SELECT * FROM users WHERE id_no='"+idNumber+"'",null)
                    if (users.count == 0){
                        messages("NO USERS","Sorry, no users found", context)
                    }else{
                        // Finally delete the data
                        db.execSQL("DELETE FROM users WHERE id_no='"+idNumber+"'")
                        messages("SUCCESS","User deleted successfully",context)
                    }
                }
            }) {
                Text(text = "Delete")
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MySqliteDatabaseAppTheme {
        HomeScreen()
    }
}

fun messages(title:String, message: String, context:Context){
    var alertDialog = AlertDialog.Builder(context)
    alertDialog.setTitle(title)
    alertDialog.setMessage(message)
    alertDialog.create().show()
}