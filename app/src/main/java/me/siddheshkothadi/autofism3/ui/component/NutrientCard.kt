package me.siddheshkothadi.autofism3.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Nutrient(text: String) {
    Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
fun ColorNutrient(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xff2fd053))
}

@Composable
fun NutrientCard(key: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .padding(horizontal = 0.dp, vertical = 6.dp)
            .border(1.dp, Color.Gray)
            .padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Nutrient(text = key)
        ColorNutrient(text = value)
    }
}