package me.siddheshkothadi.autofism3.ui.screen

import android.Manifest
import android.app.Activity
import android.graphics.Paint
import android.net.Uri
import android.util.TypedValue
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import me.siddheshkothadi.autofism3.R
import me.siddheshkothadi.autofism3.datastore.BitmapInfo
import me.siddheshkothadi.autofism3.ui.component.MapView
import me.siddheshkothadi.autofism3.ui.component.NutrientCard
import me.siddheshkothadi.autofism3.ui.nav.Screen
import me.siddheshkothadi.autofism3.ui.viewmodel.EnterDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EnterDetails(
    navController: NavHostController,
    enterDetailsViewModel: EnterDetailsViewModel,
    fishImageUri: String,
    activityContext: Activity
) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    )

    LaunchedEffect(permissionsState) {
        enterDetailsViewModel.checkLocationAccess(activityContext)
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(fishImageUri) {
        enterDetailsViewModel.getBitmapValues(fishImageUri)
    }

    val bitmapInfo =
        enterDetailsViewModel.bitmapInfo.collectAsState(initial = BitmapInfo.getDefaultInstance())
    val boundingBoxes = enterDetailsViewModel.boundingBoxes.collectAsState(initial = listOf())

    val isLoading by remember { enterDetailsViewModel.isLoading }
    val quantity by remember { enterDetailsViewModel.quantity }
    val latitude by remember { enterDetailsViewModel.latitude }
    val longitude by remember { enterDetailsViewModel.longitude }
    val date by enterDetailsViewModel.dateString.collectAsState(initial = "Loading...")
    val time by enterDetailsViewModel.timeString.collectAsState(initial = "Loading...")
    val isConnectedToNetwork by remember { enterDetailsViewModel.isConnectedToNetwork }

    val loading by remember { enterDetailsViewModel.loading }

    var quantityError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val name by remember { enterDetailsViewModel.name }

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val selectedBox = remember { enterDetailsViewModel.selectedBox }
    var expanded by remember { mutableStateOf(false) }

    val temp by remember { enterDetailsViewModel.temp }
    val pressure by remember { enterDetailsViewModel.pressure }
    val humidity by remember { enterDetailsViewModel.humidity }
    val speed by remember { enterDetailsViewModel.speed }
    val deg by remember { enterDetailsViewModel.deg }
    val ph by remember {
        enterDetailsViewModel.ph
    }
    val moisture by remember {
        enterDetailsViewModel.moisture
    }
    val n by remember {
        enterDetailsViewModel.n
    }
    val p by remember {
        enterDetailsViewModel.p
    }
    val k by remember {
        enterDetailsViewModel.k
    }
    val soc by remember {
        enterDetailsViewModel.soc
    }

    val paintConfig = remember {
        Paint().apply {
            color = android.graphics.Color.BLUE
            strokeWidth = 7.0f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            strokeMiter = 100f
        }
    }

    val redPaintConfig = remember {
        Paint().apply {
            color = android.graphics.Color.RED
            strokeWidth = 10.0f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            strokeMiter = 100f
        }
    }

    val textSizePx = remember {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            15f,
            context.resources.displayMetrics
        )
    }

    val interiorPaint = remember {
        Paint().apply {
            textSize = textSizePx
            color = android.graphics.Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = false
            alpha = 255
        }
    }

    val exteriorPaint = remember {
        Paint().apply {
            textSize = textSizePx
            color = android.graphics.Color.BLUE
            style = Paint.Style.FILL
            isAntiAlias = false
            alpha = 255
        }
    }

    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.statusBars.only(
                        WindowInsetsSides.Top
                    )
                )
            ) {
                SmallTopAppBar(
                    title = {
                        Text(
                            stringResource(id = Screen.EnterDetails.resourceId),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back Arrow"
                            )
                        }
                    }
                )
            }
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyRow(contentPadding = PaddingValues(12.dp)) {
                item {
                    AsyncImage(
                        model = Uri.parse(fishImageUri),
                        contentDescription = "Soil image",
                        modifier = Modifier
                            .width(300.dp)
                            .clip(RoundedCornerShape(12.dp))

                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(date, fontSize = 12.sp, fontWeight = FontWeight.Light)
                Text(time, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    stringResource(R.string.location_of_my_soil),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(12.dp))
                Icon(Icons.Filled.PinDrop, null, tint = Color.Red)
            }
            if (latitude.isNotBlank() && longitude.isNotBlank()) {
//                if(!isConnectedToNetwork) {
//                    Text(stringResource(R.string.map_view_may_not_render_properly), color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
//                    Spacer(Modifier.height(12.dp))
//                }
                MapView(
                    latitude, longitude,
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                if (isLoading || loading) {
                    CircularProgressIndicator(Modifier.size(20.dp))
                } else {
                    Text(
                        stringResource(id = R.string.location_not_found),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Text(
                "Name of the soil",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 18.dp)
                    .padding(bottom = 0.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = {
                    enterDetailsViewModel.updateName(it)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                label = {
                    Text(stringResource(R.string.name), color = Color.Gray)
                },
                isError = quantityError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 18.dp)
            )

            if (!(isLoading || loading)) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    temp?.let {
                        NutrientCard(key = stringResource(R.string.temperature), value = "$it°C")
                    }
                    pressure?.let {
                        NutrientCard(key = stringResource(R.string.pressure), "$it hPa")
                    }
                    humidity?.let {
                        NutrientCard(key = stringResource(R.string.humidity), "$it%")
                    }
                    NutrientCard(key = stringResource(id = R.string.pH), value = "$ph")
                    NutrientCard(key = stringResource(id = R.string.moisture), value = "$moisture%")
                    NutrientCard(key = "N (kg/h-1)", value = "$n%")
                    NutrientCard(key = "P (kg/h-1)", value = "$p%")
                    NutrientCard(key = "K (kg/h-1)", value = "$k%")
                    NutrientCard(key = "SOC", value = "$soc%")
                }
            } else {
                CircularProgressIndicator()
            }
            Button(
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 24.dp, top = 12.dp),

                onClick = {
                    try {

                        if (name.isNotBlank()) {
                            coroutineScope.launch {
                                enterDetailsViewModel.enqueueDataUploadRequest(fishImageUri)
                                navController.navigate(Screen.History.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(Screen.Camera.route) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // re-selecting the same item
                                    launchSingleTop = true
                                    // Restore state when re-selecting a previously selected item
                                    restoreState = true
                                }
                            }
                        } else {
                            throw Exception()
                        }
                    } catch (e: Exception) {
                        quantityError = true

                        Toast.makeText(
                            context,
                            "Please enter a valid quantity",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }) {
                Text(stringResource(R.string.submit))
            }
        }
    }
}