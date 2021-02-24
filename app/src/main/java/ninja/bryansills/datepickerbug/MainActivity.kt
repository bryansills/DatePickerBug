package ninja.bryansills.datepickerbug

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.DatePicker
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var date by rememberSaveable { mutableStateOf(Date()) }
            var showDialog by rememberSaveable { mutableStateOf(false) }

            if (showDialog) {
                DatePickerDialog(
                    currentDate = date,
                    onSaveDate = {
                        date = it
                        showDialog = false
                    },
                    onDismiss = { showDialog = false }
                )
            }

            Column(modifier = Modifier.fillMaxSize()) {
                Text("Here is the bug")
                Button(onClick = { showDialog = true }) {
                    Text(text = date.toString())
                }
            }
        }
    }
}

@Composable
fun DatePickerDialog(
    currentDate: Date,
    onSaveDate: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by rememberSaveable {
        mutableStateOf(
            currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column {
                    AndroidView(
                        viewBlock = {
                            val datePicker = DatePicker(it)
                            datePicker.init(
                                selectedDate.year,
                                selectedDate.monthValue,
                                selectedDate.dayOfMonth
                            ) { _, year, monthOfYear, dayOfMonth ->
                                selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                            }
                            datePicker
                        },
                        update = {
                            it.updateDate(
                                selectedDate.year,
                                selectedDate.monthValue - 1,
                                selectedDate.dayOfMonth
                            )
                        }
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            shape = RectangleShape,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text("Cancel")
                        }
                        TextButton(
                            onClick = {
                                val newDate = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                                onSaveDate(newDate)
                            },
                            shape = RectangleShape,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    )
}

