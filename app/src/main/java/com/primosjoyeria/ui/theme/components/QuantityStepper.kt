package com.primosjoyeria.ui.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun QuantityStepper(onInc: () -> Unit, onDec: () -> Unit, onRemove: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onInc) { Text("+") }
        Button(onClick = onDec) { Text("−") }
        OutlinedButton(onClick = onRemove) { Text("Quitar") }
    }
}
