package me.siddheshkothadi.autofism3.ui.screen

import android.net.Uri
import me.siddheshkothadi.autofism3.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import me.siddheshkothadi.autofism3.ui.component.AppBar
import me.siddheshkothadi.autofism3.ui.component.NutrientCard
import me.siddheshkothadi.autofism3.ui.viewmodel.AnalysisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AnalysisReport(
    analysisViewModel: AnalysisViewModel,
    navController: NavHostController,
    uri: String
) {
    val uploadHistoryFish = remember { analysisViewModel.uploadHistoryFish }
    val img = painterResource(id = R.drawable.img)
    val image = painterResource(id = R.drawable.soil_image)
    val save_image = painterResource(id = R.drawable.save)

    LaunchedEffect(uri) {
        analysisViewModel.getByImageUri(uri)
    }

    Scaffold(
        topBar = {
            AppBar(title = "Analysis Report")
        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it), contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = uploadHistoryFish.value?.name ?: "",
                        style = TextStyle(fontWeight = FontWeight.Bold, letterSpacing = 2.sp),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 5.dp)
                    )
                }
                uploadHistoryFish.value?.let { fish ->
                    AsyncImage(
                        model = "http://172.16.5.143:5000/${fish.image_url}", contentDescription = "soil image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(start = 20.dp, end = 20.dp)
                            .clip(RoundedCornerShape(12.dp))

                    )
                }

                Spacer(modifier = Modifier.padding(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Greater Noida, 24 November", fontSize = 12.sp)
                    Spacer(Modifier.weight(1f))
                    Text("${uploadHistoryFish.value?.temp} °C", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.padding(4.dp))

                Row(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column(
                        modifier = Modifier
                            // .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            // modifier = Modifier.fillMaxSize(0.5f),
                            textAlign = TextAlign.Left,
                            text = "PH Level",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            //modifier = Modifier.fillMaxSize(0.4f),
                            textAlign = TextAlign.Left,
                            text = "High",
                            color = Color.Red,
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                    //Spacer(Modifier.weight(1f))

                    Box(
                        modifier = Modifier.fillMaxHeight(),
                    ) {
                        Text(
                            //modifier = Modifier.fillMaxSize(0.5f),
                            textAlign = TextAlign.Left,
                            text = uploadHistoryFish.value?.ph.toString(),
                            fontSize = 18.sp,
                            //style = TextStyle(fontWeight = FontWeight.Bold),
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))

                uploadHistoryFish.value?.predictions?.soilType?.type.let { it1 ->
                    NutrientCard(
                        key = "Soil Type",
                        value = it1.toString()
                    )
                }

                Row(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column(
                        modifier = Modifier
                            // .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            // modifier = Modifier.fillMaxSize(0.5f),
                            textAlign = TextAlign.Left,
                            text = "Moisture Content",
                            style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                    //Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                    ) {
                        Text(
                            //modifier = Modifier.fillMaxSize(0.5f),
                            textAlign = TextAlign.Left,
                            text = "${uploadHistoryFish.value?.moisture}%",
                            fontSize = 18.sp,
                            //style = TextStyle(fontWeight = FontWeight.Bold),
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))

                Column(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {

                    Text(
                        //modifier = Modifier.fillMaxSize(0.5f),
                        textAlign = TextAlign.Left,
                        text = "Npk Value",
                        fontSize = 18.sp,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Row(
                        modifier = Modifier
                            // .fillMaxWidth()
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            // modifier = Modifier.fillMaxSize(0.5f),
                            textAlign = TextAlign.Left,
                            text = "Nitrogen (kg/h-1)",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            //modifier = Modifier.fillMaxSize(0.4f),
                            textAlign = TextAlign.Left,
                            text = uploadHistoryFish.value?.nitrogen.toString(),
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        )
                    }


                    Spacer(modifier = Modifier.padding(4.dp))

                    Row(
                        modifier = Modifier
                            // .fillMaxWidth()
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            // modifier = Modifier.fillMaxSize(0.5f),
                            textAlign = TextAlign.Left,
                            text = "Phosphorus (kg/h-1)",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            //modifier = Modifier.fillMaxSize(0.4f),
                            textAlign = TextAlign.Left,
                            text = uploadHistoryFish.value?.phosphorus.toString(),
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        )
                    }


                    Spacer(modifier = Modifier.padding(4.dp))

                    Row(
                        modifier = Modifier
                            // .fillMaxWidth()
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            // modifier = Modifier.fillMaxSize(0.5f),
                            textAlign = TextAlign.Left,
                            text = "Potassium (kg/h-1)",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            //modifier = Modifier.fillMaxSize(0.4f),
                            textAlign = TextAlign.Left,
                            text = uploadHistoryFish.value?.potassium.toString(),
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        )
                    }

                }



                Spacer(modifier = Modifier.padding(4.dp))
                NutrientCard(key = "SOC", value = "${uploadHistoryFish.value?.soc}%")
                NutrientCard(key = "Humidity", value = "${uploadHistoryFish.value?.humidity}%")
                NutrientCard(key = "Pressure", value = "1013 hPa")
            }
        }
    }
}